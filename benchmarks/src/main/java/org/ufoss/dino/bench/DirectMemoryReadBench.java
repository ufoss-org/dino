/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.bench;

import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class DirectMemoryReadBench {

    private static final int OBJ_SIZE = 8 + 4 + 1;
    private static final int NUM_ELEM = 1_000_000;

    private static final VarHandle intHandle = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle longHandle = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

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
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.putLong(i);
            bb2.putLong(i);
            bb.putInt(i);
            bb2.putInt(i);
            bb.put((byte) (i & 1));
            bb2.put((byte) (i & 1));
            LONG_HANDLE_STRUCT.set(base2, (long) i, (long) i);
            INT_HANDLE_STRUCT.set(base2, (long) i, i);
            BYTE_HANDLE_STRUCT.set(base2, (long) i, (byte) (i & 1));
        }
    }

    public void tearDown() {
        segment.close();
        segment2.close();
    }

    public void directRead() {
        bb.rewind();
        for (int i = 0; i < NUM_ELEM; i++) {
            bb.getLong();
            bb.getInt();
            bb.get();
        }
    }

    public void indexedRead() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            bb.getLong(index);
            bb.getInt(index + 8);
            bb.get(index + 12);
        }
    }

    public void varhandleRead() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            longHandle.get(bb, index);
            intHandle.get(bb, index + 8);
            bb.get(index + 12);
        }
    }

    public void indexedReadMemorySegmentAssociated() {
        var index = 0;
        for (int i = 0; i < NUM_ELEM; i++) {
            index = OBJ_SIZE * i;
            bb2.getLong(index);
            bb2.getInt(index + 8);
            bb2.get(index + 12);
        }
    }

    public void varhandleMemorySegmentReadGroupAndStruct() {
        for (int i = 0; i < NUM_ELEM; i++) {
            LONG_HANDLE_STRUCT.get(base2, (long) i);
            INT_HANDLE_STRUCT.get(base2, (long) i);
            BYTE_HANDLE_STRUCT.get(base2, (long) i);
        }
    }
}
