package io.github.openminigameserver.worldedit.platform;

import com.sk89q.worldedit.world.registry.BlockRegistry;
import com.sk89q.worldedit.world.registry.BundledRegistries;
import io.github.openminigameserver.worldedit.platform.MinestomBlockRegistry;

public class MinestomRegistries extends BundledRegistries {

    @Override
    public BlockRegistry getBlockRegistry() {
        return MinestomBlockRegistry.INSTANCE;
    }

}
