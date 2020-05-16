/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory;

import org.ufoss.dino.internal.memory.OffstringImpl;
import org.ufoss.dino.internal.memory.StringOffString;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * This interface represents off-heap memory region (a {@link OffHeap}) that store a a binary String content encoded
 * with a given {@link Charset}.
 * <p>
 * OffStrings are constant (immutable), their values cannot be changed after they are created.
 * <p>OffStrings must be explicitly (see {@link OffString#close()}) to release associated resources
 */
public interface OffString extends AutoCloseable {

    /**
     * @return the off-heap memory region that store binary String content encoded with {@link Charset}
     * @see #getCharset() to obtain the charset
     */
    @NotNull OffHeap getMemory();

    /**
     * @return the {@link Charset} used to encode String in off-heap memory
     * @see #getMemory() to obtain the off-heap memory region
     */
    @NotNull Charset getCharset();

    @NotNull OffHeap toMemory(@NotNull Charset charset);

    /**
     * Best effort to return a String from the memory (that may be too big for one single String)
     *
     * @return a String that contains all content of this OffString
     * @throws UnsupportedOperationException if the memory's content cannot fit into a {@link String} instance,
     * e.g. it has more than {@link Integer#MAX_VALUE} characters
     */
    @Override
    @NotNull String toString();

    /**
     * Releases associated resources
     * @implSpec Invoke {@link OffHeap#close()}
     */
    @Override
    void close();

    static @NotNull OffString of(@NotNull String string, @NotNull Charset charset) {
        return new StringOffString(Objects.requireNonNull(string), Objects.requireNonNull(charset));
    }

    static @NotNull OffString of(@NotNull OffHeap memory, @NotNull Charset charset) {
        return new OffstringImpl(Objects.requireNonNull(memory), Objects.requireNonNull(charset));
    }
}
