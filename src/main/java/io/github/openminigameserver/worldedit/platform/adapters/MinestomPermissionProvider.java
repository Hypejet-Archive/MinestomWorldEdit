package io.github.openminigameserver.worldedit.platform.adapters;

import me.window.permissions.PermissionProvider;
import net.minestom.server.entity.Player;

public class MinestomPermissionProvider {

    public static PermissionProvider provider;

    public static void init() {
        provider = new PermissionProvider(4, "worldedit");
    }
    public static boolean hasPermission(Player player, String permission) {
        return provider.hasExtensionPermission(player, permission);
    }

    public static boolean hasWorldEditPermission(Player player) {
        return provider.hasExtensionPermission(player);
    }

}
