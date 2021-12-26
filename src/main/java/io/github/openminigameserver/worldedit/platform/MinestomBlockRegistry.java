package io.github.openminigameserver.worldedit.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockMaterial;
import com.sk89q.worldedit.world.registry.BundledBlockRegistry;
import com.sk89q.worldedit.world.registry.PassthroughBlockMaterial;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public final class MinestomBlockRegistry extends BundledBlockRegistry {
    private static final Map<String, Block> blockMap = new HashMap<>();
    private static final Map<String, BlockMaterial> blockMaterialMap = new HashMap<>();
    private static final Map<String, List<Block>> blockAlternativesMap = new HashMap<>();
    public static final MinestomBlockRegistry INSTANCE = new MinestomBlockRegistry();

    private MinestomBlockRegistry() {
    }

    static {
        for(Block block : Block.values()) {
            blockMap.put(block.name(), block);
        }
        for(short id=0; id<Short.MAX_VALUE; id++) {
            Block block = Block.fromStateId(id);

            if(block != null) {
                blockAlternativesMap.computeIfAbsent(block.name(), k->new ArrayList<>())
                        .add(block);
            }
        }
    }

    @Nullable
    @Override
    public BlockMaterial getMaterial(BlockType blockType) {
        Block block = blockMap.get(blockType.getId());
        if (block == null) return null;

        return blockMaterialMap.computeIfAbsent(blockType.getId(),
                id -> new MinestomBlockMaterial(super.getMaterial(blockType), block));
    }

    public static class MinestomBlockMaterial extends PassthroughBlockMaterial {
        private Block minestomBlock;

        public MinestomBlockMaterial(@Nullable BlockMaterial material, Block block) {
            super(material);
            this.minestomBlock = block;
        }

        @Override
        public boolean isAir() {
            return minestomBlock.isAir();
        }

        @Override
        public boolean isSolid() {
            return minestomBlock.isSolid();
        }

        @Override
        public boolean isLiquid() {
            return minestomBlock.isLiquid();
        }
    }

    @Override
    public OptionalInt getInternalBlockStateId(BlockState state) {
        Block block = blockMap.get(state.toString());
        if(block != null) {
            return OptionalInt.of(block.stateId());
        }

        block = blockMap.get(state.getBlockType().getId());
        if(block != null) {
            return OptionalInt.of(block.stateId());
        }

        for(Block alt : blockAlternativesMap.get(state.toString())) {
            Map<String, String> propMap = alt.properties();

            boolean matches = true;
            for(Entry<Property<?>, Object> entry : state.getStates().entrySet()) {
                Property<?> property = entry.getKey();

                String value = propMap.get(property.getName());
                if(value == null || !entry.getValue().toString().equals(value)) {
                    matches = false;
                    break;
                }
            }

            if(matches) {
                return OptionalInt.of(alt.stateId());
            }
        }

        return OptionalInt.empty();
    }

    @NotNull
    public Map<String, ? extends Property<?>> getProperties(@NotNull BlockType blockType) {
        String id = blockType.getId();

        if(!blockMap.containsKey(id)) {
            return Collections.emptyMap();
        }

        Block block = blockMap.get(id);

        List<Map<String, String>> propertyMapList = new ArrayList<>();

        propertyMapList.add(block.properties());
        for(Block alternative : blockAlternativesMap.get(block.name())) {
            propertyMapList.add(alternative.properties());
        }

        return createPropertyMap(propertyMapList);
    }

    private Map<String, ? extends Property<?>> createPropertyMap(List<Map<String, String>> propertyMapList) {
        Map<String, Set<String>> propertiesStringList = new HashMap<>();

        for(Map<String, String> map : propertyMapList) {
            for(Entry<String, String> entry : map.entrySet()) {
                propertiesStringList.computeIfAbsent(entry.getKey(), k -> new LinkedHashSet<>())
                        .add(entry.getValue());
            }
        }

        Map<String, Property<?>> properties = new HashMap<>();

        for(Entry<String, Set<String>> entry : propertiesStringList.entrySet()) {
            properties.put(entry.getKey(), createProperty(entry.getKey(), entry.getValue()));
        }

        return properties;
    }

    private Property<?> createProperty(String name, Set<String> values) {
        Property<?> property = createBooleanProperty(name, values);
        if(property != null) return property;

        property = createIntegerProperty(name, values);
        if(property != null) return property;

        return createEnumProperty(name, values);
    }

    private static final HashSet<String> booleanValues = Sets.newHashSet("true", "false");
    private BooleanProperty createBooleanProperty(String name, Set<String> values) {
        List<Boolean> booleans = new ArrayList<>();

        for(String string : values) {
            if(!booleanValues.contains(string)) return null;

            booleans.add(Boolean.valueOf(string));
        }

        return new BooleanProperty(name, booleans);
    }

    private IntegerProperty createIntegerProperty(String name, Set<String> values) {
        List<Integer> integers = new ArrayList<>();

        for(String string : values) {
            try {
                integers.add(Integer.valueOf(string));
            } catch(NumberFormatException exception) {
                return null;
            }
        }

        return new IntegerProperty(name, integers);
    }

    private EnumProperty createEnumProperty(String name, Set<String> values) {
        return new EnumProperty(name, Lists.newArrayList(values));
    }

}
