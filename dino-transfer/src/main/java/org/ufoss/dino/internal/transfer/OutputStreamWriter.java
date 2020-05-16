/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.DinoIOException;
import org.ufoss.dino.transfer.Writer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of the {@link Writer} interface based on a {@link OutputStream}
 */
public final class OutputStreamWriter implements Writer {

    private final @NotNull OutputStream out;

    public OutputStreamWriter(@NotNull OutputStream out) {
        this.out = out;
    }

    @Override
    public OutputStreamWriter writeByte(byte value) {
        try {
            out.write(value);
        } catch (IOException ioException) {
            throw new DinoIOException(ioException);
        }
        return this;
    }

    @Override
    public OutputStreamWriter writeInt(int value) {
        try {
            out.write((value >>> 24) & 0xFF);
            out.write((value >>> 16) & 0xFF);
            out.write((value >>> 8) & 0xFF);
            out.write((value) & 0xFF);
        } catch (IOException ioException) {
            throw new DinoIOException(ioException);
        }
        return this;
    }

    public OutputStreamWriter writeIntLE(int value) {
        try {
            out.write((value) & 0xFF);
            out.write((value >>> 8) & 0xFF);
            out.write((value >>> 16) & 0xFF);
            out.write((value >>> 24) & 0xFF);
        } catch (IOException ioException) {
            throw new DinoIOException(ioException);
        }
        return this;
    }
}
