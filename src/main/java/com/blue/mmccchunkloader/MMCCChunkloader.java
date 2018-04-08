package com.blue.mmccchunkloader;

import com.blue.mmccchunkloader.commands.LoadChunkCommand;
import com.blue.mmccchunkloader.eventhandlers.PlayerEventListener;
import com.blue.mmccchunkloader.eventhandlers.WorldTickListener;
import com.blue.mmccchunkloader.storage.ModConfiguration;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(modid = MMCCChunkloader.MOD_ID, name = MMCCChunkloader.MOD_NAME, version = MMCCChunkloader.MOD_VERSION, serverSideOnly = true)
public class MMCCChunkloader
{
    public static final String MOD_ID = "mmccchunkloader";
    public static final String MOD_NAME = "MMCC Chunkloader";
    public static final String MOD_VERSION = "0.0.1";
    public static final String chatPrefix = "[Chunk-Loader] ";
    public static Configuration modConfiguration;
    //Used to store chunks loaded by each player per dimension
    public static HashMap<UUID, PlayerLoadedChunks> playerChunkLoads = new HashMap<>();
    //Used to quickly check for conflicts in chunk loads between two players
    public static HashMap<ChunkPos, UUID> loadedChunks = new HashMap<>();

    @Mod.Instance
    public static MMCCChunkloader instance;

    @NetworkCheckHandler
    public boolean checkClient(Map<String, String> map, Side side) {
        return true;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        modConfiguration = new Configuration(event.getSuggestedConfigurationFile());
        loadConfiguration();
        //Login/Logout tracking
        MinecraftForge.EVENT_BUS.register(new PlayerEventListener());
        MinecraftForge.EVENT_BUS.register(new WorldTickListener());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        handleDimensionWhitelist();
        modConfiguration.save();
        event.registerServerCommand(new LoadChunkCommand());
    }

    public void loadConfiguration() {
        modConfiguration.load();
        ModConfiguration.defaultMax = modConfiguration.get("Main", "max_player_chunks", 9).getInt();
        ModConfiguration.defaultPersistance = modConfiguration.get("Main", "offline_persistence", false).getBoolean();
        ModConfiguration.persistanceTimeout = modConfiguration.get("Main", "persistence_timeout", 120).getInt();
        ModConfiguration.defaultDimensionState = modConfiguration.get("Main", "default_dimension_allowed", false).getBoolean();
    }

    public void handleCommandPermissions() {
        //TODO Command permission config loading
    }

    public void handleDimensionWhitelist() {
        Integer[] ids = DimensionManager.getStaticDimensionIDs();
        for (int i=0; i<ids.length; i++) {
            DimensionType type = DimensionManager.getProviderType(ids[i]);
            String newId = type.getName() + type.getSuffix();
            ModConfiguration.dimensionWhitelist.put(newId, modConfiguration.get("Dimension_Whitelist", newId, ModConfiguration.defaultDimensionState).getBoolean());
        }
    }
}
