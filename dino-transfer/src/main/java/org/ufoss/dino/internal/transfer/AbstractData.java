/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.transfer;

import org.ufoss.dino.transfer.Data;

abstract class AbstractData implements Data {

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void indexCheck(long index, int limit, int requestedLength) {
        if ((index | limit - index - requestedLength) < 0) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is less than 0 or greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }

    /**
     * Checks that there is at least 'requestedLength' bytes available
     */
    protected static void existingIndexCheck(int index, int limit, int requestedLength) {
        if (index > limit - requestedLength) {
            throw new IndexOutOfBoundsException(
                    String.format("requested index=%d is greater than (limit=%d - %d)",
                            index, limit, requestedLength));
        }
    }
}
