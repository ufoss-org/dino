/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.transfer.Reader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ufoss.dino.internal.BinaryTestData;

import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

interface ReaderTests {

    @Test
    @DisplayName("Verify read using native Big Endian is working")
    default void readBE() {
        // Reader is natively Big Endian ordered
        final var reader = instanciateReader(BinaryTestData.BYTES_BIG_ENDIAN);
        Assertions.assertThat(reader.readByte()).isEqualTo(BinaryTestData.FIRST_BYTE);
        Assertions.assertThat(reader.readInt()).isEqualTo(BinaryTestData.FIRST_INT);
        Assertions.assertThat(reader.readByte()).isEqualTo(BinaryTestData.SECOND_BYTE);
        Assertions.assertThat(reader.readInt()).isEqualTo(BinaryTestData.SECOND_INT);
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    default void readLE() {
        final var reader = instanciateReader(BinaryTestData.BYTES_LITTLE_ENDIAN);
        reader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        Assertions.assertThat(reader.readByte()).isEqualTo(BinaryTestData.FIRST_BYTE);
        Assertions.assertThat(reader.readInt()).isEqualTo(BinaryTestData.FIRST_INT);
        Assertions.assertThat(reader.readByte()).isEqualTo(BinaryTestData.SECOND_BYTE);
        Assertions.assertThat(reader.readInt()).isEqualTo(BinaryTestData.SECOND_INT);
    }

    Reader instanciateReader(byte[] byteArray);
}
