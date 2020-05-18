# &#x1f996; Dino &#x1f995;

[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)

* Dino is a IO library that works with one thread per IO operation, for example a multiplexed TCP server that runs with
Dino will create two threads for each client Socket : one thread for read operations and one thread for write operations.
* Dino is designed from the start to be used in conjunction with
[Project Loom virtual threads](https://wiki.openjdk.java.net/display/loom/Main) and
[JDK14 foreign memory](http://cr.openjdk.java.net/~mcimadamore/panama/memaccess_javadoc/jdk/incubator/foreign/package-summary.html).

<p align="center">
<a href="https://ufoss.org/dino/dino.html/">Read Documentation</a>
</p>

## Compile

To compile Dino project you will need a [Project Loom Early Access JDK](http://jdk.java.net/loom/).

## Other inspiring libraries
* [Quasar](https://github.com/puniverse/quasar), loom ancestor on JDK
* [Chronicle Bytes](https://github.com/OpenHFT/Chronicle-Bytes)
* [kotlinx-io](https://github.com/Kotlin/kotlinx-io)
* [Netty](https://github.com/netty/netty)
