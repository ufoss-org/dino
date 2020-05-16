/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.transfer;

import org.jetbrains.annotations.Range;

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
}
