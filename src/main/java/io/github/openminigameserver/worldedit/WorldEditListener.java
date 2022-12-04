package io.github.openminigameserver.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.actors.MinestomPlayer;
import io.github.openminigameserver.worldedit.platform.adapters.MinestomAdapter;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;

public class WorldEditListener {

    public static void register(EventNode<Event> eventNode, MinestomPlatform platform, WorldEdit worldEdit) {
        eventNode.addListener(PlayerDisconnectEvent.class, (event) -> platform.removePlayer(event.getPlayer().getUuid()));

        eventNode.addListener(PlayerPluginMessageEvent.class, (event) -> {
            if (event.getIdentifier().equals("worldedit:cui")) {
                com.sk89q.worldedit.entity.Player player = (com.sk89q.worldedit.entity.Player) MinestomAdapter.INSTANCE.asActor(event.getPlayer());

                LocalSession session = worldEdit.getSessionManager().get(player);
                session.handleCUIInitializationMessage(event.getMessageString(), player);
            }
        });

        eventNode.addListener(PlayerBlockInteractEvent.class, (event) -> {
            MinestomPlayer minestomPlayer = platform.getPlayer(event.getPlayer());
            World world = MinestomAdapter.INSTANCE.asWorld(event.getInstance());
            Location location = MinestomAdapter.INSTANCE.asLocation(world, event.getBlockPosition());

            if (worldEdit.handleBlockRightClick(minestomPlayer, location, Direction.UP)) {
                event.setCancelled(true);
            }

            if (worldEdit.handleRightClick(minestomPlayer)) {
                event.setCancelled(true);
            }
        });

        eventNode.addListener(PlayerBlockBreakEvent.class, (event) -> {
            MinestomPlayer minestomPlayer = platform.getPlayer(event.getPlayer());
            World world = MinestomAdapter.INSTANCE.asWorld(event.getInstance());
            Location location = MinestomAdapter.INSTANCE.asLocation(world, event.getBlockPosition());

            if (worldEdit.handleBlockLeftClick(minestomPlayer, location, Direction.UP)) {
                event.setCancelled(true);
            }

            if (worldEdit.handleArmSwing(minestomPlayer)) {
                event.setCancelled(true);
            }
        });

        eventNode.addListener(PlayerUseItemEvent.class, (event) -> {
            MinestomPlayer minestomPlayer = platform.getPlayer(event.getPlayer());
            if (worldEdit.handleRightClick(minestomPlayer)) {
                event.setCancelled(true);
            }
        });

        eventNode.addListener(PlayerHandAnimationEvent.class, (event) -> {
            MinestomPlayer minestomPlayer = platform.getPlayer(event.getPlayer());
            if (worldEdit.handleArmSwing(minestomPlayer)) {
                event.setCancelled(true);
            }
        });
    }

}
