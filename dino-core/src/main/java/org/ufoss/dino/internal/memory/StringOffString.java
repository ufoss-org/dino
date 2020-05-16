/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.internal.utils.UnsafeStringOps;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

/**
 * OffString built from a java.lang.String
 */
public final class StringOffString extends AbstractOffString {

    private final String string;

    public StringOffString(String string, Charset charset) {
        this(string, UnsafeStringOps.encode(string, charset));
    }

    private StringOffString(String string, UnsafeStringOps.Result result) {
        super(result.getBbMemory(), result.getCharset());
        this.isAscii = result.isAscii();
        this.isLatin1 = result.isLatin1();
        this.string = string;
    }

    @Override
    public final @NotNull String toString() {
        return this.string;
    }
}
