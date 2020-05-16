/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.jdk14.memory;

import org.ufoss.dino.jdk14.utils.MemorySegmentOps;
import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.memory.OffHeap;
import org.ufoss.dino.memory.impl.UnsafeOffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class contains a native {@link MemorySegment}.
 */
final class MemorySegmentUnsafeOffHeap extends UnsafeOffHeap {

    private final MemorySegment segment;
    private final ByteBuffer baseByBu;

    MemorySegmentUnsafeOffHeap(MemorySegment segment) {
        this(segment, MemorySegmentOps.getBaseByteBuffer(segment));
    }

    private MemorySegmentUnsafeOffHeap(MemorySegment segment, ByteBuffer baseByBu) {
        super(baseByBu);
        this.segment = segment;
        this.baseByBu = baseByBu;
    }

    @Override
    public final long getByteSize() {
        return this.segment.byteSize();
    }

    @Override
    public final @NotNull OffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentUnsafeOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final @NotNull OffHeap acquire() {
        return new MemorySegmentUnsafeOffHeap(this.segment.acquire());
    }

    @Override
    public final @NotNull ByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentUnsafeByBuOffHeap(this.segment, this.baseByBu);
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
