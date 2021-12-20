package io.github.openminigameserver.worldedit.platform.adapters;

import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.internal.wna.WorldNativeAccess;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import com.sk89q.worldedit.world.block.BlockState;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockChangePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public final class MinestomWorldNativeAccess implements WorldNativeAccess<Chunk, Short, Vec> {
    //private Batch currentBlockBatch;
    private final WeakReference<Instance> worldRef;
    //private final boolean useBlockBatch;

    /*private final BlockBatch newBlockBatch() {
        return !this.useBlockBatch ? null : this.getWorld()..createBlockBatch();
    }*/

    private final Instance getWorld() {
        Instance instance = this.worldRef.get();
        if (instance != null) {
            return instance;
        } else {
            throw new RuntimeException("World is unloaded");
        }
    }

    @Nullable
    public Chunk getChunk(int x, int z) {
        return this.getWorld().getChunk(x, z);
    }

    @NotNull
    public Short toNative(@NotNull BlockState state) {
        return (short)BlockStateIdAccess.getBlockStateId(state);
    }

    @NotNull
    public Short getBlockState(@NotNull Chunk chunk, @NotNull Vec position) {
        return chunk.getBlock(position.blockX(), position.blockY(), position.blockZ()).stateId();
    }

    @NotNull
    public Short setBlockState(@NotNull Chunk chunk, @NotNull Vec position, Short state) {
        if (false) {//this.useBlockBatch && this.currentBlockBatch != null) {
            //this.currentBlockBatch.setBlockStateId(position.toBlockPosition(), state);
        } else {
            // Cannot place block in a read-only chunk
            if (chunk.isReadOnly()) {
                return 0;
            }

            // Set the block
            getWorld().setBlock(
                    chunk.getChunkX()*16 + position.blockX(),
                    position.blockY(),
                    chunk.getChunkZ()*16 + position.blockZ(), Block.fromStateId(state));
        }

        return state;
    }

    @NotNull
    public Vec getPosition(int x, int y, int z) {
        return new Vec(x, y, z);
    }

    @NotNull
    public Short getValidBlockForPosition(Short block, @Nullable Vec position) {
        return block;
    }

    public void updateLightingForBlock(@Nullable Vec position) {
    }

    public boolean updateTileEntity(@Nullable Vec position, @Nullable CompoundBinaryTag tag) {
        return false;
    }

    public void notifyBlockUpdate(@Nullable Vec position, @Nullable Short oldState, @Nullable Short newState) {
    }

    public void notifyBlockUpdate(Chunk chunk, @Nullable Vec position, @Nullable Short oldState, @Nullable Short newState) {
    }

    public boolean isChunkTicking(@Nullable Chunk chunk) {
        return chunk != null;
    }

    public void markBlockChanged(@Nullable Vec position) {
    }

    public void markBlockChanged(Chunk chunk, @Nullable Vec position) {
    }

    public void notifyNeighbors(@Nullable Vec pos, @Nullable Short oldState, @Nullable Short newState) {
    }

    public void updateNeighbors(@Nullable Vec pos, @Nullable Short oldState, @Nullable Short newState, int recursionLimit) {
    }

    public void onBlockStateChange(@Nullable Vec pos, @Nullable Short oldState, @Nullable Short newState) {
    }

    public final void flush() {
        //if (this.currentBlockBatch != null) {
        //    this.currentBlockBatch.flush(() -> {});
        //}
    }

    public final boolean getUseBlockBatch() {
        return false;//this.useBlockBatch;
    }

    public MinestomWorldNativeAccess(@NotNull WeakReference worldRef, boolean useBlockBatch) {
        this.worldRef = worldRef;
        //this.useBlockBatch = useBlockBatch;
        //this.currentBlockBatch = this.newBlockBatch();
    }
}
