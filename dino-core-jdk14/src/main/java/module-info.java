/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

import org.ufoss.dino.internal.jdk14.memory.MemorySegmentOffHeapFactory;
import org.ufoss.dino.memory.OffHeapFactory;

@SuppressWarnings("module")
module dino.core.jdk14 {
    requires jdk.incubator.foreign;
    requires dino.core;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports org.ufoss.dino.jdk14.utils;

    provides OffHeapFactory with MemorySegmentOffHeapFactory;
}
