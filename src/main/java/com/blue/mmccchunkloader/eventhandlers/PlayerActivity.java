package com.blue.mmccchunkloader.eventhandlers;

import com.blue.mmccchunkloader.storage.ChunkLoaderPos;
import com.blue.mmccchunkloader.storage.ChunkLoaders;
import com.blue.mmccchunkloader.storage.SavedData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;



public class PlayerActivity
{
    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (!e.player.world.isRemote)
        {
            SavedData data = SavedData.get(e.player.world);
            if(data != null)
            {
                ChunkLoaders cl = data.getChunkloaders();
                cl.updateLoginTimeStamp(e.player.getUniqueID().toString(), System.currentTimeMillis());
                data.setChunkLoaders(cl);

                for(ChunkLoaderPos loaderPos : cl.getLoaders());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        if (!e.player.world.isRemote)
        {
            SavedData data = SavedData.get(e.player.world);
            if(data != null)
            {
                ChunkLoaders cl = data.getChunkloaders();
                cl.updateLoginTimeStamp(e.player.getUniqueID().toString(), System.currentTimeMillis());
                data.setChunkLoaders(cl);
            }
        }
    }
}
