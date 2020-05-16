/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory;

import org.ufoss.dino.internal.memory.OffHeapServiceLoader;
import org.ufoss.dino.internal.utils.UnsafeByteBufferOps;
import org.ufoss.dino.memory.impl.*;
import org.jetbrains.annotations.NotNull;
import org.ufoss.dino.memory.impl.*;

import java.util.Objects;

public interface OffHeapFactory {

    @NotNull MutableOffHeap newSafeMutableOffHeap(long byteSize);

    @NotNull MutableUnsafeOffHeap newUnsafeMutableOffHeap(long byteSize);

    @NotNull MutableSafeByBuOffHeap newMutableSafeByBuOffHeap(int byteSize);

    @NotNull MutableUnsafeByBuOffHeap newMutableUnsafeByBuOffHeap(int byteSize);

    @NotNull SafeByBuOffHeap newSafeByteBufferOffHeap(byte @NotNull [] bytes);

    @NotNull UnsafeByBuOffHeap newUnsafeByteBufferOffHeap(byte @NotNull [] bytes);

    int getLoadPriority();

    static @NotNull MutableOffHeap allocate(long byteSize) {
        if (UnsafeByteBufferOps.SUPPORT_UNSAFE) {
            return OffHeapServiceLoader.OFF_HEAP_FACTORY.newUnsafeMutableOffHeap(byteSize);
        }
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newSafeMutableOffHeap(byteSize);
    }

    static @NotNull MutableByBuOffHeap allocate(int byteSize) {
        if (UnsafeByteBufferOps.SUPPORT_UNSAFE) {
            return OffHeapServiceLoader.OFF_HEAP_FACTORY.newMutableUnsafeByBuOffHeap(byteSize);
        }
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newMutableSafeByBuOffHeap(byteSize);
    }

    static @NotNull ByBuOffHeap of(byte @NotNull [] bytes) {
        if (UnsafeByteBufferOps.SUPPORT_UNSAFE) {
            return OffHeapServiceLoader.OFF_HEAP_FACTORY.newUnsafeByteBufferOffHeap(Objects.requireNonNull(bytes));
        }
        return OffHeapServiceLoader.OFF_HEAP_FACTORY.newSafeByteBufferOffHeap(Objects.requireNonNull(bytes));
    }
}
