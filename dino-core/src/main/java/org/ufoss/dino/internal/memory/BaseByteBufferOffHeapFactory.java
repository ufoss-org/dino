/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.internal.utils.BaseOffHeapOps;
import org.ufoss.dino.memory.*;
import org.ufoss.dino.memory.impl.*;
import org.jetbrains.annotations.NotNull;
import org.ufoss.dino.memory.MutableOffHeap;
import org.ufoss.dino.memory.OffHeapFactory;
import org.ufoss.dino.memory.impl.*;

import java.nio.ByteBuffer;

final class BaseByteBufferOffHeapFactory implements OffHeapFactory {

    @Override
    public @NotNull MutableOffHeap newSafeMutableOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newMutableSafeByBuOffHeap((int) byteSize);
    }

    @Override
    public @NotNull MutableUnsafeOffHeap newUnsafeMutableOffHeap(long byteSize) {
        if (byteSize > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("byteSize must be a positive Integer");
        }
        return newMutableUnsafeByBuOffHeap((int) byteSize);
    }

    @Override
    public final @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableSafeByBuOffHeap(bb, BaseOffHeapOps.cleanByteBuffer(bb), Thread.currentThread());
    }

    @Override
    public final @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize) {
        final var bb = ByteBuffer.allocateDirect(byteSize);
        return new BaseMutableUnsafeByBuOffHeap(bb, BaseOffHeapOps.cleanByteBuffer(bb), Thread.currentThread());
    }

    @Override
    public final @NotNull SafeByBuOffHeap newSafeByteBufferOffHeap(byte @NotNull [] bytes) {
        return new BaseSafeByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes, Thread.currentThread());
    }

    @Override
    public final @NotNull UnsafeByBuOffHeap newUnsafeByteBufferOffHeap(byte @NotNull [] bytes) {
        return new BaseUnsafeByBuOffHeap(ByteBuffer.allocateDirect(bytes.length), bytes, Thread.currentThread());
    }

    /**
     * @return Integer.MIN_VALUE because this is the default implementation
     */
    @Override
    public final int getLoadPriority() {
        return Integer.MIN_VALUE;
    }
}
