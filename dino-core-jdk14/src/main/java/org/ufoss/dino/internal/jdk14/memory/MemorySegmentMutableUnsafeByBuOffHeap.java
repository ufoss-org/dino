/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.jdk14.memory;

import org.ufoss.dino.jdk14.utils.MemorySegmentOps;
import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.ufoss.dino.memory.impl.MutableUnsafeByBuOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment} of size < Integer.MAX_VALUE and the direct {@link ByteBuffer}
 * linked to it. They both point to the same off-heap memory region.
 */
final class MemorySegmentMutableUnsafeByBuOffHeap extends MutableUnsafeByBuOffHeap {

    private final MemorySegment segment;

    MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment segment) {
        this(segment, segment.asByteBuffer());
    }

    MemorySegmentMutableUnsafeByBuOffHeap(MemorySegment segment, ByteBuffer bb) {
        super(bb);
        this.segment = segment;
    }

    @Override
    public final @NotNull ByBuOffHeap asReadOnly() {
        return new MemorySegmentUnsafeByBuOffHeap(this.segment, this.getByteBuffer());
    }

    @Override
    public final @NotNull MutableByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentMutableUnsafeByBuOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final @NotNull MutableByBuOffHeap acquire() {
        return new MemorySegmentMutableUnsafeByBuOffHeap(this.segment.acquire());
    }

    @Override
    protected final void closeAfterCheckState() {
        this.segment.close();
    }

    @Override
    protected final void checkState() {
        MemorySegmentOps.checkStateForSegment(this.segment);
    }
}
