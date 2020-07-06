package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.memory.MutableByBuOffHeap;

import static org.ufoss.dino.internal.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface MutableBybuTests {

    default void writeBETest(MutableByBuOffHeap mutableBybu) {
        try (mutableBybu) {
            mutableBybu.writeByteAt(0, FIRST_BYTE);
            mutableBybu.writeIntAt(1, FIRST_INT);
            mutableBybu.writeByteAt(5, SECOND_BYTE);
            mutableBybu.writeIntAt(6, SECOND_INT);
            assertThat(mutableBybu.toByteArray()).isEqualTo(BYTES_BIG_ENDIAN);
        }
    }

    default void writeLETest(MutableByBuOffHeap mutableBybu) {
        try (mutableBybu) {
            mutableBybu.writeByteAt(0, FIRST_BYTE);
            mutableBybu.writeIntAtLE(1, FIRST_INT);
            mutableBybu.writeByteAt(5, SECOND_BYTE);
            mutableBybu.writeIntAtLE(6, SECOND_INT);
            assertThat(mutableBybu.toByteArray()).isEqualTo(BYTES_LITTLE_ENDIAN);
        }
    }
}
