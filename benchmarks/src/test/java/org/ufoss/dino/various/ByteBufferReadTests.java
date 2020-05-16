/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.various;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

public final class ByteBufferReadTests {

    private ByteBuffer bb;

    private static final VarHandle INT_HANDLE = MethodHandles.byteBufferViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle LONG_HANDLE = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    @BeforeAll
    void before() {
        // Allocate 13 bytes : 4 for int, 8 for long, 1 for byte
        // ByteBuffer is natively Big Endian ordered
        this.bb = ByteBuffer.allocateDirect(13);
        this.bb.putInt(42);
        this.bb.putLong(128L);
        this.bb.put((byte) 0xa);
    }

    @Test
    @DisplayName("Direct read")
    void directRead() {
        this.bb.rewind();
        assertThat(this.bb.getInt()).isEqualTo(42);
        assertThat(this.bb.getLong()).isEqualTo(128L);
        assertThat(this.bb.get()).isEqualTo((byte) 0xa);
    }

    @Test
    @DisplayName("Indexed read")
    void indexedRead() {
        assertThat(this.bb.getInt(0)).isEqualTo(42);
        assertThat(this.bb.getLong(4)).isEqualTo(128L);
        assertThat(this.bb.get(12)).isEqualTo((byte) 0xa);
    }

    @Test
    @DisplayName("VarHandle read")
    void varHandleRead() {
        assertThat(INT_HANDLE.get(bb, 0)).isEqualTo(42);
        assertThat(LONG_HANDLE.get(bb, 4)).isEqualTo(128L);
    }
}
