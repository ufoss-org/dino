///*
// * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
// */
//
//package org.ufoss.dino.internal.transfer;
//
//import org.ufoss.dino.transfer.MutableData;
//import org.ufoss.dino.transfer.Writer;
//import org.ufoss.dino.jdk14.utils.MemorySegmentOps;
//import jdk.incubator.foreign.MemorySegment;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Range;
//
//import java.nio.ByteOrder;
//
///**
// * Implementation of the mutable {@link MutableData} interface based on a {@link MemorySegment}
// */
//public final class MutableSegmentData extends SegmentData implements MutableData {
//
//    /**
//     * The data writer
//     */
//    private final @NotNull Writer writer;
//
//    /**
//     * Build a Data from a new off-heap {@link MemorySegment}
//     * <p> The byte order of a newly-created Bytes is always {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}. </p>
//     *
//     * @param capacity total capacity of the MemorySegment
//     */
//    public MutableSegmentData(@Range(from = 1, to = Long.MAX_VALUE) long capacity) {
//        super(MemorySegment.allocateNative(capacity), 0);
//        this.writer = new WriterImpl();
//    }
//
//    @Override
//    public @NotNull Writer getWriter() {
//        return this.writer;
//    }
//
//    /**
//     * Implementation of the {@code Writer} interface that write in the segment
//     */
//    private final class WriterImpl implements Writer {
//
//        private boolean isBigEndian = true;
//
//        @Override
//        public void writeByte(byte value) {
//            final var currentLimit = limit;
//            final var byteSize = 1;
//            final var targetLimit = currentLimit + byteSize;
//
//            // 1) at least 1 byte left to write a byte in current memory segment
//            if (capacity >= targetLimit) {
//                limit = targetLimit;
//                MemorySegmentOps.writeByte(base.addOffset(currentLimit), value);
//                return;
//            }
//
//            // 2) memory is exhausted
//            throw new WriterOverflowException();
//        }
//
//        @Override
//        public void writeInt(int value) {
//            final var currentLimit = limit;
//            final var intSize = 4;
//            final var targetLimit = currentLimit + intSize;
//
//            // 1) at least 4 bytes left to write an int in current memory segment
//            if (capacity >= targetLimit) {
//                limit = targetLimit;
//                MemorySegmentOps.writeInt(base.addOffset(currentLimit), value, this.isBigEndian);
//                return;
//            }
//
//            // 2) memory is exhausted
//            throw new WriterOverflowException();
//        }
//
//        @Override
//        public @NotNull ByteOrder getByteOrder() {
//            return this.isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
//        }
//
//        @Override
//        public void setByteOrder(@NotNull ByteOrder byteOrder) {
//            this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
//        }
//    }
//}
