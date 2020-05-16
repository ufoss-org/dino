/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * This class exposes some operations provided by sun.misc.Unsafe
 */
public final class UnsafeAccess {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final sun.misc.Unsafe UNSAFE = getUnsafe();

    static final StringOffsets UNSAFE_STRING_OFFSETS = unsafeStringOffsets();
    static final boolean SUPPORT_UNSAFE_ARRAY_OPS = supportsUnsafeArrayOps();
    static final Long UNSAFE_BYTE_BUFFER_ADDRESS_OFFSET = unsafeByteBufferAddressOffset();

    // uninstanciable
    private UnsafeAccess() {
    }

    /**
     * @return the {@code sun.misc.Unsafe}, or {@code null} if not available
     */
    private static sun.misc.Unsafe getUnsafe() {
        sun.misc.Unsafe unsafe = null;
        try {
            unsafe = AccessController.doPrivileged(
                    (PrivilegedExceptionAction<sun.misc.Unsafe>) () -> {
                        final var unsafeClass = sun.misc.Unsafe.class;

                        for (final var f : unsafeClass.getDeclaredFields()) {
                            f.setAccessible(true);
                            final var x = f.get(null);
                            if (unsafeClass.isInstance(x)) {
                                logger.info("Your platform supports unsafe access");
                                return unsafeClass.cast(x);
                            }
                        }
                        return null;
                    });
        } catch (Throwable e) {
            // Catch Throwable to prevent all possible problem
            logger.warn("Your platform does not support unsafe access ", e);
        }
        return unsafe;
    }

    private static boolean supportsUnsafeArrayOps() {
        if (UNSAFE == null) {
            return false;
        }
        try {
            final var unsafeClass = UNSAFE.getClass();
            unsafeClass.getMethod("objectFieldOffset", Field.class);
            unsafeClass.getMethod("arrayBaseOffset", Class.class);
            unsafeClass.getMethod("arrayIndexScale", Class.class);
            unsafeClass.getMethod("getInt", Object.class, long.class);
            unsafeClass.getMethod("putInt", Object.class, long.class, int.class);
            unsafeClass.getMethod("getLong", Object.class, long.class);
            unsafeClass.getMethod("putLong", Object.class, long.class, long.class);
            unsafeClass.getMethod("getObject", Object.class, long.class);
            unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
            unsafeClass.getMethod("getByte", Object.class, long.class);
            unsafeClass.getMethod("putByte", Object.class, long.class, byte.class);
            unsafeClass.getMethod("getBoolean", Object.class, long.class);
            unsafeClass.getMethod("putBoolean", Object.class, long.class, boolean.class);
            unsafeClass.getMethod("getFloat", Object.class, long.class);
            unsafeClass.getMethod("putFloat", Object.class, long.class, float.class);
            unsafeClass.getMethod("getDouble", Object.class, long.class);
            unsafeClass.getMethod("putDouble", Object.class, long.class, double.class);

            logger.info("Your platform supports unsafe array operations");
            return true;
        } catch (Throwable e) {
            logger.warn("Your platform does not support unsafe array operations, will use safe way : {}", e.getMessage());
        }
        return false;
    }

