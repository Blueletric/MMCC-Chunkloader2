package com.blue.mmccchunkloader.eventhandlers;

import com.blue.mmccchunkloader.MMCCChunkloader;
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
        PlayerLoadedChunks chunkLoads = MMCCChunkloader.playerChunkLoads.get(e.player.getUniqueID());
        if (chunkLoads != null) {
            WorldServer worldServer = DimensionManager.getWorld(chunkLoads.getDimension());
            if (worldServer != null) {
                System.out.println("Loading player chunks!");
                for (ChunkPos chunkPos : chunkLoads.loadedChunks) {
                    worldServer.getChunkProvider().loadChunk(chunkPos.x, chunkPos.z);
                }
            }else{
                System.out.println("ERROR LOADING CHUNK, WORLD WAS NULL");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        System.out.println("Player logged out!");
        PlayerLoadedChunks chunkLoads = MMCCChunkloader.playerChunkLoads.get(e.player.getUniqueID());
        if (chunkLoads != null) {
            WorldServer worldServer = DimensionManager.getWorld(chunkLoads.getDimension());
            if (worldServer != null) {
                System.out.println("Unloading player chunks!");
                for (ChunkPos chunkPos : chunkLoads.loadedChunks) {
                    Chunk chunk = worldServer.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
                    worldServer.getChunkProvider().queueUnload(chunk);
                }
            }else{
                System.out.println("ERROR UNLOADING CHUNK, WORLD WAS NULL");
            }
        }
    }
}
