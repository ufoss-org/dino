/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

import org.ufoss.dino.memory.OffHeapFactory;

module dino.core {
    requires jdk.unsupported;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    uses OffHeapFactory;

    exports org.ufoss.dino;
    exports org.ufoss.dino.memory;
    exports org.ufoss.dino.memory.impl;
    exports org.ufoss.dino.utils;
}
