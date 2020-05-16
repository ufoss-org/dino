/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.various;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferWriteTests {

    // Allocate 13 bytes : 4 for int, 8 for long, 1 for byte
    // ByteBuffer is natively Big Endian ordered
    private final ByteBuffer bb = ByteBuffer.allocateDirect(13);

    private static final VarHandle INT_HANDLE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    @AfterEach
    void afterEach() {
        this.bb.clear();
    }

    private void verifyContent() {
        assertThat(this.bb.getInt(0)).isEqualTo(42);
        assertThat(this.bb.getLong(4)).isEqualTo(128L);
        assertThat(this.bb.get(12)).isEqualTo((byte) 0xa);
    }

    @Test
    @DisplayName("Direct write")
    void directWrite() {
        this.bb.putInt(42);
        this.bb.putLong(128L);
        this.bb.put((byte) 0xa);
        verifyContent();
    }

    @Test
    @DisplayName("Indexed write")
    void indexedWrite() {
        this.bb.putInt(0, 42);
        this.bb.putLong(4, 128L);
        this.bb.put(12, (byte) 0xa);
        verifyContent();
    }

    @Test
    @DisplayName("VarHandle write")
    void varHandleWrite() {
        INT_HANDLE.set(bb, 0, 42);
        LONG_HANDLE.set(bb, 4, 128L);
        bb.put(12, (byte) 0xa);
        verifyContent();
    }
}
