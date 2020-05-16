/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.transfer.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Implementation of the immutable {@link Data} interface based on a fixed size array of {@link ByBuOffHeap}
 *
 * @see MutableByBuArrayData
 */
public final class ByBuArrayData extends AbstractByBuArrayData<ByBuOffHeap> {

    public ByBuArrayData(@NotNull Data first, Data @NotNull ... rest) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(rest);

        // to get total capacity we start with a loop on rest array
        var totalCapacity = 0;
        // todo use instanceof pattern matching of java 14 https://openjdk.java.net/jeps/305
        for (final var data : rest) {
            if (data instanceof ByBuArrayData) {
                totalCapacity += ((ByBuArrayData) data).lastWrittenIndex + 1;
            } else if (data instanceof ByBuData) {
                totalCapacity++;
            } else {
                throw new IllegalArgumentException("data type " + data.getClass().getTypeName() + " is not supported");
            }
        }

        var byteSizesSum = first.getByteSize();
        // initiate arrays
        var offset = 0;
        if (first instanceof ByBuArrayData) {
            final var arrayData = (ByBuArrayData) first;
            offset = arrayData.lastWrittenIndex + 1;
            totalCapacity += offset;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bybuArray = Arrays.copyOf(arrayData.bybuArray, totalCapacity);
            this.limits = Arrays.copyOf(arrayData.limits, totalCapacity);
        } else if (first instanceof ByBuData) {
            final var bytesData = (ByBuData) first;
            offset = 1;
            totalCapacity++;
            if (totalCapacity < DEFAULT_CAPACITY) {
                totalCapacity = DEFAULT_CAPACITY;
            }
            this.bybuArray = new Bytes[totalCapacity];
            this.bybuArray[0] = bytesData.bybu;
            this.limits = new int[totalCapacity];
            this.limits[0] = bytesData.limit;
        } else {
            throw new IllegalArgumentException("data type " + first.getClass().getTypeName() + " is not supported");
        }
        this.lastWrittenIndex = totalCapacity;

        int dataLength;
        for (final var data : rest) {
            byteSizesSum += data.getByteSize();
            if (data instanceof ByBuArrayData) {
                final var arrayData = (ByBuArrayData) data;
                dataLength = arrayData.lastWrittenIndex + 1;
                System.arraycopy(arrayData.bybuArray, 0, this.bybuArray, offset, dataLength);
                System.arraycopy(arrayData.limits, 0, this.limits, offset, dataLength);
                offset += dataLength;
            } else if (first instanceof ByBuData) {
                final var bytesData = (ByBuData) data;
                this.bybuArray[offset] = bytesData.bybu;
                this.limits[offset] = bytesData.limit;
                offset++;
            }
        }
        this.byteSize = byteSizesSum;

        this.reader = new ReaderImpl();
    }
}
