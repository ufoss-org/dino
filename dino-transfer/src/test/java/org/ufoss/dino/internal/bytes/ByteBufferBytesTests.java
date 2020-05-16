/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.bytes;

import org.ufoss.dino.internal.BinaryTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.ufoss.dino.internal.BinaryTestData.FIRST_BYTE;
import static org.ufoss.dino.internal.BinaryTestData.FIRST_INT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ByteBufferBytesTests implements BytesTests, MutableBytesTests {

    @Test
    @DisplayName("Verify read using Big Endian is working")
    void readBE() {
        readBETest(new ByteBufferBytes(BinaryTestData.BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        readLETest(new ByteBufferBytes(BinaryTestData.BYTES_LITTLE_ENDIAN));
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
