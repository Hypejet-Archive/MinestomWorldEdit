package io.github.openminigameserver.worldedit.platform.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.item.ItemType;
import io.github.openminigameserver.worldedit.platform.MinestomPlatform;
import io.github.openminigameserver.worldedit.platform.actors.MinestomConsole;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class MinestomAdapter {
    public static MinestomPlatform platform;
    public static final MinestomAdapter INSTANCE = new MinestomAdapter();

    private MinestomAdapter() {
    }

    @NotNull
    public MinestomPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(@NotNull MinestomPlatform var1) {
        platform = var1;
    }

    @NotNull
    public BlockVector3 asBlockVector(@NotNull Vec position) {
        return BlockVector3.at(position.blockX(), position.blockY(), position.blockZ());
    }

    public Component asComponent(com.sk89q.worldedit.util.formatting.text.Component component) {
        return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(GsonComponentSerializer.INSTANCE.serialize(component));
    }

    @NotNull
    public Vec asBlockPosition(@NotNull BlockVector3 position) {
        return new Vec(position.getX(), position.getY(), position.getZ());
    }

    @NotNull
    public Location asLocation(@NotNull World world, @NotNull Pos position) {
        return new Location(world, position.blockX(), position.blockY(), position.blockZ(), position.yaw(), position.pitch());
    }

    public Location asLocation(@NotNull World world, @NotNull Point position) {
        return new Location(world, position.blockX(), position.blockY(), position.blockZ(), 0, 0);
    }

    @NotNull
    public Direction asDirection(@NotNull BlockFace blockFace) {
        return Direction.valueOf(blockFace.toDirection().name());
    }

    @NotNull
    public Tag asTag(@NotNull NBT nbt) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(NBTWriter writer = new NBTWriter(baos)) {
            writer.writeNamed("value", nbt);
        }
        try(NBTInputStream nbtis = new NBTInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            return nbtis.readNamedTag().getTag();
        }
    }

    @NotNull
    public NBT asNBT(@NotNull Tag nbt) throws IOException, NBTException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(NBTOutputStream nbtos = new NBTOutputStream(baos)) {
            nbtos.writeNamedTag("value", nbt);
        }

        try(NBTReader reader = new NBTReader(new ByteArrayInputStream(baos.toByteArray()))) {
            return reader.read();
        }
    }

    @NotNull
    public BaseItemStack asBaseItemStack(@NotNull ItemStack item) throws IOException {
        return new BaseItemStack(ItemType.REGISTRY.get(item.material().name()), item.amount());
    }

    @NotNull
    public World asWorld(@NotNull Instance instance) {
        return platform.getWorld(instance);
    }

    @NotNull
    public Pos toPosition(@NotNull Location location) {
        return new Pos(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @NotNull
    public Actor asActor(@NotNull CommandSender commandSender) {
        if (commandSender instanceof Player) {
            return platform.getPlayer((Player)commandSender);
        } else {
            return MinestomConsole.INSTANCE;
        }
    }

    public Player asMinestomPlayer(@NotNull com.sk89q.worldedit.entity.Player player) {
        return MinecraftServer.getConnectionManager().getPlayer(player.getUniqueId());
    }

    @NotNull
    public ItemStack toItemStack(@NotNull BaseItemStack itemStack) throws IOException, NBTException {
        Material material = Material.fromNamespaceId(itemStack.getType().getId());

        if (itemStack.hasNbtData()) {
            return ItemStack.fromNBT(material, (NBTCompound)asNBT(itemStack.getNbtData()), itemStack.getAmount());
        } else {
            return ItemStack.of(material, itemStack.getAmount());
        }
    }

    @NotNull
    public Vec toPosition(@NotNull Vector3 location) {
        return new Vec(location.getX(), location.getY(), location.getZ());
    }

}
