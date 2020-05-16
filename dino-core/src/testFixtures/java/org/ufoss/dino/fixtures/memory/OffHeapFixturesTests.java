/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.fixtures.memory;

import org.ufoss.dino.memory.MutableOffHeap;
import org.ufoss.dino.memory.OffHeapFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.ufoss.dino.fixtures.BinaryTestData.*;
import static org.assertj.core.api.Assertions.*;

public class OffHeapFixturesTests implements OffHeapReadTests, MutableOffHeapWriteTests {

    @Test
    @DisplayName("Verify MutableOffHeap allocate requested long length")
    void allocateLong() {
        try (final var memory = OffHeapFactory.allocate(2L)) {
            assertThat(memory.getByteSize())
                    .isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Verify read using Big Endian is working")
    void readBE() {
        readBETest(OffHeapFactory.of(BYTES_BIG_ENDIAN));
    }

    @Test
    @DisplayName("Verify read using Little Endian is working")
    void readLE() {
        readLETest(OffHeapFactory.of(BYTES_LITTLE_ENDIAN));
    }

    @Test
    @DisplayName("Verify write using Big Endian is working with MutableOffHeap")
    void writeOffHeapBE() {
        writeBETest(OffHeapFactory.allocate(10L));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working with MutableOffHeap")
    void writeOffHeapLE() {
        writeLETest(OffHeapFactory.allocate(10L));
    }

    @Test
    @DisplayName("Verify write using Big Endian is working with MutableOffHeap")
    void writeByBuOffHeapBE() {
        writeBETest(OffHeapFactory.allocate(10));
    }

    @Test
    @DisplayName("Verify write using Little Endian is working with MutableOffHeap")
    void writeByBuOffHeapLE() {
        writeLETest(OffHeapFactory.allocate(10));
    }

    @Test
    @DisplayName("Verify all operations on closed OffHeap throw IllegalStateException")
    void closedOffHeap() {
        final var mutableBytes = OffHeapFactory.allocate(10L);
        closedTest(mutableBytes);
    }

    @Test
    @DisplayName("Verify all operations on closed OffHeap throw IllegalStateException")
    void closedByBuOffHeap() {
        final var mutableBytes = OffHeapFactory.allocate(10);
        closedTest(mutableBytes);
    }

    private void closedTest(MutableOffHeap memory) {
        memory.close();
        assertThatThrownBy(() -> memory.readByteAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not alive");
        assertThatThrownBy(() -> memory.readIntAt(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not alive");
        assertThatThrownBy(() -> memory.writeByteAt(0, FIRST_BYTE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not alive");
        assertThatThrownBy(() -> memory.writeIntAt(0, FIRST_INT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not alive");
    }

    @Test
    @DisplayName("Verify all operations without calling Acquire with other thread throw IllegalStateException")
    void threadSafetyTest() {
        try (final var memory = OffHeapFactory.allocate(2L)) {
            threadSafetyTest(() -> memory.readByteAt(0));
            threadSafetyTest(() -> memory.readIntAt(0));
            threadSafetyTest(() -> memory.writeByteAt(0, FIRST_BYTE));
            threadSafetyTest(() -> memory.writeIntAt(0, FIRST_INT));
        }
    }

    void threadSafetyTest(Runnable runnable) {
        final var uncaughtExceptions = new ArrayList<Throwable>();
        final var thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler((t, e) -> uncaughtExceptions.add(e));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            fail("Should not throw InterruptedException");
        }
        assertThat(uncaughtExceptions)
                .hasSize(1);
        assertThat(uncaughtExceptions.get(0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContainingAll("Attempt to access", "outside owning thread");
    }
}