    private static Long unsafeByteBufferAddressOffset() {
        if (UNSAFE == null) {
            return null;
        }
        try {
            final var unsafeClass = UNSAFE.getClass();
            unsafeClass.getMethod("objectFieldOffset", Field.class);
            unsafeClass.getMethod("getLong", Object.class, long.class);
            unsafeClass.getMethod("getByte", long.class);
            unsafeClass.getMethod("putByte", long.class, byte.class);
            unsafeClass.getMethod("getInt", long.class);
            unsafeClass.getMethod("putInt", long.class, int.class);
            unsafeClass.getMethod("getLong", long.class);
            unsafeClass.getMethod("putLong", long.class, long.class);
            unsafeClass.getMethod("copyMemory", long.class, long.class, long.class);
            unsafeClass.getMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
            unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);

            // must have access to address field of Buffer class
            final var field = getField(Buffer.class, "address");
            if (field == null || field.getType() != long.class) {
                return null;
            }
            logger.info("Your platform supports unsafe ByteBuffer operations");
            return UNSAFE.objectFieldOffset(field);
        } catch (Throwable e) {
            logger.warn("Your platform does not support unsafe ByteBuffer operations, will use safe way : {}", e.getMessage());
        }
        return null;
    }

    static final class StringOffsets {
        final long coderFieldOffset;
        final long bytesFieldOffset;
        final long hashFieldOffset;

        StringOffsets(long coderFieldOffset, long bytesFieldOffset, long hashFieldOffset) {
            this.coderFieldOffset = coderFieldOffset;
            this.bytesFieldOffset = bytesFieldOffset;
            this.hashFieldOffset = hashFieldOffset;
        }
    }

    private static StringOffsets unsafeStringOffsets() {
        if (UNSAFE == null) {
            return null;
        }
        try {
            final var unsafeClass = UNSAFE.getClass();
            unsafeClass.getMethod("objectFieldOffset", Field.class);
            unsafeClass.getMethod("getInt", Object.class, long.class);
            unsafeClass.getMethod("putInt", Object.class, long.class, int.class);
            unsafeClass.getMethod("getObject", Object.class, long.class);
            unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
            unsafeClass.getMethod("getByte", Object.class, long.class);
            unsafeClass.getMethod("putByte", Object.class, long.class, byte.class);
            unsafeClass.getMethod("allocateInstance", Class.class);

            // must have access to coder, value and hash fields of String class
            var field = getField(String.class, "coder");
            if (field == null || field.getType() != byte.class) {
                return null;
            }
            final var coderFieldOffset = UNSAFE.objectFieldOffset(field);
            field = getField(String.class, "value");
            if (field == null || field.getType() != byte[].class) {
                return null;
            }
            final var bytesFieldOffset = UNSAFE.objectFieldOffset(field);
            field = getField(String.class, "hash");
            if (field == null || field.getType() != int.class) {
                return null;
            }

            logger.info("Your platform supports unsafe String operations");
            return new StringOffsets(coderFieldOffset, bytesFieldOffset, UNSAFE.objectFieldOffset(field));
        } catch (Throwable e) {
            logger.warn("Your platform does not support unsafe String operations, will use safe way : {}", e.getMessage());
        }
        return null;
    }

    /**
     * @return the field with this name in provided class, or null if not found.
     */
    private static Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Throwable t) {
            // Failed to access the field
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T allocateInstance(Class<T> clazz) throws InstantiationException {
        return (T) UNSAFE.allocateInstance(clazz);
    }

    static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    static byte getByte(Object target, long offset) {
        return UNSAFE.getByte(target, offset);
    }

    static void putByte(Object target, long offset, byte value) {
        UNSAFE.putByte(target, offset, value);
    }

    static int getInt(Object target, long offset) {
        return UNSAFE.getInt(target, offset);
    }

    static void putInt(Object target, long offset, int value) {
        UNSAFE.putInt(target, offset, value);
    }

    static long getLong(Object target, long offset) {
        return UNSAFE.getLong(target, offset);
    }

    static void putLong(Object target, long offset, long value) {
        UNSAFE.putLong(target, offset, value);
    }

    static Object getObject(Object target, long offset) {
        return UNSAFE.getObject(target, offset);
    }

    static void putObject(Object target, long offset, Object value) {
        UNSAFE.putObject(target, offset, value);
    }

    static int arrayBaseOffset(Class<?> clazz) {
        return UNSAFE.arrayBaseOffset(clazz);
    }

    static void invokeCleaner(ByteBuffer bb) {
        UNSAFE.invokeCleaner(bb);
    }

    static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        UNSAFE.copyMemory(srcBase, srcOffset, destBase, destOffset, bytes);
    }
}
