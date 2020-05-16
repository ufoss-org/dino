/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.transfer;

/**
 * This interface allows to write some binary data
 */
public interface Writer {

    /**
     * Writes a byte in the data
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if there is no room in data to write a byte
     */
    Writer writeByte(byte value);

    /**
     * Writes a 4-byte int in the data
     * <p>bytes are written using BIG ENDIAN byte order
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if there is no room in data to write an int (4 bytes)
     */
    Writer writeInt(int value);

    /**
     * Writes a 4-byte int in the data
     * <p>bytes are written using LITTLE ENDIAN byte order
     *
     * @return this instance
     * @throws IndexOutOfBoundsException if there is no room in data to write an int (4 bytes)
     */
    Writer writeIntLE(int value);
}
