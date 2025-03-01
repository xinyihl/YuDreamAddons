package com.yudream.yudreamaddons.common.api;

import com.yudream.yudreamaddons.Tags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetworkHubDataStorage extends WorldSavedData {
    private static final String DATA_NAME = Tags.MOD_NAME + "_NetworkHubDataStorage";
    private final List<NetworkStatus> networks = new ArrayList<>();

    public NetworkHubDataStorage(String name) {
        super(name);
    }

    public static NetworkHubDataStorage get(World world) {
        NetworkHubDataStorage instance = null;
        if (world.getMapStorage() != null) {
            instance = (NetworkHubDataStorage) world.getMapStorage().getOrLoadData(NetworkHubDataStorage.class, DATA_NAME);
            if (instance == null) {
                instance = new NetworkHubDataStorage(DATA_NAME);
                world.getMapStorage().setData(DATA_NAME, instance);
            }
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        networks.clear();
        NBTTagList list = nbt.getTagList("networks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            networks.add(NetworkStatus.readFromNBT(tag));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (NetworkStatus network : networks) {
            NBTTagCompound tag = new NBTTagCompound();
            list.appendTag(network.writeToNBT(tag));
        }
        nbt.setTag("networks", list);
        return nbt;
    }

    public void addNetwork(NetworkStatus network) {
        networks.add(network);
        markDirty();
    }

    public void removeNetwork(EntityPlayer player, UUID netId) {
        removeNetwork(player.getGameProfile().getId(), netId);
    }

    public void removeNetwork(UUID player, UUID netId) {
        boolean removed = networks.removeIf(p -> p.getOwner().equals(player) && p.getUuid().equals(netId));
        if (removed) markDirty();
    }

    @Nullable
    public NetworkStatus getNetwork(EntityPlayer player, UUID netId) {
        return getNetwork(player.getGameProfile().getId(), netId);
    }

    @Nullable
    public NetworkStatus getNetwork(UUID player, UUID netId) {
        return networks.stream().filter(p -> {
            if (p.isPublic()) {
                return p.getUuid().equals(netId);
            } else {
                return p.getOwner().equals(player) && p.getUuid().equals(netId);
            }
        }).findFirst().orElse(null);
    }

    @Nullable
    public NetworkStatus getNetwork(EntityPlayer player, BlockPos pos, boolean isTarget) {
        return getNetwork(player.getGameProfile().getId(), pos, isTarget);
    }

    @Nullable
    public NetworkStatus getNetwork(UUID player, BlockPos pos, boolean isTarget) {
        if (isTarget) {
            return networks.stream().filter(p -> {
                if (p.isPublic()) {
                    return p.getTargetPos().contains(pos);
                } else {
                    return p.getOwner().equals(player) && p.getTargetPos().contains(pos);
                }
            }).findFirst().orElse(null);
        }
        return networks.stream().filter(p -> {
            if (p.isPublic()) {
                return pos.equals(p.getPos());
            } else {
                return p.getOwner().equals(player) && pos.equals(p.getPos());
            }
        }).findFirst().orElse(null);
    }

    @Nonnull
    public List<NetworkStatus> getAllNetworks() {
        return new ArrayList<>(networks);
    }

    @Nonnull
    public List<NetworkStatus> getAllNetworks(EntityPlayer player) {
        return getAllNetworks(player.getGameProfile().getId());
    }

    @Nonnull
    public List<NetworkStatus> getAllNetworks(UUID player) {
        return networks.stream().filter(p -> {
            if (p.isPublic()) {
                return true;
            } else {
                return p.getOwner().equals(player);
            }
        }).collect(Collectors.toList());
    }
}
