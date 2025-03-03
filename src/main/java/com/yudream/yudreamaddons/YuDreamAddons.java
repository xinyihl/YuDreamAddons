package com.yudream.yudreamaddons;

import com.yudream.yudreamaddons.common.network.PacketClientToServer;
import com.yudream.yudreamaddons.common.network.PacketServerToClient;
import com.yudream.yudreamaddons.common.network.PacketWandOops;
import com.yudream.yudreamaddons.common.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies =
        "required-after:configanytime@[2.0,);" +
                "required-after:mixinbooter@[8.0,);" +
                "required-after:immersiveengineering@[0.12-98,);" +
                "required-after:betterbuilderswands@[0.13.2,);" +
                "required-after:modularmachinery@[2.0,);" +
                "required-after:gugu-utils@[0.8,)"
)
public class YuDreamAddons {
    @Mod.Instance
    public static YuDreamAddons instance;
    @SidedProxy(clientSide = "com.yudream.yudreamaddons.common.proxy.ClientProxy", serverSide = "com.yudream.yudreamaddons.common.proxy.CommonProxy")
    public static CommonProxy PROXY;
    public SimpleNetworkWrapper networkWrapper;
    public File configDir;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);
        networkWrapper.registerMessage(PacketWandOops.class, PacketWandOops.class, 0, Side.SERVER);
        networkWrapper.registerMessage(PacketClientToServer.class, PacketClientToServer.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PacketServerToClient.class, PacketServerToClient.class, 2, Side.CLIENT);
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init();
    }
}
