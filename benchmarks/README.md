# Benchmarks for Dino

This is also a place to test efficiency of concepts from other frameworks :
* hadoop-common provides Text class that stores String as UTF-8 byte[]
* protobuf-java provides ByteString that stores String as byte[] with interesting implementations LiteralByteString and RopeByteString

## JMH benchmark results

### DirectMemoryReadBenchmark

<table>
    <tr>
        <th>Benchmark</th>
        <th>Score</th>
        <th>Units</th>
    </tr>
    <tr>
        <td>directRead</td>
        <td>13936</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>indexedRead</td>
        <td>84239684</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>indexedReadMemorySegmentAssociated</td>
        <td>80988567</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>varhandleMemorySegmentReadGroupAndStruct</td>
        <td>903</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>varhandleRead</td>
        <td>1830</td>
        <td>ops/s</td>
    </tr>
</table>

### DirectMemoryWriteBenchmark
    
<table>
    <tr>
        <th>Benchmark</th>
        <th>Score</th>
        <th>Units</th>
    </tr>
    <tr>
        <td>directWrite</td>
        <td>337</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>indexedWrite</td>
        <td>485</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>indexedWriteMemorySegmentAssociated</td>
        <td>387</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>varhandleMemorySegmentWrite</td>
        <td>35</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>varhandleMemorySegmentWriteGroupAndStruct</td>
        <td>289</td>
        <td>ops/s</td>
    </tr>
    <tr>
        <td>varhandleWrite</td>
        <td>46</td>
        <td>ops/s</td>
    </tr>
</table>
