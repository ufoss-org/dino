/*
 * This is free and unencumbered software released into the public domain, following <https://unlicense.org>
 */

@SuppressWarnings("module")
open module dino.core.fixtures {
    requires org.junit.jupiter.api;
    requires org.assertj.core;
    requires dino.core;

    exports org.ufoss.dino.fixtures.memory;
}
