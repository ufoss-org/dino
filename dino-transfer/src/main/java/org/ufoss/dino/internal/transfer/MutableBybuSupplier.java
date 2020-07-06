/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A pool of {@link MutableByBuOffHeap}, that allows to recycle and supply memory when needed
 */
public interface MutableBybuSupplier extends Supplier<MutableByBuOffHeap> {

    /**
     * @return a {@link MutableByBuOffHeap} from the pool or create new one
     */
    @Override
    @NotNull MutableByBuOffHeap get();

    /**
     * Returns a {@link MutableByBuOffHeap} to the pool.
     *
     * @param memory This must be a byte sequence previously obtained
     *               by calling MemorySupplier::get. The caller must
     *               not touch the bytes after returning it to
     *               the pool.
     */
    @ApiStatus.Experimental
    void recycle(@NotNull MutableByBuOffHeap memory);
}
