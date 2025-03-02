package com.yudream.yudreamaddons.common.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NetworkStatus {
    @Nonnull
    private final List<BlockPos> targetPos = new ArrayList<>();
    @Nonnull
    private UUID uuid = new UUID(0, 0);
    @Nonnull
    private UUID owner = new UUID(0, 0);
    @Nonnull
    private String networkName = "Unknown";
    private boolean isPublic = false;
    private int dimensionId = 0;
    @Nonnull
    private BlockPos pos = new BlockPos(0, 0, 0);

    private NetworkStatus() {
    }

    public NetworkStatus(@Nonnull UUID owner, @Nonnull String networkName, boolean isPublic, int dimensionId, @Nonnull BlockPos pos) {
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        this.networkName = networkName;
        this.isPublic = isPublic;
        this.dimensionId = dimensionId;
        this.pos = pos;
    }

    public static NetworkStatus readFromNBT(NBTTagCompound tag) {
        NetworkStatus networkStatus = new NetworkStatus();
        networkStatus.uuid = Objects.requireNonNull(tag.getUniqueId("u"));
        networkStatus.owner = Objects.requireNonNull(tag.getUniqueId("o"));
        networkStatus.networkName = tag.getString("n");
        networkStatus.isPublic = tag.getBoolean("i");
        networkStatus.dimensionId = tag.getInteger("d");
        networkStatus.pos = BlockPos.fromLong(tag.getLong("p"));

        NBTTagList list = tag.getTagList("tp", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound nbt = list.getCompoundTagAt(i);
            networkStatus.addTargetPos(BlockPos.fromLong(nbt.getLong("t")));
        }
        return networkStatus;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setUniqueId("u", this.uuid);
        tag.setUniqueId("o", this.owner);
        tag.setString("n", this.networkName);
        tag.setBoolean("i", this.isPublic);
        tag.setInteger("d", this.dimensionId);
        tag.setLong("p", this.pos.toLong());
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : targetPos) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setLong("t", pos.toLong());
            list.appendTag(nbt);
        }
        tag.setTag("tp", list);
        return tag;
    }

    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    @Nonnull
    public UUID getOwner() {
        return owner;
    }

    @Nonnull
    public String getNetworkName() {
        return networkName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    @Nonnull
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(@Nonnull BlockPos pos) {
        this.pos = pos;
    }

    @Nonnull
    public List<BlockPos> getTargetPos() {
        return targetPos;
    }

    public void addTargetPos(@Nonnull BlockPos pos) {
        targetPos.add(pos);
    }

    public void removeTargetPos(@Nonnull BlockPos pos) {
        targetPos.remove(pos);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NetworkStatus) {
            NetworkStatus e = (NetworkStatus) o;
            return e.getUuid().equals(this.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "NetworkStatus{" +
                "uuid=" + uuid +
                ", owner=" + owner +
                ", networkName='" + networkName + '\'' +
                ", isPublic=" + isPublic +
                ", dimensionId=" + dimensionId +
                ", pos=" + pos +
                '}';
    }
}
