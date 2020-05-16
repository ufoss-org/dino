/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

/**
 * Write operations in ByteBuffer without any index bound check (very dangerous !!)
 */
public interface IndexedByBuReaderWriter {
    byte readByteAt(long index);
    int readIntAt(long index);
    default int readIntAtLE(long index) {
        return Integer.reverseBytes(readIntAt(index));
    }
    void writeByteAt(long index, byte value);
    void writeIntAt(long index, int value);
    default void writeIntAtLE(long index, int value) {
        writeIntAt(index, Integer.reverseBytes(value));
    }
    byte[] toByteArray();
}
