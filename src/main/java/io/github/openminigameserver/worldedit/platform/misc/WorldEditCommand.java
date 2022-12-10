package io.github.openminigameserver.worldedit.platform.misc;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.platform.CommandEvent;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.CommandUtil;
import io.github.openminigameserver.worldedit.MinestomWorldEdit;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.actors.MinestomPlayer;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.enginehub.piston.inject.InjectedValueStore;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.inject.MapBackedValueStore;
import org.jetbrains.annotations.NotNull;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomPermissionProvider;

import java.util.List;
import java.util.Optional;

public class WorldEditCommand extends Command {

    org.enginehub.piston.Command cmd;
    public WorldEditCommand(org.enginehub.piston.Command command) {
        super(command.getName(), toPrimitiveArray(command.getAliases()));
        cmd = command;

        setCondition((this::condition));

        ArgumentStringArray argument = ArgumentType.StringArray("args");
        argument.setSuggestionCallback((sender, context, suggestion) -> {
            CommandSuggestionEvent event = new CommandSuggestionEvent(MinestomAdapter.INSTANCE.asActor(sender), "/"+context.getInput());
            WorldEdit.getInstance().getEventBus().post(event);

            int lastSpace = context.getInput().lastIndexOf(" ");
            if(lastSpace > 0) {
                suggestion.setStart(lastSpace+2);
            }

            List<String> suggests = CommandUtil.fixSuggestions("/"+context.getInput(), event.getSuggestions());
            for(String suggest : suggests) {
                suggestion.addEntry(new SuggestionEntry(suggest));
            }
        });

        addSyntax((sender, cmd) -> {}, argument);
    }

    private boolean condition(final CommandSender sender, final String command) {
        if(sender instanceof ConsoleSender) return true;
        InjectedValueStore store = MapBackedValueStore.create();
        store.injectValue(Key.of(Actor.class), context ->
                Optional.of(new MinestomPlayer(MinestomAdapter.platform, (Player) sender)));
        return cmd.getCondition().satisfied(store);
    }

    @Override
    public void globalListener(@NotNull CommandSender sender, @NotNull CommandContext context, @NotNull String command) {
        if(sender instanceof Player player) {
            if(player.getInstance() != null) {
                player.getInstance().scheduleNextTick((instance) -> {
                    CommandEvent event = new CommandEvent(MinestomAdapter.INSTANCE.asActor(sender),
                            "/"+command.trim());
                    WorldEdit.getInstance().getEventBus().post(event);
                });
            }
            return;
        }
        CommandEvent event = new CommandEvent(MinestomAdapter.INSTANCE.asActor(sender),
                "/"+command.trim());
        WorldEdit.getInstance().getEventBus().post(event);
    }

    private static String[] toPrimitiveArray(List<String> list) {
        String[] arr = new String[list.size()];
        for(int i=0; i<arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}
