package com.yudream.yudreamaddons.common;

import com.yudream.yudreamaddons.common.block.BlockMEAspectInputBus;
import com.yudream.yudreamaddons.common.block.BlockMEAspectOutputBus;
import com.yudream.yudreamaddons.common.block.BlockNetworkHub;
import com.yudream.yudreamaddons.common.block.BlockShareInfHandler;
import com.yudream.yudreamaddons.common.item.LinkCard;
import net.minecraft.item.Item;


public class BlocksAndItems {
    //源质输入输出总线
    public static BlockMEAspectInputBus blockMEAspectInputBus;
    public static BlockMEAspectOutputBus blockMEAspectOutputBus;
    public static Item itemMEAspectInputBus;
    public static Item itemMEAspectOutputBus;

    public static BlockShareInfHandler blockShareInfHandler;
    public static Item itemShareInfHandler;

    public static LinkCard linkCard;

    public static BlockNetworkHub blockNetworkHub;
    public static Item itemNetworkHub;
}
