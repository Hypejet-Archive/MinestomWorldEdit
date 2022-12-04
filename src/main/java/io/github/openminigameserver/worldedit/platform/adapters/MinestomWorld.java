package io.github.openminigameserver.worldedit.platform.adapters;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.util.TreeGenerator.TreeType;
import com.sk89q.worldedit.world.AbstractWorld;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public final class MinestomWorld extends AbstractWorld {
    private final WeakReference<Instance> worldRef;
    @NotNull
    private final MinestomWorldNativeAccess nativeAccess;

    public MinestomWorld(@NotNull Instance world) {
        this.worldRef = new WeakReference<>(world);
        this.nativeAccess = new MinestomWorldNativeAccess(this.worldRef, this.getWorld() instanceof InstanceContainer);
    }

    @NotNull
    public MinestomWorldNativeAccess getNativeAccess() {
        return this.nativeAccess;
    }

    @NotNull
    public Instance getWorld() {
        Instance world = this.worldRef.get();
        if (world != null) {
            return world;
        } else {
            throw new RuntimeException("The reference to the world was lost (i.e. the world may have been unloaded)");
        }
    }

    @Override
    public int getMinY() {
        DimensionType dimensionType = getWorld().getDimensionType();
        return dimensionType.getMinY();
    }

    @Override
    public int getMaxY() {
        DimensionType dimensionType = getWorld().getDimensionType();
        return dimensionType.getMinY() + dimensionType.getHeight();
    }

    @NotNull
    public Operation commit() {
        return (new Operation() {
            @Nullable
            public Operation resume(@Nullable RunContext run) {
                MinestomWorld.this.getNativeAccess().flush();
                return null;
            }

            public void cancel() {}
        });
    }

    public void checkLoadedChunk(@NotNull BlockVector3 pt) {
        int chunkX = ChunkUtils.getChunkCoordinate(pt.getX());
        int chunkZ = ChunkUtils.getChunkCoordinate(pt.getZ());
        if (this.getWorld().getChunk(chunkX, chunkZ) == null) {
            final CountDownLatch latch = new CountDownLatch(1);
            this.getWorld().loadChunk(chunkX, chunkZ).thenAccept(chunk -> latch.countDown());
            try {
                latch.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public BlockState getBlock(@NotNull BlockVector3 position) {
        this.checkLoadedChunk(position);
        short stateId = this.getWorld().getBlock(MinestomAdapter.INSTANCE.asBlockPosition(position)).stateId();
        return BlockStateIdAccess.getBlockStateById(stateId);
    }

    @NotNull
    public BaseBlock getFullBlock(@NotNull BlockVector3 position) {
        return this.getBlock(position).toBaseBlock();
    }

    public boolean setBlock(@Nullable BlockVector3 position, BlockStateHolder block, @Nullable SideEffectSet sideEffects) throws WorldEditException {
        return this.nativeAccess.setBlock(position, block, sideEffects);
    }

    @NotNull
    public List<Entity> getEntities(@Nullable Region region) {
        throw new RuntimeException("An operation is not implemented: " + "Not yet implemented");
    }

    @NotNull
    public List<Entity> getEntities() {
        throw new RuntimeException("An operation is not implemented: " + "Not yet implemented");
    }

    @Nullable
    public Entity createEntity(@Nullable Location location, @Nullable BaseEntity entity) {
        throw new RuntimeException("An operation is not implemented: " + "Not yet implemented");
    }

    @NotNull
    public String getId() {
        return this.getWorld().getUniqueId().toString();
    }

    @NotNull
    public String getName() {
        return this.getId();
    }

    @NotNull
    public Set<SideEffect> applySideEffects(@Nullable BlockVector3 position, @Nullable BlockState previousType, @Nullable SideEffectSet sideEffectSet) {
        return new LinkedHashSet<>();
    }

    public int getBlockLightLevel(@Nullable BlockVector3 position) {
        return 0;
    }

    public boolean clearContainerBlockContents(@Nullable BlockVector3 position) {
        return false;
    }

    public void dropItem(@NotNull Vector3 position, @NotNull BaseItemStack item) {
    }

    public void simulateBlockMine(@Nullable BlockVector3 position) {
    }

    public boolean generateTree(@Nullable TreeType type, @Nullable EditSession editSession, @Nullable BlockVector3 position) {
        return false;
    }

    @NotNull
    public BlockVector3 getSpawnPosition() {
        return MinestomAdapter.INSTANCE.asBlockVector(Vec.ZERO);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        MinestomWorld that = (MinestomWorld) o;
        return Objects.equals(this.worldRef.get(), that.worldRef.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.worldRef.get());
    }

}
