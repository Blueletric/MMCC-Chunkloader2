package com.blue.mmccchunkloader.eventhandlers;

import com.blue.mmccchunkloader.MMCCChunkloader;
import com.blue.mmccchunkloader.storage.ModConfiguration;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerEventListener
{
    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e)
    {
        System.out.println("Player logged in!");

        //Loads chunks for player, and if a timeout is in progress for them it is removed
        MMCCChunkloader.loadPlayerChunks(e.player.getUniqueID());
        for (int i=0; i<WorldTickListener.playerTimeouts.size(); i++) {
            PlayerTimeout timeout = WorldTickListener.playerTimeouts.get(i);
            if (timeout.getPlayerUUID().equals(e.player.getUniqueID())) {
                WorldTickListener.playerTimeouts.remove(i);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        System.out.println("Player logged out!");

        //If offline persistence is disabled, a timeout ticket is added to the tick listener for processing
        if (!ModConfiguration.defaultPersistance) {
            WorldTickListener.playerTimeouts.add(new PlayerTimeout(e.player.getUniqueID(), (ModConfiguration.persistanceTimeout * 1000)));
        }
    }
}
