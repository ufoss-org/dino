/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.transfer.Data;

import static org.ufoss.dino.fixtures.BinaryTestData.*;
import static org.assertj.core.api.Assertions.assertThat;

interface DataTests {

    default void readBEIndexedTest(Data data) {
        try (data) {
            assertThat(data.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(data.readIntAt(1)).isEqualTo(FIRST_INT);
            assertThat(data.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(data.readIntAt(6)).isEqualTo(SECOND_INT);
        }
    }

    default void readLEIndexedTest(Data data) {
        try (data) {
            assertThat(data.readByteAt(0)).isEqualTo(FIRST_BYTE);
            assertThat(data.readIntAtLE(1)).isEqualTo(FIRST_INT);
            assertThat(data.readByteAt(5)).isEqualTo(SECOND_BYTE);
            assertThat(data.readIntAtLE(6)).isEqualTo(SECOND_INT);
        }
    }
}
