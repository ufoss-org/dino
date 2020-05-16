/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.memory;

import org.jetbrains.annotations.NotNull;

/**
 * This interface represents a contiguous off-heap memory region of size < Long.MAX_VALUE.
 * <p>OffHeap and all of its sub-types offers strong safety :
 * <ul>
 *     <li>All operations make sure that we only access the content of this memory region</li>
 *     <li>No operation will succeed after a off-heap memory has been closed (see {@link OffHeap#close()}</li>
 * </ul>
 * <p>off-heap memory must be closed explicitly (see {@link OffHeap#close()}).
 * When a off-heap memory is closed, all off-heap resources associated with it are released; this has different meanings
 * depending on the kind of memory being considered:
 * <ul>
 *      <li>closing a native off-heap memory results in <em>freeing</em> the native memory associated with it</li>
 *      <li>closing a mapped off-heap memory results in the backing memory-mapped file to be unmapped</li>
 *      <li>closing an view of a off-heap memory (obtained via slice, acquire) <b>does not</b> result in the release of
 *      resources</li>
 * </ul>
 *
 * <h2>Thread confinement</h2>
 *
 *  Off-heap memory support strong thread-confinement guarantees. Upon creation, they are assigned an
 *  <em>owner thread</em>, typically the thread which initiated the creation operation. After creation, only the owner
 *  thread will be allowed to directly manipulate the memory (e.g. close it) or access (e.g. read or write in it) the
 *  memory region. Any attempt to perform such operations from a thread other than the owner thread will result in
 *  throwing a {@link IllegalStateException}.
 */
public interface OffHeap extends AutoCloseable {

    /**
     * @return the byte size of this off-heap memory region
     */
    long getByteSize();

    /**
     * Obtains a new off-heap memory view whose base address is the same as the base address of this memory plus a given
     * offset, new size is specified by the given argument.
     *
     * @param offset The new memory base offset (relative to the current memory base address), specified in bytes.
     * @param length The new memory length, specified in bytes.
     * @return a new OffHeap view with updated base position and limit addresses.
     * @throws IndexOutOfBoundsException if {@code offset} is less than {@code 0}, {@code offset} is greater than {@code byteSize},
     *                                   {@code length} is less than {@code 0}, or {@code length} is greater than {@code byteSize - offset}
     * @throws IllegalStateException     if this memory has been closed, or if access occurs from a thread other than the
     *                                   thread owning this memory.
     */
    @NotNull OffHeap slice(long offset, long length);

    /**
     * Obtains an acquired OffHeap which can be used to access memory associated
     * with this OffHeap from the current thread. As a side-effect, this memory cannot be closed until the acquired
     * view has been closed too (see {@link #close()}).
     * @return an an acquired off-heap memory which can be used to access memory associated
     * with this OffHeap from the current thread
     * @throws IllegalStateException if this memory has been closed.
     */
    @NotNull OffHeap acquire();

    /**
     * @return a ByBuOffHeap (off-heap memory represented by a direct ByteBuffer) bound to the same memory region as
     * this OffHeap
     * @throws UnsupportedOperationException if this off-heap memory's contents do not fit into a {@link ByBuOffHeap}
     *                                       instance, e.g. its size is greater than {@link Integer#MAX_VALUE}.
     * @throws IllegalStateException         if this memory has been closed, or if access occurs from a thread other
     *                                       than the thread owning this memory.
     */
    @NotNull ByBuOffHeap asByBuOffHeap();

    /**
     * Copy the contents of this off-heap memory into a fresh byte array.
     *
     * @return a fresh byte array copy of this off-heap memory.
     * @throws UnsupportedOperationException if this off-heap memory's contents cannot be copied into a {@code byte[]}
     *                                       instance, e.g. its size is greater than {@link Integer#MAX_VALUE}.
     * @throws IllegalStateException         if this memory has been closed, or if access occurs from a thread other
     *                                       than the thread owning this memory.
     */
    byte @NotNull [] toByteArray();

    /**
     * Closes this off-heap memory. Once a off-heap memory has been closed, any attempt to use the off-heap memory,
     * or to access the memory associated with the it will fail with {@link IllegalStateException}.
     * Calling this method trigger deallocation of the off-heap memory.
     *
     * @throws IllegalStateException if this memory has been closed, or if access occurs from a thread other than the
     *                               thread owning this memory
     */
    @Override
    void close();

    /**
     * Read a byte at the specified absolute {@code index}
     *
     * @implNote No index check !
     */
    byte readByteAt(long index);

    /**
     * Read a 4-bytes int at the specified absolute {@code index}
     * <p>bytes are using BIG ENDIAN byte order
     *
     * @implNote No index check !
     */
    int readIntAt(long index);

    /**
     * Read a 4-bytes int at the specified absolute {@code index}
     * <p>bytes are using LITTLE ENDIAN byte order
     *
     * @implNote No index check !
     */
    int readIntAtLE(long index);
}
