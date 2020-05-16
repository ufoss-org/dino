/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.transfer.MutableData;
import org.ufoss.dino.transfer.Writer;
import org.ufoss.dino.internal.bytes.MutableBytes;
import org.ufoss.dino.utils.BytesOps;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of the mutable {@link MutableData} interface based on a resizable array of {@link MutableBytes}
 *
 * @implNote Inspired by ArrayList
 * @see ByBuArrayData
 */
public final class MutableByBuArrayData extends AbstractByBuArrayData<MutableBytes> implements MutableData {

    /**
     * The bytes supplier, can act as a pool
     */
    private final @NotNull MutableMemorySupplier mutableMemorySupplier;

    /**
     * The data writer
     */
    private final @NotNull Writer writer;

    public MutableByBuArrayData(@NotNull MutableMemorySupplier mutableMemorySupplier) {
        this.mutableMemorySupplier = Objects.requireNonNull(mutableMemorySupplier);

        // init memories and limits with DEFAULT_CAPACITY size
        this.bybuArray = new MutableBytes[DEFAULT_CAPACITY];
        this.limits = new int[DEFAULT_CAPACITY];
        this.byteSize = 0;
        this.bybuArray[0] = mutableMemorySupplier.get();
        this.reader = new ReaderImpl();
        this.writer = new WriterImpl();
    }

    @Override
    public @NotNull Writer getWriter() {
        return this.writer;
    }

    /**
     * Get a new byte sequence from {@link #mutableMemorySupplier}
     *
     * @return newly obtained byte sequence
     */
    private @NotNull MutableBytes supplyNewBytes() {
        // no room left in array
        this.lastWrittenIndex += 1;
        if (this.lastWrittenIndex == this.bybuArray.length) {
            // increase array size by 2 times
            final var newLength = this.bybuArray.length * 2;
            this.bybuArray = Arrays.copyOf(bybuArray, newLength);
            this.limits = Arrays.copyOf(limits, newLength);
        }
        final var bytes = this.mutableMemorySupplier.get();
        this.bybuArray[this.lastWrittenIndex] = bytes;
        return bytes;
    }

    /**
     * Implementation of the {@code Writer} interface that writes in data array of {@link ByBuArrayData}
     */
    private final class WriterImpl implements Writer {

        /**
         * Current byte sequence to write in
         */
        private @NotNull MutableBytes bytes;

        /**
         * Writing index in the current {@link #bytes}
         */
        private int limit = 0;

        /**
         * Capacity of the current {@link #bytes}
         */
        private int capacity;

        boolean isBigEndian = true;

        /**
         * Current byte sequence is the last not null in the data array of {@code ArrayData}
         */
        private WriterImpl() {
            this.bytes = IntStream.rangeClosed(1, bybuArray.length)
                    .mapToObj(i -> bybuArray[bybuArray.length - i])
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
            this.capacity = this.bytes.getByteSize();
        }

        @Override
        public void writeByte(byte value) {
            final var currentLimit = this.limit;
            final var byteSize = 1;
            final var targetLimit = currentLimit + byteSize;

            // 1) at least 1 byte left to write a byte in current byte sequence
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.bytes.writeByteAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            // let's add a new byte sequence from supplier
            addNewBytes();

            // we are at 0 index in newly obtained byte sequence

            if (this.capacity < byteSize) {
                throw new WriterOverflowException();
            }
            this.limit = byteSize;
            this.bytes.writeByteAt(currentLimit, value);
        }

        @Override
        public void writeInt(int value) {
            final var currentLimit = this.limit;
            final var intSize = 4;
            final var targetLimit = currentLimit + intSize;

            // 1) at least 4 bytes left to write an int in current byte sequence
            if (this.capacity >= targetLimit) {
                this.limit = targetLimit;
                this.bytes.writeIntAt(currentLimit, value);
                return;
            }

            // 2) current byte sequence is exactly full
            if (currentLimit == this.capacity) {
                // let's add a new byte sequence from supplier
                addNewBytes();

                // we are at 0 index in newly obtained byte sequence
                if (this.capacity >= intSize) {
                    this.limit = intSize;
                    this.bytes.writeIntAt(currentLimit, value);
                    return;
                }
                throw new WriterOverflowException();
            }

            // 3) must write some bytes in current byte sequence, some others in next one
            for (final var b : BytesOps.intToBytes(value, this.isBigEndian)) {
                writeByte(b);
            }
        }

        @Override
        public final @NotNull ByteOrder getByteOrder() {
            return byteOrder;
        }

        @Override
        public final void setByteOrder(@NotNull ByteOrder byteOrder) {
            this.isBigEndian = (byteOrder == ByteOrder.BIG_ENDIAN);
            MutableByBuArrayData.this.setByteOrder(byteOrder);
        }

        /**
         * Current byte sequence is full, add a new Bytes in data array
         */
        private void addNewBytes() {
            this.bytes = supplyNewBytes();
            this.capacity = this.bytes.getByteSize();
            this.limit = 0;
        }
    }
}
