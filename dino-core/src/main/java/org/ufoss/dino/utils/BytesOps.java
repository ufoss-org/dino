/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Util class providing operations on bytes
 */
public final class BytesOps {

    // uninstanciable
    private BytesOps() {
    }


    public static int bytesToInt(final byte b0, final byte b1, final byte b2, final byte b3) {
        return (b0 << 24) | ((b1 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b3 & 0xff);
    }

    public static int bytesToIntLE(final byte b0, final byte b1, final byte b2, final byte b3) {
        return (b3 << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | (b0 & 0xff);
    }

    public static byte @NotNull [] intToBytes(final int value) {
        return new byte[]{
                (byte) ((value >> 24) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) (value & 0xff)
        };
    }

    public static byte @NotNull [] intToBytesLE(final int value) {
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 24) & 0xff)
        };
    }
}
