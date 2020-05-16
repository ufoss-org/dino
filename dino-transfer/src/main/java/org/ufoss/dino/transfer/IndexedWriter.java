/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.transfer;

public interface IndexedWriter extends Writer {

    /**
     * Write a byte at the current {@code writeIndex}
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if {@code writeIndex} is greater or equal than {@code byteSize}, there is no
     *                                   room left to write a byte
     * @implSpec increase {@code writeIndex} by {@code 1} after write
     */
    @Override
    IndexedWriter writeByte(byte value);

    /**
     * Write a 4-byte int at the current {@code writeIndex}
     * <p>bytes are written using BIG ENDIAN byte order
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if {@code writeIndex} is greater than {@code (byteSize - 4)}, there is no room
     *                                   left to write a 4-byte int
     * @implSpec increase {@code writeIndex} by {@code 4} after write
     */
    @Override
    IndexedWriter writeInt(int value);

    /**
     * Write a byte at the specified absolute {@code index}
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if {@code index} is less than {@code 0} or if {@code index} is greater than
     *                                   {@code writeIndex} or if {@code index} is greater or equal than
     *                                   {@code (byteSize)} so there is no room left to write a byte
     * @implSpec do not modify {@code writeIndex}
     */
    IndexedWriter writeByteAt(long index, byte value);

    /**
     * Write a 4-byte int at the specified absolute {@code index}
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if {@code index} is less than {@code 0} or if {@code index} is greater than
     *                                   {@code (writeIndex)} or if {@code index} is greater than {@code (byteSize - 4)}
     *                                   so there is no room left to write a 4-byte int
     * @implSpec do not modify {@code writeIndex}
     */
    IndexedWriter writeIntAt(long index, int value);
}
