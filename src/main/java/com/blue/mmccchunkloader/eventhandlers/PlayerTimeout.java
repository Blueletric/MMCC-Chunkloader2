package com.blue.mmccchunkloader.eventhandlers;

import com.blue.mmccchunkloader.config.ConfigurationHandler;
import com.blue.mmccchunkloader.storage.ChunkLoaders;
import com.blue.mmccchunkloader.storage.SavedData;
import com.blue.mmccchunkloader.storage.ChunkLoaderPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;



public class PlayerTimeout {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e)
    {
        long timeout = ConfigurationHandler.playerInactivityTimeout;
        if (timeout == 0)
        {
            return;
        }
        timeout = timeout * 60 * 60 * 1000;

        if (!e.world.isRemote
                && e.world.getTotalWorldTime() % 1200 == 0)
        {
            SavedData data =  SavedData.get(e.world);
            if (data != null)
            {
                ChunkLoaders cl = data.getChunkloaders();
                for(ChunkLoaderPos loaderPos : cl.getLoaders())
                {
                    if (System.currentTimeMillis() - loaderPos.loginTimeStamp >= timeout
                            && !isPlayerOnline(e.world, loaderPos.ownerId))
                    {
                        System.out.println("Hello World");
                    }
                }
            }
        }
    }

    public boolean isPlayerOnline(World world, String ownerId)
    {
        boolean ret = false;
        for (EntityPlayer playerEntity : world.playerEntities)
        {
            if (playerEntity.getUniqueID().toString().equals(ownerId))
            {
                ret = false;
                break;
            }
        }
        return ret;
    }
}
