/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory.impl;

import org.ufoss.dino.internal.utils.UnsafeByteBufferOps;
import org.ufoss.dino.memory.ByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Base abstract implementation of {@link ByBuOffHeap} memory
 */
public abstract class SafeByBuOffHeap extends AbstractByteBufferOffHeap implements ByBuOffHeap {

    protected SafeByBuOffHeap(ByteBuffer bb, byte[] bytes) {
        this(UnsafeByteBufferOps.safeFillWithByteArray(bb, 0, bytes, 0, bytes.length));
    }

    /**
     * Instantiate a readonly AbstractByBuOffHeap from a ByteBuffer
     */
    protected SafeByBuOffHeap(@NotNull ByteBuffer bb) {
        super(Objects.requireNonNull(bb), true, UnsafeByteBufferOps.SAFE_READER_WRITER);
    }

    @Override
    public final long getByteSize() {
        return baseByBu.capacity();
    }

    @Override
    public final @NotNull ByBuOffHeap asByBuOffHeap() {
        return this;
    }

    @Override
    public final @NotNull ByteBuffer getByteBuffer() {
        return this.baseByBu;
    }
}
