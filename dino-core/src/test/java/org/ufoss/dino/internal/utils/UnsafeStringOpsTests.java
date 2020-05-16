/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnsafeStringOpsTests {

    /**
     * ASCII Format
     * Each character is stored with a sing byte (using 7 bits)
     */
    private final static String ASCII = "1";

    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     * Each character is stored with a sing byte
     * <p>This String contains a non ASCII char
     */
    private final static String ISO_8859_1 = "¡";

    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark.
     * Each character is stored with 2 bytes
     */
    private final static String UTF_16 = "€";

    @Test
    @DisplayName("check that a ISO_8859_1 String isLatin true using Compact String String#isLatin1 of JDK9")
    void isISO_8859_1() {
        assertThat(UnsafeStringOps.UnsafeOps.isLatin1(ISO_8859_1))
            .isTrue();
    }

    @Test
    @DisplayName("check that a non ISO_8859_1 String isLatin false using Compact String String#isLatin1 of JDK9")
    void isNotISO_8859_1() {
        assertThat(UnsafeStringOps.UnsafeOps.isLatin1(UTF_16))
            .isFalse();
    }
}
