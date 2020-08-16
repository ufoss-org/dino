/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.transfer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.ufoss.dino.internal.transfer.ByBuData;
import org.ufoss.dino.memory.OffHeapFactory;

/**
 * A complete read-only (immutable) binary data
 */
public interface Data extends IndexedReader, AutoCloseable {

    /**
     * @return The size (in bytes) of this binary data
     */
    @Range(from = 1, to = Long.MAX_VALUE) long getByteSize();

    /**
     * Closes all resources that store binary data
     */
    @Override
    void close();

    static @NotNull Data of(byte @NotNull [] bytes) {
        return new ByBuData(OffHeapFactory.of(bytes), bytes.length);
    }
}
