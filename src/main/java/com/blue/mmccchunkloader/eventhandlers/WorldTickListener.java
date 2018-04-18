package com.blue.mmccchunkloader.eventhandlers;

import com.blue.mmccchunkloader.MMCCChunkloader;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class WorldTickListener {
    public static ArrayList<PlayerTimeout> playerTimeouts = new ArrayList<>();
    private static ArrayList<PlayerTimeout> completedTimeouts = new ArrayList<>();
    private static long lastMilliseconds = System.currentTimeMillis();
    public static PlayerProfileCache cache;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        //Exits method if the tick phase is not at the start, since unload requests and efforts to prevent chunk unloading should be made before each tick
        if (!(e.phase == TickEvent.Phase.START)) return;

        //Handles timeouts for chunk unloading after a player logs off
        long millisSinceLast = (System.currentTimeMillis() - lastMilliseconds);
        if (!playerTimeouts.isEmpty()) {
            for (PlayerTimeout timeout : playerTimeouts) {
                timeout.addTime(millisSinceLast);
                if (timeout.timeoutCompleted()) {
                    MMCCChunkloader.unloadPlayerChunks(timeout.getPlayerUUID());
                    completedTimeouts.add(timeout);
                    System.out.println("Timeout elapsed for " + cache.getProfileByUUID(timeout.getPlayerUUID()).getName());
                }
            }
            playerTimeouts.removeAll(completedTimeouts);
            completedTimeouts.clear();
        }

        //Clears the unload flag in chunks by calling getLoadedChunk. If the chunk is not loaded, this loads it
        for (PlayerLoadedChunks chunks : MMCCChunkloader.playerChunkLoads) {
            if (chunks.shouldBeLoaded) {
                WorldServer world = DimensionManager.getWorld(chunks.getDimension());
                for (ChunkPos pos : chunks.loadedChunks) {
                    Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.x, pos.z);
                    if ((chunk == null) || (!chunk.isLoaded())) {
                        world.getChunkProvider().loadChunk(pos.x, pos.z);
                    }
                }
            }
        }

        lastMilliseconds = System.currentTimeMillis();
    }
}
