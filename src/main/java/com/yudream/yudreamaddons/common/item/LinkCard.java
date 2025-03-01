package com.yudream.yudreamaddons.common.item;

import com.yudream.yudreamaddons.Tags;
import com.yudream.yudreamaddons.common.title.TitleShareInfHandler;
import github.kasuminova.mmce.common.tile.MEPatternProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.yudream.yudreamaddons.common.registry.Registry.CREATIVE_TAB;

public class LinkCard extends Item {

    public LinkCard(){
        this.setCreativeTab(CREATIVE_TAB);
        this.setRegistryName(new ResourceLocation(Tags.MOD_ID,"link_card"));
        this.setTranslationKey(Tags.MOD_ID + ".link_card");
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(@Nonnull EntityPlayer player,@Nonnull World worldIn,@Nonnull BlockPos pos,@Nonnull EnumHand hand,@Nonnull EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote) return EnumActionResult.PASS;
        ItemStack itemStack = player.getHeldItem(hand);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(player.isSneaking()){
            if (!(tileEntity instanceof MEPatternProvider)){
                itemStack.setTagCompound(null);
                player.sendMessage(new TextComponentString("§a清除坐标成功！"));
            } else {
                NBTTagCompound nbtpos = new NBTTagCompound();
                nbtpos.setLong("link_card_pos", pos.toLong());
                itemStack.setTagCompound(nbtpos);
                player.sendMessage(new TextComponentString("§a保存坐标成功！"));
            }
        }else {
            if (!(tileEntity instanceof TitleShareInfHandler)){
                String zwf;
                // player.sendMessage(new TextComponentString("§c设置失败，目标不是库存共享总线！"));
            } else {
                NBTTagCompound nbtpos = itemStack.getTagCompound();
                if (nbtpos == null) {
                    player.sendMessage(new TextComponentString("§c设置失败，未保存坐标！"));
                } else {
                    BlockPos blockPos = BlockPos.fromLong(nbtpos.getLong("link_card_pos"));
                    ((TitleShareInfHandler) tileEntity).setBlockPos(player, blockPos);
                }
            }
        }

        return EnumActionResult.SUCCESS;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn,@Nonnull List<String> tooltip,@Nonnull ITooltipFlag flagIn)
    {
        NBTTagCompound nbtpos = stack.getTagCompound();
        if (nbtpos != null) {
            NBTTagCompound nbt = (NBTTagCompound) nbtpos.getTag("link_card_pos");
            if(nbt != null){
                tooltip.add("Pos: " + nbt.getInteger("x") + "/" + nbt.getInteger("y") + "/" + nbt.getInteger("z"));
                return;
            }
        }
        tooltip.add("Pos: 未保存坐标");
    }
}
