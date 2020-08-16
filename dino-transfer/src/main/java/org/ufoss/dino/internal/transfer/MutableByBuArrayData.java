/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.jetbrains.annotations.NotNull;
import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.ufoss.dino.transfer.MutableData;
import org.ufoss.dino.utils.BytesOps;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implementation of the mutable {@link MutableData} interface based on a resizable array of {@link MutableByBuOffHeap}
 *
 * @implNote Inspired by ArrayList
 * @see ByBuArrayData
 */
public final class MutableByBuArrayData extends AbstractByBuArrayData<MutableByBuOffHeap> implements MutableData {

    /**
     * The bytes supplier, can act as a pool
     */
    private final @NotNull MutableBybuSupplier mutableBybuSupplier;

    /**
     * Current byte sequence to write in
     */
    private @NotNull MutableByBuOffHeap bytes;

    /**
     * Writing index in the current {@link #bytes}
     */
    private int limit = 0;

    /**
     * Capacity of the current {@link #bytes}
     */
    private long capacity;

    public MutableByBuArrayData(@NotNull MutableBybuSupplier mutableBybuSupplier) {
        this.mutableBybuSupplier = Objects.requireNonNull(mutableBybuSupplier);

        // init memories and limits with DEFAULT_CAPACITY size
        this.bybuArray = new MutableByBuOffHeap[DEFAULT_CAPACITY];
        this.limits = new int[DEFAULT_CAPACITY];
        this.byteSize = 0;
        this.bybuArray[0] = mutableBybuSupplier.get();
        this.bytes = IntStream.rangeClosed(1, bybuArray.length)
                .mapToObj(i -> bybuArray[bybuArray.length - i])
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
        this.capacity = this.bytes.getByteSize();
    }

    /**
     * Get a new byte sequence from {@link #mutableBybuSupplier}
     *
     * @return newly obtained byte sequence
     */
    private @NotNull MutableByBuOffHeap supplyNewBytes() {
        this.lastWrittenIndex += 1;
        // no room left in array
        if (this.lastWrittenIndex == this.bybuArray.length) {
            // increase array size by 2 times
            final var newLength = this.bybuArray.length * 2;
            this.bybuArray = Arrays.copyOf(bybuArray, newLength);
            this.limits = Arrays.copyOf(limits, newLength);
        }
        final var bytes = this.mutableBybuSupplier.get();
        this.bybuArray[this.lastWrittenIndex] = bytes;
        return bytes;
    }

    @Override
    public MutableByBuArrayData writeByte(byte value) {
        final var currentLimit = this.limit;
        final var byteSize = 1;
        final var targetLimit = currentLimit + byteSize;

        // 1) at least 1 byte left to write a byte in current byte sequence
        if (this.capacity >= targetLimit) {
            this.limit = targetLimit;
            this.bytes.writeByteAt(currentLimit, value);
            return this;
        }

        // 2) current byte sequence is exactly full
        // let's add a new byte sequence from supplier
        addNewBytes();

        // we are at 0 index in newly obtained byte sequence

        if (this.capacity < byteSize) {
            throw new IndexOutOfBoundsException("Memory is exhausted");
        }
        this.limit = byteSize;
        this.bytes.writeByteAt(currentLimit, value);
        return this;
    }

    @Override
    public MutableByBuArrayData writeInt(int value) {
        final var currentLimit = this.limit;
        final var intSize = 4;
        final var targetLimit = currentLimit + intSize;

        // 1) at least 4 bytes left to write an int in current byte sequence
        if (this.capacity >= targetLimit) {
            this.limit = targetLimit;
            this.bytes.writeIntAt(currentLimit, value);
            return this;
        }

        // 2) current byte sequence is exactly full
        if (currentLimit == this.capacity) {
            // let's add a new byte sequence from supplier
            addNewBytes();

            // we are at 0 index in newly obtained byte sequence
            if (this.capacity >= intSize) {
                this.limit = intSize;
                this.bytes.writeIntAt(currentLimit, value);
                return this;
            }
            throw new IndexOutOfBoundsException("Memory is exhausted");
        }

        // 3) must write some bytes in current byte sequence, some others in next one
        for (final var b : BytesOps.intToBytes(value)) {
            writeByte(b);
        }
        return this;
    }

    @Override
    public MutableByBuArrayData writeIntLE(int value) {
        return null;
    }

    @Override
    public MutableByBuArrayData writeByteAt(long index, byte value) {
        return null;
    }

    @Override
    public MutableByBuArrayData writeIntAt(long index, int value) {
        return null;
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
