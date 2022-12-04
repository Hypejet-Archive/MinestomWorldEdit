package io.github.openminigameserver.worldedit.platform.adapters;

import net.minestom.server.entity.Player;

public class MinestomPermissionProvider {
    public static boolean hasPermission(Player player, String permission) {
        if (player.hasPermission("*") || player.hasPermission("worldedit.*"))
            return true;
        return player.hasPermission(permission);
    }

}
