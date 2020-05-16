/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import org.ufoss.dino.memory.ByBuOffHeap;
import org.ufoss.dino.memory.OffHeapFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Util class providing unsafe optimised operations on String (fallback to safe if unsafe is not supported)
 */
public final class UnsafeStringOps {

    private static final Ops OPS = (UnsafeAccess.UNSAFE_STRING_OFFSETS != null) ? new UnsafeOps() : new SafeOps();

    // uninstanciable
    private UnsafeStringOps() {
    }

    public static Result encode(String string, Charset charset) {
        return OPS.encode(string, charset);
    }

    public static String toLatin1String(byte[] bytes) {
        return OPS.toLatin1String(bytes);
    }

    public static String toUtf16String(byte[] bytes) {
        return OPS.toUtf16String(bytes);
    }

    private static abstract class Ops {
        /**
         * Never change the returned byte array in Result
         * <p>would break String parameter's immutability when bytes were obtained via Unsafe call !
         */
        abstract Result encode(String string, Charset charset);

        /**
         * Never change the parameter byte array
         * <p>would break String's immutability when using unsafe instantiation !
         */
        abstract String toLatin1String(byte[] bytes);

        /**
         * Never change the parameter byte array
         * <p>would break String's immutability when using unsafe instantiation !
         */
        abstract String toUtf16String(byte[] bytes);
    }

    static final class UnsafeOps extends Ops {

        private static final byte LATIN1 = 0;
        private static final byte UTF16 = 1;
        private static final UnsafeAccess.StringOffsets STRING_OFFSETS = UnsafeAccess.UNSAFE_STRING_OFFSETS;

        static boolean isLatin1(String string) {
            return UnsafeAccess.getByte(string, STRING_OFFSETS.coderFieldOffset) == LATIN1;
        }

        static byte[] getBytes(String string) {
            return (byte[]) UnsafeAccess.getObject(string, STRING_OFFSETS.bytesFieldOffset);
        }

        @Override
        final Result encode(String string, Charset charset) {
            // fast-path 1) UTF-8 is the most widely used charset, it must be top priority !
            if (charset == StandardCharsets.UTF_8) {
                return encodeUtf8(string);
            }

            // fast-path 2) ISO_8859_1 is vastly used too
            if (charset == StandardCharsets.ISO_8859_1) {
                return encodeLatin1(string);
            }

            // fast-path 3) ASCII
            if (charset == StandardCharsets.US_ASCII) {
                return encodeAscii(string);
            }

            // ASCII compatible target charset require special path
            if (charset.contains(StandardCharsets.US_ASCII)) {
                // Check for Latin1 (ISO_8859_1) compact String via Unsafe call
                if (isLatin1(string)) {
                    // get Latin1 byte array directly from String via Unsafe call
                    final var bytes = getBytes(string);

                    // 1) all bytes are Ascii
                    if (!AsciiOps.hasNegatives(bytes)) {
                        return new Result().withAscii(bytes);
                    }

                    // 2) UTF-8 is ASCII compatible but is NOT ISO_8859_1 compatible,
                    // must encode this non Ascii Latin1 byte array to UTF-8
                    if (charset.contains(StandardCharsets.UTF_8)) {
                        return new Result().withNotAsciiNotLatin1Utf8(Latin1Ops.encodelatinBytesToUTF8(bytes));
                    }

                    // 3) Required charset is ASCII compatible but is not UTF-8 compatible,
                    // must encode this non Ascii String to target charset
                }
            }

            // encode String to target charset
            return new Result().withNotAsciiNotLatin1(string.getBytes(charset), charset);
        }

