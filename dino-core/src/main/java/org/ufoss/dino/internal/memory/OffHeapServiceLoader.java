/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

package org.ufoss.dino.internal.memory;

import org.ufoss.dino.memory.OffHeapFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public final class OffHeapServiceLoader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final OffHeapFactory OFF_HEAP_FACTORY = getOffHeapFactory();


    // uninstanciable
    private OffHeapServiceLoader() {
    }

    private static OffHeapFactory getOffHeapFactory() {
        final var iterator = ServiceLoader.load(OffHeapFactory.class).iterator();

        final var offHeapFactory = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),false)
                .max(Comparator.comparingInt(OffHeapFactory::getLoadPriority))
                // If no module in classpath implements this Service, fallback to BaseByteBufferOffHeapFactory
                .orElse(new BaseByteBufferOffHeapFactory());
        logger.info("ServiceLoader<OffHeapFactory> loaded : {}", offHeapFactory.getClass().getTypeName());
        return offHeapFactory;
    }
}
