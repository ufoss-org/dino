/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory;

import org.jetbrains.annotations.NotNull;

public interface MutableOffHeap extends OffHeap {

    /**
     * @return a new immutable OffHeap view of this MutableOffHeap.
     */
    @NotNull OffHeap asReadOnly();

    /**
     * {@inheritDoc}
     *
     * @return a new MutableOffHeap view with updated base position and limit addresses.
     */
    @Override
    @NotNull MutableOffHeap slice(long offset, long length);

    /**
     * {@inheritDoc}
     *
     * @return an an acquired MutableOffHeap which can be used to access memory associated
     * with this OffHeap from the current thread
     */
    @Override
    @NotNull MutableOffHeap acquire();

    /**
     * {@inheritDoc}
     *
     * @return a MutableByBuOffHeap (off-heap memory represented by a direct ByteBuffer) bound to the same memory
     * region as this OffHeap
     */
    @NotNull MutableByBuOffHeap asByBuOffHeap();

    /**
     * Write a byte at the specified absolute {@code index}
     *
     * @implNote No index check !
     */
    void writeByteAt(long index, byte value);

    /**
     * Write a 4-byte int at the specified absolute {@code index}
     * <p>bytes are using BIG ENDIAN byte order
     *
     * @implNote No index check !
     */
    void writeIntAt(long index, int value);

    /**
     * Write a 4-byte int at the specified absolute {@code index}
     * <p>bytes are using LITTLE ENDIAN byte order
     *
     * @implNote No index check !
     */
    void writeIntAtLE(long index, int value);
}
