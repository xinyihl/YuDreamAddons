package com.yudream.yudreamaddons.common.title.base;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkBootingStatusChange;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import com.warmthdawn.mod.gugu_utils.modularmachenary.CommonMMTile;
import hellfirepvp.modularmachinery.common.tiles.base.MachineComponentTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.EssentiaStack;
import thaumicenergistics.api.IThELangKey;
import thaumicenergistics.api.ThEApi;
import thaumicenergistics.api.storage.IAEEssentiaStack;
import thaumicenergistics.api.storage.IEssentiaStorageChannel;
import thaumicenergistics.integration.appeng.AEEssentiaStack;
import thaumicenergistics.integration.appeng.grid.GridUtil;
import thaumicenergistics.integration.appeng.grid.IThEGridHost;
import thaumicenergistics.integration.appeng.grid.ThEGridBlock;
import thaumicenergistics.integration.appeng.util.ThEActionSource;
import thaumicenergistics.util.AEUtil;
import thaumicenergistics.util.ForgeUtil;
import thaumicenergistics.util.IThEGridNodeBlock;
import thaumicenergistics.util.IThEOwnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MEAspectBus extends CommonMMTile implements MachineComponentTile, IThEGridHost, IActionHost, IPowerChannelState, IThEOwnable, IThEGridNodeBlock {
    protected ThEGridBlock gridBlock = new ThEGridBlock(this, this, true);
    protected IGridNode gridNode;
    protected ThEActionSource src = new ThEActionSource(this);
    protected EntityPlayer owner;
    protected boolean isPowered = false;
    protected boolean isActive = false;

    public MEAspectBus() {
    }

    public ThEGridBlock getGridBlock() {
        return this.gridBlock;
    }

    public IGridNode getGridNode() {
        return this.gridNode;
    }

    public void invalidate() {
        if (this.gridNode != null) {
            this.gridNode.destroy();
            this.gridNode = null;
        }
        super.invalidate();
    }

    public void onChunkUnload() {
        super.onChunkUnload();
        this.invalidate();
    }

    @Nullable
    public IGridNode getGridNode(@Nonnull AEPartLocation aePartLocation) {
        return this.getActionableNode();
    }

    @Nonnull
    public AECableType getCableConnectionType(@Nonnull AEPartLocation aePartLocation) {
        return AECableType.SMART;
    }

    @Nonnull
    public IGridNode getActionableNode() {
        if (this.gridNode == null && ForgeUtil.isServer()) {
            this.gridNode = AEApi.instance().grid().createGridNode(this.getGridBlock());
            this.initGridNodeOwner();
            this.gridNode.updateState();
        }
        return this.gridNode;
    }

    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    protected IEssentiaStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEssentiaStorageChannel.class);
    }

    public void gridChanged() {
    }

    public EntityPlayer getOwner() {
        return this.owner;
    }

    public void setOwner(EntityPlayer player) {
        this.owner = player;
    }

    public void securityBreak() {
        this.getWorld().destroyBlock(this.getPos(), true);
    }

    public boolean isPowered() {
        return this.isPowered;
    }

    public boolean isActive() {
        if (!ForgeUtil.isServer()) {
            return this.isActive;
        } else {
            return this.gridNode != null && this.gridNode.isActive();
        }
    }

    @MENetworkEventSubscribe
    public final void updateBootStatus(MENetworkBootingStatusChange event) {
        this.markDirty();
    }

    @MENetworkEventSubscribe
    public void updatePowerStatus(MENetworkPowerStatusChange event) {
        try {
            this.isPowered = GridUtil.getEnergyGrid(this).isNetworkPowered();
            this.markDirty();
        } catch (GridAccessException var3) {
            this.isPowered = false;
        }
    }

    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        nbtTagCompound.setBoolean("powered", this.isPowered());
        nbtTagCompound.setBoolean("active", this.isActive());
        return nbtTagCompound;
    }

    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.isPowered = tag.getBoolean("powered");
        this.isActive = tag.getBoolean("active");
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 1, this.getUpdateTag());
    }

    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.handleUpdateTag(packet.getNbtCompound());
        this.readNBT(packet.getNbtCompound());
    }

    public void withPowerStateText(Consumer<String> consumer, Function<IThELangKey, String> localizationMapper) {
        if (this.isPowered()) {
            if (this.isActive()) {
                consumer.accept(localizationMapper.apply(ThEApi.instance().lang().deviceOnline()));
            } else {
                consumer.accept(localizationMapper.apply(ThEApi.instance().lang().deviceMissingChannel()));
            }
        } else {
            consumer.accept(localizationMapper.apply(ThEApi.instance().lang().deviceOffline()));
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world == null) return;
        IBlockState state = world.getBlockState(this.getPos());
        world.notifyBlockUpdate(this.getPos(), state, state, 3);
    }

    public int addAspectToME(Aspect aspect, int i, boolean b) {
        EssentiaStack inContainer = new EssentiaStack(aspect, i);
        AEEssentiaStack toInsert = AEEssentiaStack.fromEssentiaStack(inContainer);
        try {
            IStorageGrid storage = GridUtil.getStorageGrid(this);
            IMEMonitor<IAEEssentiaStack> monitor = storage.getInventory(this.getChannel());
            if (monitor.canAccept(toInsert)) {
                IAEEssentiaStack notInserted = monitor.injectItems(toInsert, Actionable.SIMULATE, this.src);
                if (notInserted != null && notInserted.getStackSize() > 0) {
                    toInsert.decStackSize(notInserted.getStackSize());
                }
                if (b) {
                    monitor.injectItems(toInsert, Actionable.MODULATE, this.src);
                }
            }
            this.markDirty();
            return (int) toInsert.getStackSize();
        } catch (GridAccessException e) {
            //Ignore
        }
        return 0;
    }

    public boolean takeAspectFromME(Aspect aspect, int i, boolean b) {
        try {
            IStorageGrid storage = GridUtil.getStorageGrid(this);
            IMEMonitor<IAEEssentiaStack> monitor = storage.getInventory(this.getChannel());
            IAEEssentiaStack canExtract = monitor.extractItems(AEUtil.getAEStackFromAspect(aspect, i), Actionable.SIMULATE, this.src);
            if (canExtract == null || canExtract.getStackSize() != i) {
                return false;
            }
            if (b) {
                monitor.extractItems(canExtract, Actionable.MODULATE, this.src);
            }
            this.markDirty();
            return true;
        } catch (GridAccessException e) {
            //Ignore
        }
        return false;
    }
}
