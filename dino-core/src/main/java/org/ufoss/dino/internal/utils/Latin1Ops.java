/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.memory.OffHeapFactory;

public final class Latin1Ops {

    // uninstanciable
    private Latin1Ops() {
    }

    /**
     * @param bytes Latin1 bytes
     * @return a UTF-8 encoded ByteBufferOffHeap built from Latin1 byte[] parameter
     */
    public static ByBuOffHeap encodelatinBytesToUTF8(final byte[] bytes) {
        final var bbMemory = OffHeapFactory.allocate(bytes.length << 1);
        final var bybu = bbMemory.getByteBuffer();
        // position in new ByteBuffer starts at 0
        final var writtenBytes = UnsafeByteBufferOps.write(bybu, 0, writer -> {
            for (int sourceIndex = 0; sourceIndex < bytes.length; sourceIndex++) {
                byte c = bytes[sourceIndex];
                if (c < 0) {
                    writer.writeByte((byte) (0xc0 | ((c & 0xff) >> 6)));
                    writer.writeByte((byte) (0x80 | (c & 0x3f)));
                } else {
                    writer.writeByte(c);
                }
            }
        });
        // position = number of written bytes
        bybu.position(writtenBytes);
        if (writtenBytes == bbMemory.getByteSize()) {
            return bbMemory;
        }
        return bbMemory.slice(0, writtenBytes);
    }
}
