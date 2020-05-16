/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.transfer;

/**
 * This interface allows to read some binary data
 */
public interface Reader {

    /**
     * Read a byte in the data
     *
     * @throws IndexOutOfBoundsException if there is no byte left to read
     */
    byte readByte();

    /**
     * Read a 4-bytes int in the data
     * <p>bytes are read using BIG ENDIAN byte order
     * @throws IndexOutOfBoundsException if there is less than 4 bytes left to read
     */
    int readInt();

    /**
     * Read a 4-bytes int in the data
     * <p>bytes are read using LITTLE ENDIAN byte order
     * @throws IndexOutOfBoundsException if there is less than 4 bytes left to read
     */
    int readIntLE();
}
