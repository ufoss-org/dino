/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.concurrent;

import org.jetbrains.annotations.ApiStatus;

/**
 * Interrelated interfaces and static methods for establishing
 * flow-controlled components in which {@link Publisher Publishers}
 * produce items consumed by one or more {@link Subscriber Subscribers}.
 *
 * <p>These interfaces are a simplified version to the <a
 * href="http://www.reactive-streams.org/"> reactive-streams</a>
 * specification.
 * They apply in both concurrent and distributed
 * asynchronous settings: All (three) methods are defined in {@code
 * void} "one-way" message style.
 *
 * <p>Loom allows an imperative style code that greatly simplify Flow API by making blocking IO operations non-blocking
 * transparently without any change when running in a virtual thread.
 */
@ApiStatus.Experimental
public final class DinoFlow {

    // uninstantiable
    private DinoFlow() {
    }

    /**
     * A cold value publisher. Each subscriber must {@link Publisher#subscribe(Subscriber) subscribe} to start receiving
     * the elements from this
     *
     * @param <T> type of elements that are emitted to subscribers
     */
    @ApiStatus.Experimental
    @FunctionalInterface
    public interface Publisher<T> {

        /**
         * The parameter {@code subscriber} starts receiving elements when this method is called
         */
        void subscribe(Subscriber<? super T> subscriber);
    }

    /**
     * A Subscriber that starts receiving elements from a Publisher as soon as it is passed as parameter to
     * {@link Publisher#subscribe(Subscriber) publisher.subscribe(subscriber)}
     *
     * @param <T> type of elements that are received
     */
    @ApiStatus.Experimental
    @FunctionalInterface
    public interface Subscriber<T> {

        /**
         * Receive one value emitted by the Publisher
         * <p>This method is not thread-safe, it should not be invoked concurrently.
         */
        void receive(T item);
    }
}
