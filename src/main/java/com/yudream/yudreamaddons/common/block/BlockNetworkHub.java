package com.yudream.yudreamaddons.common.block;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.event.GUIHandler;
import com.yudream.yudreamaddons.common.title.TileNetworkHub;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.yudream.yudreamaddons.common.registry.Registry.CREATIVE_TAB;

public class BlockNetworkHub extends Block {
    public static final PropertyBool CONNECT = PropertyBool.create("connect");

    public BlockNetworkHub() {
        super(Material.ROCK);
        this.setCreativeTab(CREATIVE_TAB);
        this.setRegistryName(new ResourceLocation(Tags.MOD_ID, "network_hub"));
        this.setTranslationKey(Tags.MOD_ID + ".network_hub");
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONNECT, false));
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CONNECT, meta == 1);  // 从元数据获取状态
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CONNECT) ? 1 : 0;  // 返回元数据表示方块是否工作
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECT);  // 允许方块具有工作状态属性
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, @Nonnull BlockPos pos) {
        boolean connect = false;
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileNetworkHub) {
            TileNetworkHub tn = (TileNetworkHub) te;
            connect = tn.isConnected();
        }
        return super.getActualState(state, worldIn, pos).withProperty(CONNECT, connect);
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && !playerIn.getHeldItem(hand).isEmpty()) {
            //NetworkHubDataStorage storage = NetworkHubDataStorage.get(worldIn);
            //storage.getAllNetworks().forEach(network -> {
            //    playerIn.sendMessage(new TextComponentString(network.getUuid().toString()));
            //});
            // 打开GUI
            playerIn.openGui(YuDreamAddons.instance, GUIHandler.GUI_NETWORK_HUB, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        if (!worldIn.isRemote && playerIn.getHeldItem(hand).isEmpty()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileNetworkHub) {
                TileNetworkHub tn = (TileNetworkHub) te;
                tn.sync();
                tn.test(playerIn, playerIn.isSneaking());
            }
        }

        if (worldIn.isRemote && playerIn.getHeldItem(hand).isEmpty()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileNetworkHub) {
                TileNetworkHub tn = (TileNetworkHub) te;
                //tn.test(playerIn, playerIn.isSneaking());
                playerIn.sendMessage(new TextComponentString("客户端NETUUID: " + tn.getNetworkUuid().toString()));
                playerIn.sendMessage(new TextComponentString("客户端保存的NET列表: "));
                tn.getNetworks().forEach(networkStatus -> playerIn.sendMessage(new TextComponentString(networkStatus.getUuid().toString())));
            }
        }
        return true;
    }

//    @Override
//    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
//        super.onBlockAdded(worldIn, pos, state);
//        if (worldIn.isRemote) return;
//
//        // 更新状态 - 假设根据某种条件判断是否工作
//
//
//
//        //worldIn.setBlockState(pos, state.withProperty(IS_WORKING, isWorking), 2);
//    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileNetworkHub && placer instanceof EntityPlayer) {
                TileNetworkHub tn = (TileNetworkHub) te;
                tn.setOwner((EntityPlayer) placer);
                tn.getActionableNode();
            }
        }
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState iBlockState) {
        return new TileNetworkHub();
    }
}
