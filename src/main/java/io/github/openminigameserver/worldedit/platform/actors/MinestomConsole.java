package io.github.openminigameserver.worldedit.platform.actors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.formatting.WorldEditText;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import io.github.openminigameserver.worldedit.platform.misc.SessionKeyImpl;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.ConsoleSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public final class MinestomConsole extends AbstractNonPlayerActor {
    private static final ConsoleSender consoleSender = MinecraftServer.getCommandManager().getConsoleSender();
    private static final UUID emptyUUID = new UUID(0L, 0L);
    private static final Gson gson = GsonComponentSerializer.populate(new GsonBuilder()).create();
    public static final MinestomConsole INSTANCE = new MinestomConsole();

    @NotNull
    public UUID getUniqueId() {
        return emptyUUID;
    }

    @NotNull
    public String[] getGroups() {
        return new String[0];
    }

    public void checkPermission(@Nullable String permission) {
    }

    public boolean hasPermission(@Nullable String permission) {
        return true;
    }

    @NotNull
    public SessionKey getSessionKey() {
        return new SessionKeyImpl(this.getUniqueId(), this.getName());
    }

    @NotNull
    public String getName() {
        return "CONSOLE";
    }

    public void printRaw(@NotNull String msg) {
        this.sendColorized(msg, TextColor.YELLOW);
    }

    public void printDebug(@NotNull String msg) {
        this.sendColorized(msg, TextColor.YELLOW);
    }

    public void print(@NotNull String msg) {
        this.sendColorized(msg, TextColor.WHITE);
    }

    @NotNull
    public Gson getGson() {
        return gson;
    }

    public void print(@NotNull Component component) {
        final Component newComponent = WorldEditText.format(component, this.getLocale());

        consoleSender.sendMessage(net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(
                MinestomConsole.INSTANCE.getGson().toJsonTree(newComponent).getAsJsonObject().toString())
        );
    }

    public void printError(@NotNull String msg) {
        this.sendColorized(msg, TextColor.RED);
    }

    private void sendColorized(String msg, TextColor formatting) {
        for(String line : msg.split("\n")) {
            print(TextComponent.of(line, formatting));
        }
    }

    @NotNull
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
