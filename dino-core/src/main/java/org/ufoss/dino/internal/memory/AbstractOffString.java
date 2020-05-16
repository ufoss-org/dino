/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.memory.OffHeap;
import org.ufoss.dino.memory.OffHeapFactory;
import org.ufoss.dino.memory.OffString;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

abstract class AbstractOffString implements OffString {

    /**
     * This OffHeap stores this String's bytes
     */
    final OffHeap memory;

    final Charset charset;

    Boolean isLatin1;

    Boolean isAscii;

    AbstractOffString(OffHeap memory, Charset charset) {
        this.memory = memory;
        this.charset = charset;
    }

    @Override
    public final @NotNull OffHeap getMemory() {
        return this.memory;
    }

    @Override
    public final @NotNull Charset getCharset() {
        return this.charset;
    }

    @Override
    public final @NotNull OffHeap toMemory(@NotNull Charset charset) {
        // 1) fastest-path : directly return memory : if destination charset is the same, or is fully compatible with current
        if (Objects.requireNonNull(charset).contains(this.charset)) {
            return this.memory;
        }

        // fixme implement code below !
        // fast-path 2) if destination and current charsets are ASCII compatible, then current OffString is maybe ASCII
        if (charset.contains(StandardCharsets.US_ASCII)
                && this.charset.contains(StandardCharsets.US_ASCII)
                && this.isAscii == null) {

        }

        return OffHeapFactory.allocate(0);
    }

    @Override
    public final void close() {
        this.memory.close();
    }

    @Override
    public abstract @NotNull String toString();
}
