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
import hellfirepvp.modularmachinery.common.tiles.base.MachineComponentTile;
import hellfirepvp.modularmachinery.common.tiles.base.TileColorableMachineComponent;
import net.minecraft.entity.player.EntityPlayer;
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

public abstract class TitleMEAspectBus extends TileColorableMachineComponent implements MachineComponentTile, IThEGridHost, IActionHost, IPowerChannelState, IThEOwnable, IThEGridNodeBlock {
    protected ThEGridBlock gridBlock = new ThEGridBlock(this, this, true);
    protected IGridNode gridNode;
    protected ThEActionSource src = new ThEActionSource(this);
    protected EntityPlayer owner;
    protected boolean isPowered = false;
    protected boolean isActive = false;

    public TitleMEAspectBus() {
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

    public int takeAspectFromME(Aspect aspect, int i, boolean b) {
        try {
            IStorageGrid storage = GridUtil.getStorageGrid(this);
            IMEMonitor<IAEEssentiaStack> monitor = storage.getInventory(this.getChannel());
            IAEEssentiaStack canExtract = monitor.extractItems(AEUtil.getAEStackFromAspect(aspect, i), Actionable.SIMULATE, this.src);

            if (canExtract == null) {
                return 0;
            }

            if (canExtract.getStackSize() != i) {
                return (int) canExtract.getStackSize();
            }

            if (b) {
                monitor.extractItems(canExtract, Actionable.MODULATE, this.src);
            }

            this.markDirty();
            return (int) canExtract.getStackSize();
        } catch (GridAccessException e) {
            //Ignore
        }
        return 0;
    }
}
