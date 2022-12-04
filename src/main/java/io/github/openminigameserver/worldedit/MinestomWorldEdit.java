package io.github.openminigameserver.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.item.ItemType;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter;
import io.github.openminigameserver.worldedit.platform.config.WorldEditConfiguration;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MinestomWorldEdit extends Extension {

    private static MinestomWorldEdit INSTANCE;
    private WorldEditConfiguration config;

    public static MinestomWorldEdit getInstance() {
        return INSTANCE;
    }

    public WorldEditConfiguration getConfig() {
        return config;
    }

    @Override
    public void initialize() {
        INSTANCE = this;

        File dataFolder = new File("config/extensions/WorldEdit");
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
        config = new WorldEditConfiguration();
        config.load();
        LocalSession.MAX_HISTORY_SIZE = 50; // Increase max history to 50
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
        for (Block minestomBlock : Block.values()) {
            String id = minestomBlock.name();
            if (!BlockType.REGISTRY.keySet().contains(id)) {
                BlockType block = new BlockType(id, null);

                for (BlockState state : block.getAllStates()) {
                    SortedMap<String, String> stateMap = new TreeMap<>();
                    for (Map.Entry<Property<?>, Object> entry : state.getStates().entrySet()) {
                        stateMap.put(entry.getKey().getName(), entry.getValue().toString());
                    }

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
