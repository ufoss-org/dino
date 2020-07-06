/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.memory.ByBuOffHeap;

import static org.ufoss.dino.internal.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface BybuTests {

    default void readBETest(ByBuOffHeap bybu) {
        try (bybu) {
            assertThat(bybu.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bybu.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(bybu.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bybu.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    default void readLETest(ByBuOffHeap bybu) {
        try (bybu) {
            assertThat(bybu.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(bybu.readIntAtLE(1)).isEqualTo(FIRST_INT);
            assertThat(bybu.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(bybu.readIntAtLE(6)).isEqualTo(SECOND_INT);
        }
    }
}
