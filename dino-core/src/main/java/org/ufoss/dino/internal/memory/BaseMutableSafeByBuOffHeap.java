/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.memory.MutableByBuOffHeap;
import org.ufoss.dino.memory.impl.MutableSafeByBuOffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

final class BaseMutableSafeByBuOffHeap extends MutableSafeByBuOffHeap {

    private final Runnable cleanupAction;
    private final Thread owner;
    private boolean closed = false;

    BaseMutableSafeByBuOffHeap(final ByteBuffer bb, Runnable cleanupAction, Thread owner) {
        super(bb);
        this.cleanupAction = cleanupAction;
        this.owner = owner;
    }

    @Override
    public final @NotNull ByBuOffHeap asReadOnly() {
        // call constructor to do nothing on close, because cleaner is already associated to this ByteBuffer
        return new BaseSafeByBuOffHeap(getByteBuffer(), () -> {}, this.owner);
    }

    @Override
    public final @NotNull MutableByBuOffHeap slice(long offset, long length) {
        sliceIndexCheck(offset, length, getByteSize());

        // save previous values
        final var limit = getByteBuffer().limit();
        final var position = getByteBuffer().position();

        // change values so slice respect required offset and length
        getByteBuffer().limit((int) (offset + length));
        getByteBuffer().position((int) offset);
        // call constructor to do nothing on close, because invoke cleaner on a sliced ByteBuffer throws an Exception
        final var slice = new BaseMutableSafeByBuOffHeap(getByteBuffer().slice(), () -> {}, this.owner);

        // re-affect previous values
        getByteBuffer().limit(limit);
        getByteBuffer().position(position);

        return slice;
    }

    @Override
    public final @NotNull MutableByBuOffHeap acquire() {
        return new BaseMutableSafeByBuOffHeap(getByteBuffer(), () -> {}, Thread.currentThread());
    }

    @Override
    protected final void closeAfterCheckState() {
        this.cleanupAction.run();
        this.closed = true;
    }

    @Override
    protected final void checkState() {
        if (this.owner != Thread.currentThread()) {
            throw new IllegalStateException("Attempt to access OffHeap outside owning thread");
        }
        if (this.closed) {
            throw new IllegalStateException("OffHeap is not alive");
        }
    }
}
