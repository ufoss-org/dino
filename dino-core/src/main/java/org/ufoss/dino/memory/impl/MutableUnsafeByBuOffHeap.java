/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory.impl;

import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class MutableUnsafeByBuOffHeap extends MutableUnsafeOffHeap implements MutableByBuOffHeap {

    /**
     * Instantiate a readonly AbstractByteBufferOffHeap from a ByteBuffer
     */
    protected MutableUnsafeByBuOffHeap(@NotNull ByteBuffer bb) {
        super(Objects.requireNonNull(bb));
    }

    @Override
    public final long getByteSize() {
        return baseByBu.capacity();
    }

    @Override
    public final @NotNull MutableByBuOffHeap asByBuOffHeap() {
        return this;
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.baseByBu;
    }
}
