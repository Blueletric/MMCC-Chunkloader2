package com.blue.mmccchunkloader;

import com.blue.mmccchunkloader.CommonProxy;
import com.blue.mmccchunkloader.commands.CommandStats;
import com.blue.mmccchunkloader.storage.ChunkLoaders;
import com.blue.mmccchunkloader.storage.SavedData;
import com.blue.mmccchunkloader.storage.ChunkLoaderPos;
import com.blue.mmccchunkloader.config.ConfigurationHandler;
import com.blue.mmccchunkloader.eventhandlers.PlayerActivity;
import com.blue.mmccchunkloader.eventhandlers.PlayerTimeout;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import java.awt.*;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION)
public class MMCCChunkloader
{
    @Mod.Instance
    public static MMCCChunkloader instance;

    @SidedProxy(serverSide = Reference.SERVER_SIDE_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //config
        ConfigurationHandler.Init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());

        //Login/Logout tracking
        MinecraftForge.EVENT_BUS.register(new PlayerActivity());
        MinecraftForge.EVENT_BUS.register(new PlayerTimeout());

        //Keybinds
        proxy.registerKeys();
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent e)
    {
        World world;
        world = DimensionManager.getWorld(DimensionType.OVERWORLD.getId());
        SavedData data = SavedData.get(world);
        ChunkLoaders chunkLoadersModel = data.getChunkloaders();

        for (ChunkLoaderPos chunkLoaderPos : chunkLoadersModel.getLoaders())
        {
            world = DimensionManager.getWorld(chunkLoaderPos.dimension);
            if (world != null && !world.isRemote)
            {
                System.out.println("HAHA MMCC HAS CHUNKLOADING FUCKERS");
            }
        }
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStats());
    }
}
