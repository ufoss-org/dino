/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.various.string;

import org.apache.hadoop.io.Text;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringCharsetTests {

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
    @DisplayName("check that a ASCII String has same length in UTF-8 than number of characters")
    void utf8_ascii_text() {
        final var utf8 = new Text(ASCII);
        assertThat(utf8.getLength())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("check that a ISO_8859_1 non ASCII String has not same length in UTF-8 than number of characters")
    void utf8_non_ascii_text() {
        final var utf8 = new Text(ISO_8859_1);
        assertThat(utf8.getLength())
                .isEqualTo(2);
    }
}
