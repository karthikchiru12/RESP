# RESP

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/karthikchiru12/RESP/blob/master/LICENSE)  ![RESP :1.0](https://img.shields.io/badge/Version-v1.0-blue)


An implementation of RESP(Redis Serialization Protocol) for learning and for fun.

You can use package in your project by adding below maven dependency.

```
<dependency>
    <groupId>me.chiranjeevikarthik.resp</groupId>
    <artifactId>RESP</artifactId>
    <version>1.1</version>
</dependency>
```

##### Usage

```
import me.chiranjeevikarthik.resp.encoding.Encoder;
import me.chiranjeevikarthik.resp.parsing.Parser;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser("+HelloWOrld\r\n");
        System.out.println(parser.parse());
        //[{"length":"10","type":"SIMPLE_STRING","value":"HelloWOrld"}]
        Encoder encoder = new Encoder("[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"HELLO\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"WORLD\"}]}]");
        System.out.println(encoder.encode().replace("\r\n","\\r\\n"));
        //*2\r\n$5\r\nHELLO\r\n$5\r\nWORLD\r\n
    }
}
```

##### Supported data types
- SIMPLE_STRING
- BULK_STRING
- INTEGER
- DOUBLE
- ARRAY
- ERROR_STRING
- BLOB_ERROR
- BOOLEAN
- BIG_NUMBER
- SET
- VERBATIM_STRING
- MAP
