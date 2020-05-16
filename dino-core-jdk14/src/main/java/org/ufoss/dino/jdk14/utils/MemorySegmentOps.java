/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.jdk14.utils;

import org.ufoss.dino.utils.BytesOps;
import jdk.incubator.foreign.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Util class providing operations on MemorySegment
 */
public final class MemorySegmentOps {

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT_AS_BYTE_SEQ_HANDLE = MemoryLayout.ofSequence(4, MemoryLayouts.BITS_8_BE)
            .varHandle(byte.class, MemoryLayout.PathElement.sequenceElement());

    // uninstanciable
    private MemorySegmentOps() {
    }


    public static byte readByte(@NotNull MemoryAddress address) {
        return (byte) BYTE_HANDLE.get(Objects.requireNonNull(address));
    }

    public static void writeByte(@NotNull MemoryAddress address, byte value) {
        BYTE_HANDLE.set(Objects.requireNonNull(address), value);
    }

    public static int readInt(@NotNull MemoryAddress address) {
        return BytesOps.bytesToInt(
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 0L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 1L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 2L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 3L)
        );
    }

    public static int readIntLE(@NotNull MemoryAddress address) {
        return BytesOps.bytesToIntLE(
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 0L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 1L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 2L),
                (byte) INT_AS_BYTE_SEQ_HANDLE.get(address, 3L)
        );
    }

    public static void writeInt(@NotNull MemoryAddress address, final int value) {
        INT_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) ((value >> 24) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 16) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 8) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) (value & 0xff));
    }

    public static void writeIntLE(@NotNull MemoryAddress address, final int value) {
        INT_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) (value & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 8) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 16) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) ((value >> 24) & 0xff));
    }

    /**
     * Depending on segment's byteSize, returns :
     * <ul>
     *     <li>if byteSize < Integer.MAX_VALUE returned ByteBuffer covers this full memory region</li>
     *     <li>if byteSize > Integer.MAX_VALUE returned ByteBuffer covers first Integer.MAX_VALUE bytes of memory
     *     region</li>
     * </ul>
     */
    public static ByteBuffer getBaseByteBuffer(@NotNull MemorySegment segment) {
        if (segment.byteSize() > Integer.MAX_VALUE) {
            return segment.asSlice(0, Integer.MAX_VALUE).asByteBuffer();
        }
        return segment.asByteBuffer();
    }

    public static void checkStateForSegment(@NotNull MemorySegment segment) {
        if (segment.ownerThread() != Thread.currentThread()) {
            throw new IllegalStateException("Attempt to access segment outside owning thread");
        }
        if (!segment.isAlive()) {
            throw new IllegalStateException("Segment is not alive");
        }
    }
}
