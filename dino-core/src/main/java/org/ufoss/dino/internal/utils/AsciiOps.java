/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

public final class AsciiOps {

    // uninstanciable
    private AsciiOps() {
    }

    /**
     * @return if byte[] contains a negative value (= a non ASCII value)
     */
    public static boolean hasNegatives(byte[] bytes) {
        return hasNegativesInternal(bytes, 0, bytes.length);
    }

    /**
     * @param offset from position (inclusive)
     * @param length  number of elements to get
     * @return if byte[] section contains a negative value (= a non ASCII value)
     */
    public static boolean hasNegatives(byte[] bytes, int offset, int length) {
        if ((offset | length | bytes.length - offset - length) < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Incorrect parameters to parse byte[] : bytes.length=%d, offset=%d, length=%d",
                            bytes.length, offset, length));
        }
        return hasNegativesInternal(bytes, offset, length);
    }

    private static boolean hasNegativesInternal(byte[] bytes, int offset, int length) {
        for (var i = offset; i < offset + length; i++) {
            if (UnsafeArrayOps.getByte(bytes, i) < 0) {
                return true;
            }
        }
        return false;
    }
}
