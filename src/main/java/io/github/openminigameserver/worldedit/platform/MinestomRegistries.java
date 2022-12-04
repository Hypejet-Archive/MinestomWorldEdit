package io.github.openminigameserver.worldedit.platform;

import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledRegistries;

public class MinestomRegistries extends BundledRegistries {

    @Override
    public BlockRegistry getBlockRegistry() {
        return MinestomBlockRegistry.INSTANCE;
    }

}
