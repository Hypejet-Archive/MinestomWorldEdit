package io.github.openminigameserver.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.item.ItemType;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomPermissionProvider;
import io.github.openminigameserver.worldedit.platform.config.WorldEditConfiguration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.registry.Registries;
import net.minestom.server.utils.NamespaceID;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MinestomWorldEdit extends Extension {

    private static MinestomWorldEdit INSTANCE;
    private File dataFolder;
    private WorldEditConfiguration config;

    public static MinestomWorldEdit getInstance() {
        return INSTANCE;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public WorldEditConfiguration getConfig() {
        return config;
    }

    @Override
    public void initialize() {
        INSTANCE = this;

        dataFolder = new File(MinecraftServer.getExtensionManager().getExtensionFolder(), "WorldEdit");
        dataFolder.mkdirs();

        MinestomAdapter.platform = new MinestomPlatform(this);
        loadConfig();

        WorldEdit.getInstance().getPlatformManager().register(MinestomAdapter.platform);
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent(MinestomAdapter.platform));
        WorldEdit.getInstance().getEventBus().post(new PlatformsRegisteredEvent());

        registerBlocks();
        registerItems();

    }

    private void loadConfig() {
        File file = new File(dataFolder, "config.yml");
        config = new WorldEditConfiguration(
                /*YamlConfigurationLoader.builder()
                        .file(file)
                        .nodeStyle(NodeStyle.BLOCK).build(), getLogger()*/);
        config.load();
    }

    private void registerItems() {
        for (Material itemType : Material.values()) {
            String id = itemType.name();
            if (!ItemType.REGISTRY.keySet().contains(id)) {
                ItemType.REGISTRY.register(id, new ItemType(id));
            }
        }
    }

    private void registerBlocks() {
        for(Block minestomBlock : Block.values()) {
            String id = minestomBlock.name();
            if (!BlockType.REGISTRY.keySet().contains(id)) {
                BlockType block = new BlockType(id, null);

                for(BlockState state : block.getAllStates()) {
                    SortedMap<String, String> stateMap = new TreeMap<>();
                    for(Map.Entry<Property<?>, Object> entry : state.getStates().entrySet()) {
                        stateMap.put(entry.getKey().getName(), entry.getValue().toString());
                    }
                    /*String[] stateStrings = new String[stateMap.size()];

                    int i=0;
                    for(Map.Entry<String, String> entry : stateMap.entrySet()) {
                        stateStrings[i++] = entry.getKey() + "=" + entry.getValue();
                    }*/

                    short stateId = minestomBlock.withProperties(stateMap).stateId();
                    BlockStateIdAccess.register(state, stateId);
                }
                BlockType.REGISTRY.register(id, block);
            }
        }
    }

    @Override
    public void terminate() {
        WorldEdit.getInstance().getSessionManager().unload();
        WorldEdit.getInstance().getPlatformManager().unregister(MinestomAdapter.platform);
    }
}
