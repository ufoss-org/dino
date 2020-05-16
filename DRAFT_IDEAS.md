## buffers

* use cache byte arrays for reading and writing (inspiration : private byte writeBuffer[] = new byte[8]; in DataOutPutStream)
* benchmark IO vs NIO for Sockets.
IO (with NioSocketImpl) uses InputStream and OutputStream to read and write byte arrays that are wrapped into native ByteBuffers.
This adds an operation that could affect performance.
* benchmark native memory performance : MemorySegment vs single ByteBuffer vs array of ByteBuffers.
* benchmark IO vs NIO vs foreign memory (than can store directly to a File) for Files : write content of a resource file to a temp file.
