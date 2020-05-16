/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.internal.utils.UnsafeStringOps;
import org.ufoss.dino.memory.OffHeap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.Boolean.TRUE;

public final class OffstringImpl extends AbstractOffString {

    /**
     * late init string used as cache, assigned the first time {@link #toString} is invoked
     */
    private String string;

    public OffstringImpl(OffHeap memory, Charset charset) {
        super(memory, charset);
        if (charset == StandardCharsets.US_ASCII) {
            this.isAscii = true;
            this.isLatin1 = true;
        } else if (charset == StandardCharsets.ISO_8859_1) {
            this.isLatin1 = true;
        }
    }

    @Override
    public final @NotNull String toString() {
        // 1) fastest-path : return already cached String :)
        if (this.string != null) {
            return this.string;
        }

        // 2) fast-path for Latin1 or ASCII (Latin1 is ASCII compatible)
        if (TRUE.equals(this.isLatin1) || TRUE.equals(this.isAscii)) {
            return this.string = UnsafeStringOps.toLatin1String(this.memory.toByteArray());
        }

        // 3) fast-path for UTF-16
        if (StandardCharsets.UTF_16 == this.charset) {
            return this.string = UnsafeStringOps.toUtf16String(this.memory.toByteArray());
        }

        return this.string = new String(this.memory.toByteArray(), this.charset);
    }
}
