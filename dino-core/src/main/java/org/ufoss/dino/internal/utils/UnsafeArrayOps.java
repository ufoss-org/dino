/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

/**
 * Util class providing unsafe optimised operations on arrays (fallback to safe if unsafe is not supported)
 */
public final class UnsafeArrayOps {

    private static final Ops OPS = UnsafeAccess.SUPPORT_UNSAFE_ARRAY_OPS ? new UnsafeOps() : new SafeOps();

    // uninstanciable
    private UnsafeArrayOps() {
    }

    public static byte getByte(byte[] target, long index) {
        return OPS.getByte(target, index);
    }

    /*static void arraycopy(byte[] src, long srcIndex, byte[] target, long targetIndex, long length) {
        System.arraycopy(src, (int) srcIndex, target, (int) targetIndex, (int) length);
    }*/

    private static abstract class Ops {

        abstract byte getByte(byte[] target, long index);
    }

    static final class UnsafeOps extends Ops {

        static final long BYTE_ARRAY_BASE_OFFSET = UnsafeAccess.arrayBaseOffset(byte[].class);

        @Override
        final byte getByte(byte[] target, long index) {
            return UnsafeAccess.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        final byte getByte(byte[] target, long index) {
            return target[(int) index];
        }
    }
}
