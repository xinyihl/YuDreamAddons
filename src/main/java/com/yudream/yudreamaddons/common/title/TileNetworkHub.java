package com.yudream.yudreamaddons.common.title;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import com.yudream.yudreamaddons.Configurations;
import com.yudream.yudreamaddons.common.BlocksAndItems;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.title.base.TitleMeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileNetworkHub extends TitleMeBase implements ITickable {
    @SideOnly(Side.CLIENT)
    private final List<NetworkStatus> networks = new ArrayList<>();
    private boolean isHead = false;
    private UUID networkUuid = new UUID(0, 0);
    private UUID owner = new UUID(0, 0);
    private boolean isConnected = false;
    private IGridConnection connection;
    private int tickCounter = 0;

    public TileNetworkHub() {
        super();
        this.proxy.setFlags(GridFlags.DENSE_CAPACITY);
    }

    @Override
    public ItemStack getVisualItemStack() {
        return new ItemStack(BlocksAndItems.itemNetworkHub);
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull AEPartLocation aePartLocation) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public void setOwner(EntityPlayer placer) {
        super.setOwner(placer);
        this.owner = placer.getGameProfile().getId();
    }

    @Override
    public void onLoad() {
        isConnected = connection != null;
    }

    @Override
    public void onChunkUnload() {
        breakConnection();
    }

    @Override
    public void update() {
        if (world.isRemote) return;
        this.tickCounter = (this.tickCounter + 1) % 20;
        if (this.tickCounter % 20 == 0 && !this.networkUuid.equals(new UUID(0, 0))) {
            NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
            NetworkStatus network = storage.getNetwork(owner, this.networkUuid);
            if (network == null) {
                unsetAll();
                return;
            }
            if (this.isHead) {
                this.setConnected(!network.getTargetPos().isEmpty());
                this.getProxy().setIdlePowerUsage(Configurations.OTHER_CONFIG.powerHeadBase * network.getTargetPos().size());
            } else {
                if (this.getPos().equals(network.getPos())) {
                    this.setHead(true);
                } else {
                    if (!this.isConnected) {
                        setupConnection(network);
                        storage.markDirty();
                    }
                }
            }
            this.sync();
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.isHead = tag.getBoolean("isHead");
        this.networkUuid = tag.getUniqueId("networkUuid");
        this.owner = tag.getUniqueId("owner");
        this.isConnected = tag.getBoolean("isConnected");
        if (world.isRemote) {
            this.networks.clear();
            NBTTagList list = tag.getTagList("networks", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbt = list.getCompoundTagAt(i);
                this.networks.add(NetworkStatus.readFromNBT(nbt));
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isHead", isHead);
        tag.setUniqueId("networkUuid", networkUuid);
        tag.setUniqueId("owner", owner);
        tag.setBoolean("isConnected", isConnected);
        if (!world.isRemote) {
            NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
            List<NetworkStatus> networks = storage.getAllNetworks(this.owner);
            NBTTagList list = new NBTTagList();
            for (NetworkStatus network : networks) {
                NBTTagCompound nbt = new NBTTagCompound();
                list.appendTag(network.writeToNBT(nbt));
            }
            tag.setTag("networks", list);
        }
        return tag;
    }

    public void setupConnection(NetworkStatus network) {
        if (world.isRemote) return;
        BlockPos pos = network.getPos();
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileNetworkHub)) return;
        TileNetworkHub that = (TileNetworkHub) tile;
        int dx = this.getPos().getX() - that.getPos().getX();
        int dy = this.getPos().getY() - that.getPos().getY();
        int dz = this.getPos().getZ() - that.getPos().getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double power = Configurations.OTHER_CONFIG.powerBase + Configurations.OTHER_CONFIG.powerDistanceMultiplier * dist * Math.log(dist * dist + 3);
        try {
            connection = AEApi.instance().grid().createGridConnection(this.getActionableNode(), that.getActionableNode());
            this.setConnected(true);
            this.getProxy().setIdlePowerUsage(power);
            network.addTargetPos(this.getPos());
        } catch (FailedConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void breakConnection() {
        if (world.isRemote) return;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
        NetworkStatus network = storage.getNetwork(owner, this.networkUuid);
        if (network == null) {
            unsetAll();
            return;
        }
        if (isHead) {
            for (BlockPos pos : network.getTargetPos()) {
                TileEntity tile = world.getTileEntity(pos);
                if (!(tile instanceof TileNetworkHub)) continue;
                TileNetworkHub that = (TileNetworkHub) tile;
                that.breakConnection();
            }
            network.getTargetPos().clear();
            storage.markDirty();
            storage.removeNetwork(owner, networkUuid);
            this.networkUuid = new UUID(0, 0);
            this.isConnected = false;
        } else {
            network.removeTargetPos(this.getPos());
            this.getProxy().setIdlePowerUsage(1D);
            unsetAll();
        }
        this.sync();
    }

    private void unsetAll() {
        this.setHead(false);
        this.setConnected(false);
        this.setNetworkUuid(new UUID(0, 0));
        if (this.connection != null) {
            connection.destroy();
            connection = null;
        }
        this.sync();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (isConnected) {
            breakConnection();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public UUID getNetworkUuid() {
        return networkUuid;
    }

    public void setNetworkUuid(UUID networkUuid) {
        this.networkUuid = networkUuid;
    }

    @SideOnly(Side.CLIENT)
    public List<NetworkStatus> getNetworks() {
        return networks;
    }

    public void test(EntityPlayer player, boolean isHead) {
        if (world.isRemote) return;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(world);
        if (isHead) {
            storage.addNetwork(new NetworkStatus(player.getGameProfile().getId(), "TEST", true, world.provider.getDimension(), this.pos));
            storage.getAllNetworks().get(0).setPos(this.getPos());
            this.setHead(true);
        }
        this.setNetworkUuid(storage.getAllNetworks().get(0).getUuid());
        this.sync();
    }
}
