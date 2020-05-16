/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * Signals that an I/O exception of some sort has occurred
 *
 * <p>This is an Unchecked exception
 */
public class DinoIOException extends UncheckedIOException {

    private static final IOException NOP_IO_EXCEPTION = new IOException();

    /**
     * Constructs an {@code DinoIOException}.
     */
    public DinoIOException() {
        super(NOP_IO_EXCEPTION);
    }

    /**
     * Constructs an {@code DinoIOException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @throws NullPointerException if the message is {@code null}
     */
    public DinoIOException(@NotNull String message) {
        super(Objects.requireNonNull(message), NOP_IO_EXCEPTION);
    }

    /**
     * Constructs an {@code DinoIOException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   The {@code IOException} (which is saved for later retrieval by the
     *                {@link #getCause()} method).
     * @throws NullPointerException if the message or the cause is {@code null}
     */
    public DinoIOException(@NotNull String message, @NotNull IOException cause) {
        super(Objects.requireNonNull(message), Objects.requireNonNull(cause));
    }

    /**
     * Constructs an  {@code DinoIOException} with the specified cause and a
     * detail message of {@code cause.toString()}
     * (which typically contains the class and detail message of {@code cause}).
     *
     * @param cause The {@code IOException} (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     * @throws NullPointerException if the cause is {@code null}
     */
    public DinoIOException(@NotNull IOException cause) {
        super(Objects.requireNonNull(cause));
    }

    /**
     * Returns the cause of this exception.
     *
     * @return the {@code IOException} which is the cause of this exception
     * or null if cause is {@code NOP_IO_EXCEPTION}.
     */
    @Override
    public @Nullable IOException getCause() {
        final var cause = super.getCause();
        return (cause == NOP_IO_EXCEPTION) ? null : cause;
    }
}
