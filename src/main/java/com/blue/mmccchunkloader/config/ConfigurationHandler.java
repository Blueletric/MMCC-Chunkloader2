package com.blue.mmccchunkloader.config;

import com.blue.mmccchunkloader.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;



public class ConfigurationHandler
{
    public static Configuration configuration;

    public static int chunkLoaderSize = 5;
    public static int playerInactivityTimeout = 0;

    public static void Init(File aConfigFile)
    {
        if(configuration == null)
        {
            configuration = new Configuration(aConfigFile);
            loadConfiguration();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(Reference.MOD_ID.equalsIgnoreCase(event.getModID()))
        {
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        chunkLoaderSize = configuration.getInt("ChunkLoaderSize", Configuration.CATEGORY_GENERAL, 5, 1, 32,"Number of chunks square to load around the chunk loader. Should be an odd number.");
        playerInactivityTimeout = configuration.getInt("PlayerInactivityTimeout", Configuration.CATEGORY_GENERAL, 0, 0, 100, "Number of hours after a player has been inactive before disabling the loader.\nUse 0 to disable feature.");
        if(configuration.hasChanged())
        {
            configuration.save();
        }
    }
}


