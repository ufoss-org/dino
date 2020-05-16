/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.jdk14.memory;

import org.ufoss.dino.jdk14.utils.MemorySegmentOps;
import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.ufoss.dino.memory.MutableOffHeap;
import org.ufoss.dino.memory.OffHeap;
import jdk.incubator.foreign.MemorySegment;
import org.jetbrains.annotations.NotNull;

/**
 * This class contains a native {@link MemorySegment}.
 */
final class MemorySegmentMutableSafeOffHeap extends MemorySegmentSafeOffHeap implements MutableOffHeap {

    MemorySegmentMutableSafeOffHeap(MemorySegment segment) {
        super(segment);
    }

    @Override
    public final @NotNull OffHeap asReadOnly() {
        return new MemorySegmentSafeOffHeap(this.segment.asReadOnly());
    }

    @Override
    public final @NotNull MutableOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());
        return new MemorySegmentMutableSafeOffHeap(this.segment.asSlice(offset, length));
    }

    @Override
    public final @NotNull MutableOffHeap acquire() {
        return new MemorySegmentMutableSafeOffHeap(this.segment.acquire());
    }

    @Override
    public final @NotNull MutableByBuOffHeap asByBuOffHeap() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("ByteSize=%d of this memory is too big to export as a ByBuOffHeap", getByteSize()));
        }
        return new MemorySegmentMutableSafeByBuOffHeap(this.segment);
    }

    @Override
    public final void writeByteAt(long index, byte value) {
        MemorySegmentOps.writeByte(this.baseAddress.addOffset(index), value);
    }

    @Override
    public final void writeIntAt(long index, int value) {
        MemorySegmentOps.writeInt(this.baseAddress.addOffset(index), value);
    }

    @Override
    public final void writeIntAtLE(long index, int value) {
        MemorySegmentOps.writeIntLE(this.baseAddress.addOffset(index), value);
    }
}
