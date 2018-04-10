package com.blue.mmccchunkloader.commands;

import com.blue.mmccchunkloader.MMCCChunkloader;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;

public class UnloadChunkCommand extends CommandBase{
    @Override
    public String getName() {
        return "unloadchunk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Unloads the chunk the user is standing in.";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            //Attempts to retrieve the chunkload profile for this player in this dimension
            PlayerLoadedChunks loadedChunks = null;
            for (PlayerLoadedChunks chunks : MMCCChunkloader.playerChunkLoads) {
                if ((chunks.getOwnerId().equals(((EntityPlayer) sender).getUniqueID())) && (chunks.getDimension() == ((EntityPlayer) sender).dimension)) {
                    loadedChunks = chunks;
                    break;
                }
            }

            //If a profile does not exist, the player has no loaded chunks in this dimension
            if (loadedChunks == null) {
                TextComponentString noneInDim = new TextComponentString(MMCCChunkloader.chatPrefix + "You do not have any chunk loads in this dimension!");
                sender.sendMessage(noneInDim);
                return;
            }

            //If a profile does exist, a check is performed to make sure this player actually loaded this chunk
            ChunkPos pos = sender.getEntityWorld().getChunkFromBlockCoords(sender.getPosition()).getPos();
            if (!loadedChunks.loadedChunks.contains(pos)) {
                TextComponentString notLoaded = new TextComponentString(MMCCChunkloader.chatPrefix + "You have not loaded this chunk!");
                sender.sendMessage(notLoaded);
                return;
            }

            //If the player did load the chunk, it is removed from the loaded list.  The profile is removed if it is emptied of chunkloads
            loadedChunks.loadedChunks.remove(pos);
            if (loadedChunks.loadedChunks.isEmpty()) {
                MMCCChunkloader.playerChunkLoads.remove(loadedChunks);
            }

            TextComponentString unloaded = new TextComponentString(MMCCChunkloader.chatPrefix + "The current chunk will no longer be loaded");
            sender.sendMessage(unloaded);
        }else{
            TextComponentString notPlayer = new TextComponentString(MMCCChunkloader.chatPrefix + "You must have an in-game presence to load chunks!");
            sender.sendMessage(notPlayer);
        }
    }
}
