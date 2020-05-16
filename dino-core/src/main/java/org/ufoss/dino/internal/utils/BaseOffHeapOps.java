/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import java.nio.ByteBuffer;

public final class BaseOffHeapOps {

    // uninstanciable
    private BaseOffHeapOps() {
    }

    public static Runnable cleanByteBuffer(final ByteBuffer bb) {
        return () -> {
            // do not clean if ByteBuffer is readonly (would throw an Exception)
            if (!bb.isReadOnly()) {
                UnsafeByteBufferOps.invokeCleaner(bb);
            }
        };
    }
}
