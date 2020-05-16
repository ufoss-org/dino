/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

module dino.transfer {
    requires jdk.incubator.foreign;
    requires dino.core;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports org.ufoss.dino.transfer;
}
