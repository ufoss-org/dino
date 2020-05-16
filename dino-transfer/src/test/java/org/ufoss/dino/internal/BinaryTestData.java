/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal;

import org.ufoss.dino.utils.BytesOps;

public final class BinaryTestData {

    private BinaryTestData() {
    }

    public static final byte FIRST_BYTE = 0xa;

    public static final int FIRST_INT = 42;

    public static final byte SECOND_BYTE = 0xe;

    public static final int SECOND_INT = 4568;

    public static final byte[] BYTES_BIG_ENDIAN;

    public static final byte[] BYTES_LITTLE_ENDIAN;

    static {
        BYTES_BIG_ENDIAN = new byte[10];
        BYTES_LITTLE_ENDIAN = new byte[10];

        BYTES_BIG_ENDIAN[0] = FIRST_BYTE;
        BYTES_LITTLE_ENDIAN[0] = FIRST_BYTE;

        final var firstIntBytesBigEndian = BytesOps.intToBytes(FIRST_INT, true);
        BYTES_BIG_ENDIAN[1] = firstIntBytesBigEndian[0];
        BYTES_BIG_ENDIAN[2] = firstIntBytesBigEndian[1];
        BYTES_BIG_ENDIAN[3] = firstIntBytesBigEndian[2];
        BYTES_BIG_ENDIAN[4] = firstIntBytesBigEndian[3];

        final var firstIntBytesLittleEndian = BytesOps.intToBytes(FIRST_INT, false);
        BYTES_LITTLE_ENDIAN[1] = firstIntBytesLittleEndian[0];
        BYTES_LITTLE_ENDIAN[2] = firstIntBytesLittleEndian[1];
        BYTES_LITTLE_ENDIAN[3] = firstIntBytesLittleEndian[2];
        BYTES_LITTLE_ENDIAN[4] = firstIntBytesLittleEndian[3];

        BYTES_BIG_ENDIAN[5] = SECOND_BYTE;
        BYTES_LITTLE_ENDIAN[5] = SECOND_BYTE;

        final var secondIntBytesBigEndian = BytesOps.intToBytes(SECOND_INT, true);
        BYTES_BIG_ENDIAN[6] = secondIntBytesBigEndian[0];
        BYTES_BIG_ENDIAN[7] = secondIntBytesBigEndian[1];
        BYTES_BIG_ENDIAN[8] = secondIntBytesBigEndian[2];
        BYTES_BIG_ENDIAN[9] = secondIntBytesBigEndian[3];

        final var secondIntBytesLittleEndian = BytesOps.intToBytes(SECOND_INT, false);
        BYTES_LITTLE_ENDIAN[6] = secondIntBytesLittleEndian[0];
        BYTES_LITTLE_ENDIAN[7] = secondIntBytesLittleEndian[1];
        BYTES_LITTLE_ENDIAN[8] = secondIntBytesLittleEndian[2];
        BYTES_LITTLE_ENDIAN[9] = secondIntBytesLittleEndian[3];
    }
}
