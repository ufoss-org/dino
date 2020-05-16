/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory.impl;

import org.ufoss.dino.internal.utils.UnsafeByteBufferOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class UnsafeOffHeap extends AbstractByteBufferOffHeap {

    /**
     * Instantiate a readonly UnsafeOffHeap from a ByteBuffer
     */
    protected UnsafeOffHeap(@NotNull ByteBuffer bb) {
        super(Objects.requireNonNull(bb), true, UnsafeByteBufferOps.UNSAFE_READER_WRITER);
    }
}
