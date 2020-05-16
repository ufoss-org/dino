/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory.impl;

import org.ufoss.dino.memory.OffHeap;
import org.jetbrains.annotations.NotNull;

/**
 * Base abstract implementation of {@link OffHeap} memory
 */
public abstract class AbstractOffHeap implements OffHeap {

    @Override
    public final byte @NotNull [] toByteArray() {
        if (getByteSize() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(
                    String.format("This off-heap memory's size is too big to export to a byte array : byteSize=%d", getByteSize()));
        }
        return toByteArrayNoIndexCheck();
    }

    protected static void sliceIndexCheck(long offset, long length, long byteSize) {
        if ((offset | length) < 0 || offset > byteSize || length > (byteSize - offset)) {
            throw new IndexOutOfBoundsException(
                    String.format("Incorrect parameters to slice : offset=%d, length=%d, byteSize=%d",
                            offset, length, byteSize));
        }
    }

    protected abstract byte[] toByteArrayNoIndexCheck();
}
