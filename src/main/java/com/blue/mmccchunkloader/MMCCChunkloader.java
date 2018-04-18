package com.blue.mmccchunkloader;

import com.blue.mmccchunkloader.commands.LoadChunkCommand;
import com.blue.mmccchunkloader.commands.SaveChunksCommand;
import com.blue.mmccchunkloader.commands.UnloadChunkCommand;
import com.blue.mmccchunkloader.eventhandlers.PlayerEventListener;
import com.blue.mmccchunkloader.eventhandlers.WorldTickListener;
import com.blue.mmccchunkloader.storage.ModConfiguration;
import com.blue.mmccchunkloader.storage.PlayerLoadedChunks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    //The xml file holding chunk data
    private static File chunkData;
    //Used to store chunks loaded by each player per dimension
    public static ArrayList<PlayerLoadedChunks> playerChunkLoads = new ArrayList<>();

    @Mod.Instance
    public static MMCCChunkloader instance;

    @NetworkCheckHandler
    public boolean checkClient(Map<String, String> map, Side side) {
        return true;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        chunkData = new File(event.getModConfigurationDirectory(), "LoadedChunks.xml");
        if (chunkData.exists()) {
            loadChunksXml();
        }

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
        WorldTickListener.cache = event.getServer().getPlayerProfileCache();
        handleDimensionWhitelist();
        modConfiguration.save();
        event.registerServerCommand(new LoadChunkCommand());
        event.registerServerCommand(new UnloadChunkCommand());
        event.registerServerCommand(new SaveChunksCommand());
    }

    public static void loadChunksXml() {
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(chunkData);

            Element root = document.getElementById("ChunkList");

            for (int i=0; i<root.getChildNodes().getLength(); i++) {
                Node node = root.getChildNodes().item(i);
                if (node.getNodeName().equals("Chunk")) {
                    System.out.println("Loading chunk entry");
                    NamedNodeMap map = node.getAttributes();
                    Node uuid = map.getNamedItem("uuid");
                    if (uuid == null) {
                        System.out.println("UUID was null, skipping entry");
                        continue;
                    }
                    Node dimension = map.getNamedItem("dimension");
                    if (dimension == null) {
                        System.out.println("Dimension was null, skipping entry");
                        continue;
                    }
                    PlayerLoadedChunks loadedProfile = new PlayerLoadedChunks(UUID.fromString(uuid.getNodeValue()), Integer.valueOf(dimension.getNodeValue()), ModConfiguration.defaultMax, ModConfiguration.defaultPersistance, ModConfiguration.persistanceTimeout);

                    for (int j=0; j<node.getChildNodes().getLength(); j++) {
                        Node chunk = node.getChildNodes().item(j);
                        if (chunk != null) {
                            NamedNodeMap attributes = chunk.getAttributes();
                            Node xvalue = attributes.getNamedItem("xcoord");
                            Node zvalue = attributes.getNamedItem("zcoord");
                            if ((xvalue != null) && (zvalue != null)) {
                                ChunkPos pos = new ChunkPos(Integer.valueOf(xvalue.getNodeValue()), Integer.valueOf(zvalue.getNodeValue()));
                                loadedProfile.loadedChunks.add(pos);
                            }
                        }
                    }
                    playerChunkLoads.add(loadedProfile);
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveChunksXml() {
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.newDocument();

            Element root = document.createElement("ChunkList");

            for (PlayerLoadedChunks chunks : playerChunkLoads) {
                Element element = document.createElement("Player");
                element.setAttribute("uuid", chunks.getOwnerId().toString());
                element.setAttribute("dimension", Integer.toString(chunks.getDimension()));

                for (ChunkPos chunkPos : chunks.loadedChunks) {
                    Element chunk = document.createElement("Chunk");
                    chunk.setAttribute("xcoord", Integer.toString(chunkPos.x));
                    chunk.setAttribute("zcoord", Integer.toString(chunkPos.z));
                    element.appendChild(chunk);
                }
                root.appendChild(element);
            }
            document.appendChild(root);

            if (!chunkData.exists()) chunkData.createNewFile();
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult result = new StreamResult(chunkData);
            transformer.transform(domSource, result);
        } catch (ParserConfigurationException | IOException | TransformerException e) {
            e.printStackTrace();
        }
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

    public static void loadPlayerChunks(UUID playerId) {
        for (PlayerLoadedChunks loadedChunks : MMCCChunkloader.playerChunkLoads) {
            if ((loadedChunks != null) && (loadedChunks.getOwnerId().equals(playerId))) {
                WorldServer worldServer = DimensionManager.getWorld(loadedChunks.getDimension());
                loadedChunks.shouldBeLoaded = true;
                if (worldServer != null) {
                    System.out.println("Loading player chunks!");
                    for (ChunkPos chunkPos : loadedChunks.loadedChunks) {
                        worldServer.getChunkProvider().loadChunk(chunkPos.x, chunkPos.z);
                    }
                }else{
                    System.out.println("ERROR LOADING CHUNK, WORLD WAS NULL");
                    loadedChunks.shouldBeLoaded = false;
                }
            }
        }
    }

    public static void unloadPlayerChunks(UUID playerId) {
        for (PlayerLoadedChunks loadedChunks : MMCCChunkloader.playerChunkLoads) {
            if ((loadedChunks != null) && (loadedChunks.getOwnerId().equals(playerId))) {
                WorldServer worldServer = DimensionManager.getWorld(loadedChunks.getDimension());
                loadedChunks.shouldBeLoaded = false;
                if (worldServer != null) {
                    System.out.println("Unloading player chunks!");
                    for (ChunkPos chunkPos : loadedChunks.loadedChunks) {
                        Chunk chunk = worldServer.getChunkFromChunkCoords(chunkPos.x, chunkPos.z);
                        worldServer.getChunkProvider().queueUnload(chunk);
                    }
                }else{
                    System.out.println("ERROR UNLOADING CHUNK, WORLD WAS NULL");
                }
            }
        }
    }
}