        private static Result encodeUtf8(String string) {
            // Check for Latin1 (ISO_8859_1) compact String via Unsafe call
            if (isLatin1(string)) {
                // get Latin1 byte array directly from String via Unsafe call
                final var bytes = getBytes(string);

                // 1) all bytes are Ascii
                if (!AsciiOps.hasNegatives(bytes)) {
                    return new Result().withAscii(bytes);
                }

                // 2) UTF-8 is ASCII compatible but is NOT ISO_8859_1 compatible,
                // must encode this non Ascii Latin1 bytes to UTF-8
                return new Result().withNotAsciiNotLatin1Utf8(Latin1Ops.encodelatinBytesToUTF8(bytes));
            }
            // encode this not Latin1 String to target charset
            return new Result().withNotAsciiNotLatin1(string.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }

        private static Result encodeLatin1(String string) {
            // Check for Latin1 (ISO_8859_1) compact String via Unsafe call
            if (isLatin1(string)) {
                // get Latin1 byte array directly from String via Unsafe call
                final var bytes = getBytes(string);

                // 1) all bytes are Ascii
                if (!AsciiOps.hasNegatives(bytes)) {
                    return new Result().withAscii(bytes);
                }

                // 2) this byte array is Latin1 (ISO_8859_1) but not Ascii
                return new Result().withNotAsciiLatin1(bytes);
            }

            // We know this String is not fully compatible with Latin1 (ISO_8859_1), use best-effort String method
            // String.getBytes(StandardCharsets.ISO_8859_1) to encode it anyway
            return new Result().withNotAsciiLatin1(string.getBytes(StandardCharsets.ISO_8859_1));
        }

        private static Result encodeAscii(String string) {
            // Check for Latin1 (ISO_8859_1) compact String via Unsafe call
            if (isLatin1(string)) {
                // get Latin1 bytes directly from String via Unsafe call
                final var bytes = getBytes(string);

                // 1) all bytes are Ascii
                if (!AsciiOps.hasNegatives(bytes)) {
                    return new Result().withAscii(bytes);
                }
            }

            // We know this String is not fully compatible with ASCII, use best-effort String method
            // String.getBytes(StandardCharsets.US_ASCII) to encode it anyway
            return new Result().withAscii(string.getBytes(StandardCharsets.US_ASCII));
        }

        /**
         * This is a unsafe Latin1 String builder
         */
        @Override
        final String toLatin1String(byte[] bytes) {
            // create String instance via allocateInstance (do not call any constructor) -> this is risky !
            final String string;
            try {
                string = UnsafeAccess.allocateInstance(String.class);
            } catch (Throwable t) {
                // should never happen
                return new String(bytes, StandardCharsets.ISO_8859_1);
            }

            // init fields
            UnsafeAccess.putObject(string, STRING_OFFSETS.bytesFieldOffset, bytes);
            UnsafeAccess.putByte(string, STRING_OFFSETS.coderFieldOffset, LATIN1);
            UnsafeAccess.putInt(string, STRING_OFFSETS.hashFieldOffset, 0);

            return string;
        }

        /**
         * This is a unsafe UTF-16 String builder
         */
        @Override
        final String toUtf16String(byte[] bytes) {
            // create String instance via allocateInstance (do not call any constructor) -> this is risky !
            final String string;
            try {
                string = UnsafeAccess.allocateInstance(String.class);
            } catch (Throwable t) {
                // should never happen
                return new String(bytes, StandardCharsets.UTF_16);
            }

            // init fields
            UnsafeAccess.putObject(string, STRING_OFFSETS.bytesFieldOffset, bytes);
            UnsafeAccess.putByte(string, STRING_OFFSETS.coderFieldOffset, UTF16);
            UnsafeAccess.putInt(string, STRING_OFFSETS.hashFieldOffset, 0);

            return string;
        }
    }

    private static final class SafeOps extends Ops {

        @Override
        final Result encode(String string, Charset charset) {
            // ASCII compatible target charset require special path
            if (charset.contains(StandardCharsets.US_ASCII)) {
                final var bytes = string.getBytes(charset);

                // 1) ASCII bytes
                if (!AsciiOps.hasNegatives(bytes)) {
                    return new Result().withAscii(bytes);
                }

                // 2) non ASCII bytes, target charset is ISO_8859_1
                if (charset == StandardCharsets.ISO_8859_1) {
                    return new Result().withNotAsciiLatin1(bytes);
                }

                // 3) Charset is ASCII compatible but is not ISO_8859_1
                return new Result().withNotAsciiNotLatin1(bytes, charset);
            }
            // classic encode for non Ascii compatible charset
            return new Result().withNotAsciiNotLatin1(string.getBytes(charset), charset);
        }

        @Override
        final String toLatin1String(byte[] bytes) {
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }

        @Override
        String toUtf16String(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_16);
        }
    }

    public static class Result {

        private ByBuOffHeap bbMemory;
        private boolean isAscii;
        private boolean isLatin1;
        private Charset charset;

        Result withAscii(byte[] bytes) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = true;
            this.isLatin1 = true;
            this.charset = StandardCharsets.US_ASCII;
            return this;
        }

        Result withNotAsciiLatin1(byte[] bytes) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = false;
            this.isLatin1 = true;
            this.charset = StandardCharsets.ISO_8859_1;
            return this;
        }

        public Result withNotAsciiNotLatin1Utf8(ByBuOffHeap bbMemory) {
            this.bbMemory = bbMemory;
            this.isAscii = false;
            this.isLatin1 = false;
            this.charset = StandardCharsets.UTF_8;
            return this;
        }

        Result withNotAsciiNotLatin1(byte[] bytes, Charset charset) {
            this.bbMemory = OffHeapFactory.of(bytes);
            this.isAscii = false;
            this.isLatin1 = false;
            this.charset = charset;
            return this;
        }

        public ByBuOffHeap getBbMemory() {
            return bbMemory;
        }

        public boolean isAscii() {
            return isAscii;
        }

        public boolean isLatin1() {
            return isLatin1;
        }

        public Charset getCharset() {
            return charset;
        }
    }
}
