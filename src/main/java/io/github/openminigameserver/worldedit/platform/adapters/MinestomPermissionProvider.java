package io.github.openminigameserver.worldedit.platform.adapters;

import net.minestom.server.entity.Player;
import org.hypejet.hype.permission.PermissionProvider;

public class MinestomPermissionProvider {

    public static PermissionProvider provider;

    public static void init() {
        provider = new PermissionProvider(4, "worldedit");
    }
    public static boolean hasPermission(Player player, String permission) {
        return provider.hasPermission(player, permission);
    }

    public static boolean hasWorldEditPermission(Player player) {
        return provider.hasPermission(player);
    }

}
