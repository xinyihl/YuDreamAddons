package com.yudream.yudreamaddons.common.title;

import appeng.api.AEApi;
import appeng.api.exceptions.FailedConnectionException;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.core.AEConfig;
import appeng.me.cache.PathGridCache;
import com.yudream.yudreamaddons.Configurations;
import com.yudream.yudreamaddons.YuDreamAddons;
import com.yudream.yudreamaddons.common.BlocksAndItems;
import com.yudream.yudreamaddons.common.api.NetworkHubDataStorage;
import com.yudream.yudreamaddons.common.api.NetworkStatus;
import com.yudream.yudreamaddons.common.network.PacketServerToClient;
import com.yudream.yudreamaddons.common.title.base.TitleMeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.UUID;

import static com.yudream.yudreamaddons.common.network.PacketServerToClient.ServerToClient.DELETE_NETWORK;

public class TileNetworkHub extends TitleMeBase implements ITickable {
    private boolean isHead = false;
    private UUID networkUuid = new UUID(0, 0);
    private UUID owner = new UUID(0, 0);
    private boolean isConnected = false;
    //无需同步&保存
    private IGridConnection connection;
    private int tickCounter = 0;
    private int lastSurplusChannels;

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
        this.isConnected = this.connection != null;
    }

    @Override
    public void update() {
        if (this.world.isRemote) return;
        this.tickCounter = (this.tickCounter + 1) % 20;
        if (this.tickCounter % 20 == 0) {
            if (!this.networkUuid.equals(new UUID(0, 0))) {
                NetworkHubDataStorage storage = NetworkHubDataStorage.get(this.world);
                NetworkStatus network = storage.getNetwork(this.networkUuid);
                if (network == null) {
                    this.unsetAll();
                    return;
                }
                if (this.isHead) {
                    this.setConnected(!network.getTargetPos().isEmpty());
                    this.getProxy().setIdlePowerUsage(Configurations.OTHER_CONFIG.powerHeadBase * network.getTargetPos().size());
                    PathGridCache cache = this.getActionableNode().getGrid().getCache(IPathingGrid.class);
                    int surplusChannels = Math.max(AEConfig.instance().getDenseChannelCapacity() - cache.getChannelsInUse() + 2, 0); // 不知道为什么这玩意儿获取到的就是少2个频道
                    if (this.lastSurplusChannels != surplusChannels) {
                        this.lastSurplusChannels = surplusChannels;
                        network.setSurplusChannels(surplusChannels);
                        network.setNeedTellClient(true);
                    }
                } else {
                    if (this.getPos().equals(network.getPos())) {
                        this.setHead(true);
                    } else {
                        if (!this.isConnected) {
                            this.setupConnection(network);
                        }
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
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("isHead", this.isHead);
        tag.setUniqueId("networkUuid", this.networkUuid);
        tag.setUniqueId("owner", this.owner);
        tag.setBoolean("isConnected", this.isConnected);
        return tag;
    }

    public void setupConnection(NetworkStatus network) {
        if (this.world.isRemote) return;
        BlockPos pos = network.getPos();
        TileEntity tile = this.world.getTileEntity(pos);
        if (!(tile instanceof TileNetworkHub)) {
            NetworkHubDataStorage.get(this.world).removeNetwork(this.networkUuid);
            return;
        }
        TileNetworkHub that = (TileNetworkHub) tile;
        int dx = this.getPos().getX() - that.getPos().getX();
        int dy = this.getPos().getY() - that.getPos().getY();
        int dz = this.getPos().getZ() - that.getPos().getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double power = Configurations.OTHER_CONFIG.powerBase + Configurations.OTHER_CONFIG.powerDistanceMultiplier * dist * Math.log(dist * dist + 3);
        try {
            this.connection = AEApi.instance().grid().createGridConnection(this.getActionableNode(), that.getActionableNode());
            this.setConnected(true);
            this.getProxy().setIdlePowerUsage(power);
            network.addTargetPos(this.getPos());
        } catch (FailedConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void breakConnection() {
        if (this.world.isRemote) return;
        NetworkHubDataStorage storage = NetworkHubDataStorage.get(this.world);
        NetworkStatus network = storage.getNetwork(this.networkUuid);
        if (network == null) {
            unsetAll();
            return;
        }
        if (this.isHead) {
            for (BlockPos pos : network.getTargetPos()) {
                TileEntity tile = this.world.getTileEntity(pos);
                if (tile instanceof TileNetworkHub) {
                    ((TileNetworkHub) tile).breakConnection();
                }
            }
            storage.removeNetwork(this.networkUuid);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setUniqueId("networkUuid", this.networkUuid);
            YuDreamAddons.instance.networkWrapper.sendToAll(new PacketServerToClient(DELETE_NETWORK, tag));
        } else {
            network.removeTargetPos(this.getPos());
            this.getProxy().setIdlePowerUsage(1D);
        }
        storage.markDirty();
        this.unsetAll();
    }

    private void unsetAll() {
        this.setHead(false);
        this.setConnected(false);
        this.setNetworkUuid(new UUID(0, 0));
        if (this.connection != null) {
            this.connection.destroy();
            this.connection = null;
        }
        this.sync();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.breakConnection();
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public boolean isHead() {
        return this.isHead;
    }

    public void setHead(boolean head) {
        this.isHead = head;
    }

    public UUID getNetworkUuid() {
        return this.networkUuid;
    }

    public void setNetworkUuid(UUID networkUuid) {
        this.networkUuid = networkUuid;
    }
}
