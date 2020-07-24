/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.utils.BytesOps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;

abstract class AbstractByBuArrayData<T extends ByBuOffHeap> extends AbstractData {

    /**
     * The array of {@link ByBuOffHeap} into which data is stored
     */
    protected T @NotNull [] bybuArray;

    /**
     * Default initial capacity of the array
     */
    static final int DEFAULT_CAPACITY = 4;

    /**
     * The array of limits : one for each {@link ByBuOffHeap}
     */
    int @NotNull [] limits;

    /**
     * Index of the {@link ByBuOffHeap} in array that is currently read
     */
    int currentReadIndex = 0;

    /**
     * Index of the last {@link ByBuOffHeap} in array that has been written
     *
     * @implNote It has a 0 initial value, even if first memory was not written
     */
    int lastWrittenIndex = 0;

    long byteSize;

    /**
     * Current {@link ByBuOffHeap} to read from
     */
    private @NotNull T memory;

    /**
     * Reading index in the current {@link #memory}
     */
    private int currentPosition = 0;

    /**
     * Number of bytes loaded in the current {@link #memory}
     */
    private int currentLimit;

    long readIndex;

    long writeIndex;

    /**
     * Current byte sequence is the first in the data array
     */
    protected AbstractByBuArrayData() {
            this.memory = Objects.requireNonNull(bybuArray[0]);
            this.currentLimit = limits[0];
    }

    /**
     * @return next not empty byte sequence index from array, or empty if none exists
     */
    private @NotNull OptionalInt getNextReadIndex() {
        if (this.currentReadIndex < this.lastWrittenIndex) {
            return OptionalInt.of(++this.currentReadIndex);
        }
        return OptionalInt.empty();
    }

    @Override
    public final @Range(from = 1, to = Long.MAX_VALUE) long getByteSize() {
        return this.byteSize;
    }

    /*@Override
    public final @Range(from = 0, to = Long.MAX_VALUE - 1) long getLimit() {
        // sum of all limits
        var totalLimit = 0L;
        for (final var limit : this.limits) {
            totalLimit += limit;
        }
        return totalLimit;
    }*/

    @Override
    public final long getReadIndex() {
        return this.readIndex;
    }

    @Override
    public final long getWriteIndex() {
        return this.writeIndex;
    }

    /**
     * closes all not null bytes in bytes array
     */
    @Override
    public final void close() {
        Stream.of(this.bybuArray)
                .filter(Objects::nonNull)
                .forEach(ByBuOffHeap::close);
    }

    @Override
    public byte readByte() {
        final var currentPosition = this.currentPosition;
        final var currentLimit = this.currentLimit;
        final var byteSize = 1;
        final var targetLimit = currentPosition + byteSize;

        // 1) at least 1 byte left to read a byte in current byte sequence
        if (currentLimit >= targetLimit) {
            this.currentPosition = targetLimit;
            this.readIndex += byteSize;
            return this.memory.readByteAt(currentPosition);
        }

        // 2) current byte sequence is exactly exhausted
        // let's get next byte sequence and if present read it
        nextMemory();

        // we are at 0 index in newly obtained byte sequence

        if (this.currentLimit >= byteSize) {
            this.currentPosition = byteSize;
            this.readIndex += byteSize;
            return this.memory.readByteAt(0);
        }

        // 3) memory is exhausted
        throw new IndexOutOfBoundsException("Memory is exhausted");
    }

    @Override
    public int readInt() {
        final var currentPosition = this.currentPosition;
        final var currentLimit = this.currentLimit;
        final var intSize = 4;
        final var targetLimit = currentPosition + intSize;

        // 1) at least 4 bytes left to read an int in current byte sequence
        if (currentLimit >= targetLimit) {
            this.currentPosition = targetLimit;
            return this.memory.readIntAt(currentPosition);
        }

        // 2) current byte sequence is exactly exhausted
        if (currentLimit == currentPosition) {
            // let's get next byte sequence and if present read it
            nextMemory();

            // we are at 0 index in newly obtained byte sequence
            if (this.currentLimit >= intSize) {
                this.currentPosition = intSize;
                return this.memory.readIntAt(0);
            }

            // 3) memory is exhausted
            throw new IndexOutOfBoundsException("Memory is exhausted");
        }

        // 3) must read some bytes in current byte sequence, some others from next one
        final var value = BytesOps.bytesToInt(readByte(), readByte(), readByte(), readByte());
        this.readIndex += intSize;
        return value;
    }

    @Override
    public int readIntLE() {
        final var currentPosition = this.currentPosition;
        final var currentLimit = this.currentLimit;
        final var intSize = 4;
        final var targetLimit = currentPosition + intSize;

        // 1) at least 4 bytes left to read an int in current byte sequence
        if (currentLimit >= targetLimit) {
            this.currentPosition = targetLimit;
            return this.memory.readIntAtLE(currentPosition);
        }

        // 2) current byte sequence is exactly exhausted
        if (currentLimit == currentPosition) {
            // let's get next byte sequence and if present read it
            nextMemory();

            // we are at 0 index in newly obtained byte sequence
            if (this.currentLimit >= intSize) {
                this.currentPosition = intSize;
                return this.memory.readIntAtLE(0);
            }

            // 3) memory is exhausted
            throw new IndexOutOfBoundsException("Memory is exhausted");
        }

        // 3) must read some bytes in current byte sequence, some others from next one
        final var value = BytesOps.bytesToIntLE(readByte(), readByte(), readByte(), readByte());
        this.readIndex += intSize;
        return value;
    }

    @Override
    public byte readByteAt(long index) {
        indexCheck(index, this.writeIndex, 1);

        var currentIndex = 0;
        var limit = 0L;
        while (index > (limit += this.limits[currentIndex])) {
            currentIndex++;
            limit++;
        }
        final var currentPosition = (int) (index + this.limits[currentIndex] - limit);
        final var byteSize = 1;
        final var targetLimit = currentPosition + byteSize;

        // todo

        // 1) at least 1 byte left to read a byte in current byte sequence
        if (currentLimit >= targetLimit) {
            this.currentPosition = targetLimit;
            this.readIndex += byteSize;
            return this.memory.readByteAt(currentPosition);
        }

        // 2) current byte sequence is exactly exhausted
        // let's get next byte sequence and if present read it
        nextMemory();

        // we are at 0 index in newly obtained byte sequence

        if (this.currentLimit >= byteSize) {
            this.currentPosition = byteSize;
            this.readIndex += byteSize;
            return this.memory.readByteAt(0);
        }

        // 3) memory is exhausted
        throw new IndexOutOfBoundsException("todo");
    }

    @Override
    public int readIntAt(long index) {
        return 0; // todo
    }

    @Override
    public int readIntAtLE(long index) {
        return 0; // todo
    }

    /**
     * Switch to next ByBuOffHeap because current one is exhausted
     *
     * @throws IndexOutOfBoundsException if no next ByBuOffHeap
     */
    private void nextMemory() {
        final var nextReadIndex = getNextReadIndex().orElseThrow(() -> new IndexOutOfBoundsException("Last Bybu is exhausted"));
        this.memory = Objects.requireNonNull(bybuArray[nextReadIndex]);
        this.currentLimit = limits[nextReadIndex];
    }
}
