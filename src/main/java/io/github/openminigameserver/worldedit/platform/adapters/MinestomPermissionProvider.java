package io.github.openminigameserver.worldedit.platform.adapters;

import net.minestom.server.entity.Player;
import net.minestom.server.permission.PermissionHandler;
//import net.pixelravens.pixellib.permissions.PermissionManager;

import java.util.function.BiPredicate;

public class MinestomPermissionProvider {

    private static BiPredicate<Player, String> permissionHandler = PermissionHandler::hasPermission;

    public static void setPermissionHandler(BiPredicate<Player, String> permissionHandler) {
        MinestomPermissionProvider.permissionHandler = permissionHandler;
    }

    public static boolean hasPermission(Player player, String permission) {
        return permissionHandler.test(player, permission);
    }

}
