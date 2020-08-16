/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ufoss.dino.fixtures.BinaryTestData;
import org.ufoss.dino.transfer.Data;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ByteBufferBytesTests implements DataTests, MutableBybuTests {

    @Test
    @DisplayName("Verify indexed read using Big Endian is working")
    void readBEIndexed() {
        readBEIndexedTest(Data.of(BinaryTestData.BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify indexed read using Little Endian is working")
    void readLEIndexed() {
        readLEIndexedTest(Data.of(BinaryTestData.BYTES_LITTLE_ENDIAN));
    }

    @Test
    @DisplayName("Verify write using Big Endian is working (direct ByteBuffer)")
    void writeBE() {
        writeBETest(new MutableByteBufferBytes(10));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working (direct ByteBuffer)")
    void writeLE() {
        writeLETest(new MutableByteBufferBytes(10));
    }

    @Test
    @DisplayName("Verify all operations on closed Bytes throw IllegalStateException")
    void closed() {
        final var mutableBytes = new MutableByteBufferBytes( 10);
        mutableBytes.close();
        assertThatThrownBy(() -> mutableBytes.readByteAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
        assertThatThrownBy(() -> mutableBytes.readIntAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
        assertThatThrownBy(() -> mutableBytes.writeByteAt(0, FIRST_BYTE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
        assertThatThrownBy(() -> mutableBytes.writeIntAt(0, FIRST_INT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Segment is not alive");
    }
}
