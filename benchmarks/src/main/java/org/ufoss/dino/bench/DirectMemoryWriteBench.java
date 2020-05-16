/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.bench;

import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public final class DirectMemoryWriteBench {

    private static final int OBJ_SIZE = 8 + 4 + 1;
    private static final int NUM_ELEM = 1_000_000;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle BYTE_HANDLE = MemoryHandles.varHandle(byte.class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle INT_AS_BYTE_SEQ_HANDLE = MemoryLayout.ofSequence(4, MemoryLayouts.BITS_8_BE)
            .varHandle(byte.class, MemoryLayout.PathElement.sequenceElement());
    private static final VarHandle LONG_AS_BYTE_SEQ_HANDLE = MemoryLayout.ofSequence(8, MemoryLayouts.BITS_8_BE)
            .varHandle(byte.class, MemoryLayout.PathElement.sequenceElement());

    private static final GroupLayout STRUCT_LAYOUT = MemoryLayout.ofStruct(
            MemoryLayouts.BITS_64_BE.withName("long"),
            MemoryLayouts.BITS_32_BE.withName("int"),
            MemoryLayouts.BITS_8_BE.withName("byte"),
            MemoryLayout.ofPaddingBits(8),
            MemoryLayout.ofPaddingBits(16));
    private static final SequenceLayout SEQ_LAYOUT = MemoryLayout.ofSequence(NUM_ELEM, STRUCT_LAYOUT);
    private static final VarHandle LONG_HANDLE_STRUCT = SEQ_LAYOUT.varHandle(long.class,
            MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.groupElement("long"));
    private static final VarHandle INT_HANDLE_STRUCT = SEQ_LAYOUT.varHandle(int.class,
            MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.groupElement("int"));
    private static final VarHandle BYTE_HANDLE_STRUCT = SEQ_LAYOUT.varHandle(byte.class,
            MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.groupElement("byte"));

    private ByteBuffer bb;
    private ByteBuffer bb2;
    private MemorySegment segment;
    private MemoryAddress base;
    private MemorySegment segment2;
    private MemoryAddress base2;

    public void setup() {
        bb = ByteBuffer.allocateDirect(OBJ_SIZE * NUM_ELEM);
        segment = MemorySegment.allocateNative(OBJ_SIZE * NUM_ELEM);
        base = segment.baseAddress();
        bb2 = segment.asByteBuffer();
        segment2 = MemorySegment.allocateNative(SEQ_LAYOUT);
        base2 = segment2.baseAddress();
    }

    public void tearDown() {
        segment.close();
        segment2.close();
    }

    public void directWrite() {
        bb.clear();
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.putLong(i);
            bb.putInt(i);
            bb.put((byte) (i & 1));
        }
    }

    public void indexedWrite() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            bb.putLong(index, i);
            bb.putInt(index + 8, i);
            bb.put(index + 12, (byte) (i & 1));
        }
    }

    public void varhandleWrite() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            longHandle.set(bb, index, i);
            intHandle.set(bb, index + 8, i);
            bb.put(index + 12, (byte) (i & 1));
        }
    }

    public void indexedWriteMemorySegmentAssociated() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            bb2.putLong(index, i);
            bb2.putInt(index + 8, i);
            bb2.put(index + 12, (byte) (i & 1));
        }
    }

    public void varhandleMemorySegmentWrite() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            writeLong(base.addOffset(index), i);
            writeInt(base.addOffset(index + 8), i);
            writeByte(base.addOffset(index + 12), (byte) (i & 1));
        }
    }

    public void varhandleMemorySegmentWriteGroupAndStruct() {
        for (int i = 0; i < NUM_ELEM; i++) {
            LONG_HANDLE_STRUCT.set(base2, (long) i, (long) i);
            INT_HANDLE_STRUCT.set(base2, (long) i, i);
            BYTE_HANDLE_STRUCT.set(base2, (long) i, (byte) (i & 1));
        }
    }

    private static void writeByte(MemoryAddress address, byte value) {
        BYTE_HANDLE.set(Objects.requireNonNull(address), value);
    }

    private static void writeInt(MemoryAddress address, final int value) {
        INT_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) ((value >> 24) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 16) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 8) & 0xff));
        INT_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) (value & 0xff));
    }

    private static void writeLong(MemoryAddress address, final long value) {
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 0L, (byte) ((value >> 56) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 1L, (byte) ((value >> 48) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 2L, (byte) ((value >> 40) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 3L, (byte) ((value >> 32) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 4L, (byte) ((value >> 24) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 5L, (byte) ((value >> 16) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 6L, (byte) ((value >> 8) & 0xff));
        LONG_AS_BYTE_SEQ_HANDLE.set(address, 7L, (byte) (value & 0xff));
    }
}
