/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Util class providing unsafe optimised operations on native ByteBuffer (fallback to safe if unsafe is not supported)
 */
public final class UnsafeByteBufferOps {

    public static final boolean SUPPORT_UNSAFE = UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET != null;

    private static final Ops SAFE_OPS = new SafeOps();

    private static final Ops UNSAFE_OPS = new UnsafeOps();

    private static final Ops OPS = SUPPORT_UNSAFE ? UNSAFE_OPS : SAFE_OPS;

    public static final Function<ByteBuffer, IndexedByBuReaderWriter> SAFE_READER_WRITER =  SAFE_OPS::readerWriter;

    public static final Function<ByteBuffer, IndexedByBuReaderWriter> UNSAFE_READER_WRITER;

    static {
        if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) {
            UNSAFE_READER_WRITER = UNSAFE_OPS::readerWriter;
        } else {
            UNSAFE_READER_WRITER = bybu -> new ReversedIndexedByBuReaderWriter(UNSAFE_OPS.readerWriter(bybu));
        }
    }

    // uninstanciable
    private UnsafeByteBufferOps() {
    }

    /**
     * Write operations in ByteBuffer without any index bound check (very dangerous !!)
     */
    public static int write(ByteBuffer bybu, int index, Consumer<ByBuWriter> consumer) {
        return OPS.write(bybu, index, consumer);
    }

    /**
     * Write operations in ByteBuffer without any index bound check (very dangerous !!)
     */
    public interface ByBuWriter {
        void writeByte(byte value);
    }

    public static void invokeCleaner(ByteBuffer bybu) {
        OPS.invokeCleaner(bybu);
    }

    /**
     * Copy the contents of this ByteBuffer into a byte array
     */
    public static void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
        OPS.fillTargetByteArray(bybu, index, bytes, offset, length);
    }

    /**
     * Copy the contents of this byte array into a ByteBuffer.
     * <p>Does not change position
     */
    public static ByteBuffer safeFillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
        return SAFE_OPS.fillWithByteArray(bybu, index, bytes, offset, length);
    }

    /**
     * Copy the contents of this byte array into a ByteBuffer.
     * <p>Does not change position
     */
    public static ByteBuffer unsafeFillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
        return UNSAFE_OPS.fillWithByteArray(bybu, index, bytes, offset, length);
    }

    private static final class ReversedIndexedByBuReaderWriter implements IndexedByBuReaderWriter {

        private final IndexedByBuReaderWriter delegate;

        private ReversedIndexedByBuReaderWriter(IndexedByBuReaderWriter delegate) {
            this.delegate = delegate;
        }

        @Override
        public byte readByteAt(long index) {
            return this.delegate.readByteAt(index);
        }

        @Override
        public int readIntAt(long index) {
            return Integer.reverseBytes(this.delegate.readIntAt(index));
        }

        @Override
        public int readIntAtLE(long index) {
            return this.delegate.readIntAt(index);
        }

        @Override
        public void writeByteAt(long index, byte value) {
            this.delegate.writeByteAt(index, value);
        }

        @Override
        public void writeIntAt(long index, int value) {
            this.delegate.writeIntAt(index, Integer.reverseBytes(value));
        }

        @Override
        public void writeIntAtLE(long index, int value) {
            this.delegate.writeIntAt(index, value);
        }

        @Override
        public byte[] toByteArray() {
            return this.delegate.toByteArray();
        }
    }

    private static abstract class Ops {

        abstract int write(ByteBuffer bybu, int index, Consumer<ByBuWriter> consumer);

        abstract IndexedByBuReaderWriter readerWriter(ByteBuffer bybu);

        abstract void invokeCleaner(ByteBuffer bybu);

        abstract void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length);

        public abstract ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length);
    }

    private static final class UnsafeOps extends Ops {

        private static final long BYTE_BUFFER_ADDRESS_OFFSET = UnsafeAccess.UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET;

        /**
         * Returns the base memory address of this ByteBuffer.
         */
        private static long getBaseAddress(ByteBuffer bybu) {
            return UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
        }

        // this allows to write directly in off-heap Memory of a native ByteBuffer
        @Override
        final int write(ByteBuffer bybu, int index, Consumer<ByBuWriter> consumer) {
            final var baseAddress = getBaseAddress(bybu);
            final var bybuWriter = new UnsafeByBuWriter(baseAddress + index);
            consumer.accept(bybuWriter);
            return (int) (bybuWriter.address - baseAddress);
        }

        private static final class UnsafeByBuWriter implements ByBuWriter {

            private long address;

            private UnsafeByBuWriter(long baseAddress) {
                this.address = baseAddress;
            }

            @Override
            public void writeByte(byte value) {
                UnsafeAccess.putByte(this.address++, value);
            }
        }

        @Override
        IndexedByBuReaderWriter readerWriter(ByteBuffer bybu) {
            final var baseAddress = getBaseAddress(bybu);
            return new UnsafeIndexedByBuReaderWriter(baseAddress, bybu.capacity());
        }

        private static final class UnsafeIndexedByBuReaderWriter implements IndexedByBuReaderWriter {

            private final long baseAddress;
            private final int byteSize;

            private UnsafeIndexedByBuReaderWriter(long baseAddress, int byteSize) {
                this.baseAddress = baseAddress;
                this.byteSize = byteSize;
            }

            @Override
            public byte readByteAt(long index) {
                return UnsafeAccess.getByte(this.baseAddress + index);
            }

            @Override
            public int readIntAt(long index) {
                return UnsafeAccess.getInt(this.baseAddress + index);
            }

            @Override
            public void writeByteAt(long index, byte value) {
                UnsafeAccess.putByte(this.baseAddress + index, value);
            }

            @Override
            public void writeIntAt(long index, int value) {
                UnsafeAccess.putInt(this.baseAddress + index, value);
            }

            @Override
            public byte[] toByteArray() {
                final var bytes = new byte[this.byteSize];
                UnsafeAccess.copyMemory(null, this.baseAddress, bytes,
                        UnsafeArrayOps.UnsafeOps.BYTE_ARRAY_BASE_OFFSET, this.byteSize);
                return bytes;
            }
        }

        @Override
        void invokeCleaner(ByteBuffer bybu) {
            UnsafeAccess.invokeCleaner(bybu);
        }

        @Override
        void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(null, baseAddress + index, bytes,
                    UnsafeArrayOps.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, length);
        }

        @Override
        public ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            final var baseAddress = UnsafeAccess.getLong(bybu, BYTE_BUFFER_ADDRESS_OFFSET);
            UnsafeAccess.copyMemory(bytes, UnsafeArrayOps.UnsafeOps.BYTE_ARRAY_BASE_OFFSET + offset, null,
                    baseAddress + index, length);
            return bybu;
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        final int write(ByteBuffer bybu, int index, Consumer<ByBuWriter> consumer) {
            final var bybuWriter = new SafeByteBufferWriter(bybu, index);
            consumer.accept(bybuWriter);
            return bybuWriter.writeIndex;
        }

        private static final class SafeByteBufferWriter implements ByBuWriter {
            
            private final ByteBuffer bybu;
            private int writeIndex;

            private SafeByteBufferWriter(ByteBuffer bybu, int startIndex) {
                this.bybu = bybu;
                this.writeIndex = startIndex;
            }

            @Override
            public void writeByte(byte value) {
                bybu.put(this.writeIndex++, value);
            }
        }

        @Override
        IndexedByBuReaderWriter readerWriter(ByteBuffer bybu) {
            return new SafeIndexedByBuReaderWriter(bybu);
        }

        private static final class SafeIndexedByBuReaderWriter implements IndexedByBuReaderWriter {

            private final ByteBuffer bybu;

            private SafeIndexedByBuReaderWriter(ByteBuffer bybu) {
                this.bybu = bybu;
            }

            @Override
            public byte readByteAt(long index) {
                return bybu.get((int) index);
            }

            @Override
            public int readIntAt(long index) {
                return bybu.getInt((int) index);
            }

            @Override
            public void writeByteAt(long index, byte value) {
                bybu.put((int) index, value);
            }

            @Override
            public void writeIntAt(long index, int value) {
                bybu.putInt((int) index, value);
            }

            @Override
            public byte[] toByteArray() {
                final var bytes = new byte[this.bybu.capacity()];
                // save previous position
                final var position = bybu.position();

                bybu.position(0);
                bybu.get(bytes);

                // re-affect previous position
                bybu.position(position);

                return bytes;
            }
        }

        // NOP without unsafe, just wait for garbage collection :)
        @Override
        void invokeCleaner(ByteBuffer bybu) {
        }

        @Override
        void fillTargetByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bybu.position();

            bybu.position(index);
            bybu.get(bytes, offset, length);

            // re-affect previous position
            bybu.position(position);
        }

        @Override
        public ByteBuffer fillWithByteArray(ByteBuffer bybu, int index, byte[] bytes, int offset, int length) {
            // save previous position
            final var position = bybu.position();

            bybu.position(index);
            bybu.put(bytes, offset, length);

            // re-affect previous position
            return bybu.position(position);
        }
    }
}
