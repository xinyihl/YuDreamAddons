package com.yudream.yudreamaddons;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = YuDreamAddons.MOD_ID, name = YuDreamAddons.NAME, version = YuDreamAddons.VERSION, dependencies = "required:mixinbooter")
public class YuDreamAddons {
    public static final String MOD_ID = "yudreamaddons";
    public static final String NAME = "YuDreamAddons";
    public static final String VERSION = "1.0.1";
    public static Logger logger;
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("YuDreamAddons initialized");
    }
}
