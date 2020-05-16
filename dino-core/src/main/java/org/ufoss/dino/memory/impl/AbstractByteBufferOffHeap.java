/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory.impl;

import org.ufoss.dino.internal.utils.IndexedByBuReaderWriter;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

abstract class AbstractByteBufferOffHeap extends AbstractOffHeap {

    /**
     * Depending on byteSize :
     * <ul>
     *     <li>if byteSize < Integer.MAX_VALUE this ByteBuffer covers this full memory region</li>
     *     <li>if byteSize > Integer.MAX_VALUE this ByteBuffer covers first Integer.MAX_VALUE bytes of memory
     *     region</li>
     * </ul>
     */
    final @NotNull ByteBuffer baseByBu;
    final @NotNull IndexedByBuReaderWriter readerWriter;

    /**
     * Instantiate a AbstractOffHeap from a ByteBuffer, can be readonly
     */
    protected AbstractByteBufferOffHeap(@NotNull ByteBuffer baseByBu, boolean isReadonly,
                                        @NotNull Function<ByteBuffer, IndexedByBuReaderWriter> readerWriter) {
        if (!Objects.requireNonNull(baseByBu).isDirect()) {
            throw new IllegalArgumentException("Provided ByteBuffer must be Direct");
        }
        if (isReadonly) {
            if (!baseByBu.isReadOnly()) {
                this.baseByBu = baseByBu.asReadOnlyBuffer();
            } else {
                this.baseByBu = baseByBu;
            }
        } else {
            if (baseByBu.isReadOnly()) {
                throw new IllegalArgumentException("Provided ByteBuffer must not be readOnly");
            }
            this.baseByBu = baseByBu;
        }
        this.readerWriter = readerWriter.apply(this.baseByBu);
    }

    @Override
    protected final byte @NotNull [] toByteArrayNoIndexCheck() {
        checkState();
        return this.readerWriter.toByteArray();
    }

    @Override
    public final byte readByteAt(long index) {
        checkState();
        return this.readerWriter.readByteAt(index);
    }

    @Override
    public final int readIntAt(long index) {
        checkState();
        return this.readerWriter.readIntAt(index);
    }

    @Override
    public final int readIntAtLE(long index) {
        checkState();
        return this.readerWriter.readIntAtLE(index);
    }

    @Override
    public final void close() {
        checkState();
        closeAfterCheckState();
    }

    protected abstract void closeAfterCheckState();

    /**
     * Check it is ok to do an operation on this off-heap memory
     *
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other
     *                               than the thread owning this memory.
     */
    protected void checkState() {
    }
}
