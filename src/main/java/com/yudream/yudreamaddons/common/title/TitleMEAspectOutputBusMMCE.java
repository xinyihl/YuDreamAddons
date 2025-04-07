package com.yudream.yudreamaddons.common.title;

import com.yudream.yudreamaddons.common.BlocksAndItems;
import com.yudream.yudreamaddons.common.title.base.TitleMEAspectBusMMCE;
import hellfirepvp.modularmachinery.common.machine.IOType;
import kport.modularmagic.common.tile.machinecomponent.MachineComponentAspectProvider;
import net.minecraft.item.ItemStack;

public class TitleMEAspectOutputBusMMCE extends TitleMEAspectBusMMCE {
    @Override
    public MachineComponentAspectProvider provideComponent() {
        return new MachineComponentAspectProvider(new MYAspectProviderCopy(this), IOType.OUTPUT);
    }

    @Override
    public ItemStack getVisualItemStack() {
        return new ItemStack(BlocksAndItems.itemMEAspectOutputBusMMCE);
    }
}
