///*
// * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
// */
//
//package org.ufoss.dino.internal.transfer;
//
//import org.ufoss.dino.transfer.Data;
//import org.ufoss.dino.transfer.Reader;
//import org.ufoss.dino.jdk14.utils.MemorySegmentOps;
//import jdk.incubator.foreign.MemoryAddress;
//import jdk.incubator.foreign.MemorySegment;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Range;
//
//import java.nio.ByteOrder;
//import java.util.Objects;
//
//public class SegmentData implements Data {
//
//    private final @NotNull MemorySegment segment;
//    final @NotNull MemoryAddress base;
//
//    /**
//     * Capacity of the {@link #segment}
//     */
//    final @Range(from = 1, to = Long.MAX_VALUE) long capacity;
//
//    /**
//     * The limit of the {@link #segment}
//     */
//    @Range(from = 0, to = Long.MAX_VALUE - 1) long limit;
//
//    /**
//     * The data reader
//     */
//    private final @NotNull Reader reader;
//
//    public SegmentData(@NotNull MemorySegment segment) {
//        this(Objects.requireNonNull(segment), segment.byteSize());
//    }
//
//    public SegmentData(@NotNull MemorySegment segment, @Range(from = 0, to = Long.MAX_VALUE - 1) long limit) {
//        this.segment = Objects.requireNonNull(segment);
//        this.base = segment.baseAddress();
//        this.capacity = segment.byteSize();
//        this.reader = new ReaderImpl();
//        this.limit = limit;
//    }
//
//    @Override
//    public @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
//        return this.capacity;
//    }
//
//    @Override
//    public @NotNull Reader getReader() {
//        return this.reader;
//    }
//
//    @Override
//    public @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
//        return this.limit;
//    }
//
//    /**
//     * Closes the {@link MemorySegment}
//     */
//    @Override
//    public void close() {
//        this.segment.close();
//    }
//
//    /**
//     * Implementation of the {@code Reader} interface that read in the segment
//     */
//    private final class ReaderImpl implements Reader {
//
//        /**
//         * Reading index in the memory segment
//         */
//        private long index = 0L;
//
//        private boolean isBigEndian = true;
//
//
//        @Override
//        public byte readByte() {
//            final var currentIndex = this.index;
//            final var byteSize = 1;
//            final var targetLimit = currentIndex + byteSize;
//
//            // 1) at least 1 byte left to read a byte in memory
//            if (limit >= targetLimit) {
//                this.index = targetLimit;
//                return MemorySegmentOps.readByte(base.addOffset(currentIndex));
//            }
//
//            // 2) memory is exhausted
//            throw new ReaderUnderflowException();
//        }
//
//        @Override
//        public int readInt() {
//            final var currentIndex = this.index;
//            final var intSize = 4;
//            final var targetLimit = currentIndex + intSize;
//
//            // 1) at least 4 bytes left to read an int in memory
//            if (limit >= targetLimit) {
//                this.index = targetLimit;
//                return MemorySegmentOps.readInt(base.addOffset(currentIndex), this.isBigEndian);
//            }
//
//            // 2) memory is exhausted
//            throw new ReaderUnderflowException();
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
