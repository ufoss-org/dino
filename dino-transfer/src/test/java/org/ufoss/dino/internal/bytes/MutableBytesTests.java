package org.ufoss.dino.internal.bytes;

import java.nio.ByteOrder;

import static org.ufoss.dino.internal.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface MutableBytesTests {

    default void writeBETest(MutableBytes mutableBytes) {
        try (mutableBytes) {
            mutableBytes.writeByteAt(0, FIRST_BYTE);
            mutableBytes.writeIntAt(1, FIRST_INT);
            mutableBytes.writeByteAt(5, SECOND_BYTE);
            mutableBytes.writeIntAt(6, SECOND_INT);
            assertThat(mutableBytes.toByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
        }
    }

    default void writeLETest(MutableBytes mutableBytes) {
        try (mutableBytes) {
            mutableBytes.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            mutableBytes.writeByteAt(0, FIRST_BYTE);
            mutableBytes.writeIntAt(1, FIRST_INT);
            mutableBytes.writeByteAt(5, SECOND_BYTE);
            mutableBytes.writeIntAt(6, SECOND_INT);
            assertThat(mutableBytes.toByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
        }
    }
}
