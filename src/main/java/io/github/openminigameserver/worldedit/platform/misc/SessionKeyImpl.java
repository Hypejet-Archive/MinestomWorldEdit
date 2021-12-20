package io.github.openminigameserver.worldedit.platform.misc;

import com.sk89q.worldedit.session.SessionKey;
import java.util.UUID;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;


public final class SessionKeyImpl implements SessionKey {
    private final UUID uuid;
    private final String name;

    public SessionKeyImpl(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if(player.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPersistent() {
        return true;
    }
}
