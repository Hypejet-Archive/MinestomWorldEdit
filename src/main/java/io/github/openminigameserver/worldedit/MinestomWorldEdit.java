package io.github.openminigameserver.worldedit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.event.platform.PlatformReadyEvent;
import com.sk89q.worldedit.event.platform.PlatformsRegisteredEvent;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.item.ItemType;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter;
import io.github.openminigameserver.worldedit.platform.config.WorldEditConfiguration;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

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
    public LoadStatus initialize() {
        INSTANCE = this;

        dataFolder = getDataFolder();
        dataFolder.mkdirs();

        MinestomAdapter.platform = new MinestomPlatform(this);
        loadConfig();

        WorldEdit.getInstance().getPlatformManager().register(MinestomAdapter.platform);
        WorldEdit.getInstance().getEventBus().post(new PlatformReadyEvent(MinestomAdapter.platform));
        WorldEdit.getInstance().getEventBus().post(new PlatformsRegisteredEvent());

        registerBlocks();
        registerItems();

        return LoadStatus.SUCCESS;
    }


    public static void applyBatch(Player player, Map<Vec, Short> blocks) {
        com.sk89q.worldedit.entity.Player wePlayer = MinestomAdapter.platform.getPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
        try (EditSession editSession = session.createEditSession(wePlayer)) {
            try {
                int affected = 0;

                for (Map.Entry<Vec, Short> entry : blocks.entrySet()) {
                    BlockVector3 vec = MinestomAdapter.INSTANCE.asBlockVector(entry.getKey());
                    BlockState state = BlockStateIdAccess.getBlockStateById(entry.getValue());

                    editSession.setBlock(vec, state);
                    affected++;
                }

                List<Component> messages = ImmutableList.of(TranslatableComponent.of(
                        "worldedit.operation.affected.block",
                        TextComponent.of(affected)
                ).color(TextColor.LIGHT_PURPLE));
                if (messages.isEmpty()) {
                    wePlayer.printInfo(TranslatableComponent.of("worldedit.set.done"));
                } else {
                    wePlayer.printInfo(TranslatableComponent.of("worldedit.set.done.verbose", TextUtils.join(messages, TextComponent.of(", "))));
                }
            } catch (MaxChangedBlocksException e) {
                wePlayer.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
            } finally {
                session.remember(editSession);
            }
        }
    }

    public static void applyUpdate(Player player, Function<Short, Short> blocks) {
        com.sk89q.worldedit.entity.Player wePlayer = MinestomAdapter.platform.getPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
        try (EditSession editSession = session.createEditSession(wePlayer)) {
            try {
                Region region = session.getSelection();

                RegionFunction set = position -> {
                    short oldBlockId = (short) BlockStateIdAccess.getBlockStateId(editSession.getBlock(position));
                    short blockId = blocks.apply(oldBlockId);

                    if (blockId != oldBlockId) {
                        return editSession.setBlock(position, BlockStateIdAccess.getBlockStateById(blockId));
                    }
                    return false;
                };
                RegionVisitor visitor = new RegionVisitor(region, set);

                Operations.completeBlindly(visitor);
                List<Component> messages = Lists.newArrayList(visitor.getStatusMessages());
                if (messages.isEmpty()) {
                    wePlayer.printInfo(TranslatableComponent.of("worldedit.set.done"));
                } else {
                    wePlayer.printInfo(TranslatableComponent.of("worldedit.set.done.verbose", TextUtils.join(messages, TextComponent.of(", "))));
                }
            } catch (IncompleteRegionException e) {
                wePlayer.printError(TranslatableComponent.of("worldedit.error.incomplete-region"));
            } finally {
                session.remember(editSession);
            }
        }
    }

    private void loadConfig() {
        File file = new File(dataFolder, "config.yml");
        config = new WorldEditConfiguration(
                /*YamlConfigurationLoader.builder()
                        .file(file)
                        .nodeStyle(NodeStyle.BLOCK).build(), getLogger()*/);
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
