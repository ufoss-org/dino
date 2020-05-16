/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.bytes;

import java.nio.ByteOrder;

import static org.ufoss.dino.internal.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface BytesTests {

    default void readBETest(Bytes bytes) {
        try (bytes) {
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    default void readLETest(Bytes bytes) {
        try (bytes) {
            bytes.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            assertThat(bytes.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bytes.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(bytes.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bytes.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }
}
