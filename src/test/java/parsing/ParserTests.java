package parsing;

import me.chiranjeevikarthik.resp.parsing.Parser;
import org.json.JSONArray;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ParserTests {

    private static final Logger logger = LoggerFactory.getLogger(ParserTests.class);

    @Test
    public void Test1() {
        String respString = "*2\r\n$5\r\nHello\r\n$5\r\nworld\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]}]");
        logger.info("TEST1 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST1 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test2() {
        String respString = "*3\r\n$5\r\nPINGO\r\n$5\r\nPongo\r\n-Hello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"PINGO\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Pongo\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"Hello\"}]}]");
        logger.info("TEST2 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST2 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test3() {
        String respString = "$-1\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"0\",\"type\":\"NULL\",\"value\":\"BULK_STRING\"}]");
        logger.info("TEST3 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST3 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test4() {
        String respString = "$-1\r\n\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"0\",\"type\":\"NULL\",\"value\":\"BULK_STRING\"}]");
        logger.info("TEST4 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST4 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test5() {
        String respString = "+PONG\r\n\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PONG\"}]");
        logger.info("TEST5 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST5 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test6() {
        String respString = "+\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"0\",\"type\":\"SIMPLE_STRING\",\"value\":\"\"}]");
        logger.info("TEST6 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST6 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test7() {
        String respString = "$2\r\nHe";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"BULK_STRING\",\"value\":\"He\"}]");
        logger.info("TEST7 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST7 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test8() {
        String respString = "$2\r\nHello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST8 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST8 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test9() {
        String respString = "$\r\nHello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST9 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST9 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test10() {
        String respString = "$\r\nHello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST10 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST10 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test11() {
        String respString = "$5\r\nHello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]");
        logger.info("TEST11 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST11 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test12() {
        String respString = "$5\r\nworld\r\nWORLD\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]");
        logger.info("TEST12 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST12 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test13() {
        String respString = "+PING\r\n+PING\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"},{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"}]");
        logger.info("TEST13 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST13 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test14() {
        String respString = ":4848\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":4848}]");
        logger.info("TEST14 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST14 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test15() {
        String respString = ",4.0121\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"NA\",\"type\":\"DOUBLE\",\"value\":4.0121}]");
        logger.info("TEST15 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST15 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test16() {
        String respString = ",\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"0\",\"type\":\"DOUBLE\",\"value\":\"0.0\"}]");
        logger.info("TEST16 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST16 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test17() {
        String respString = ":\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":\"0\"}]");
        logger.info("TEST17 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST17 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test18() {
        String respString = "!21\r\nSYNTAX invalid syntax\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"21\",\"type\":\"BLOB_ERROR\",\"value\":\"SYNTAX invalid syntax\"}]");
        logger.info("TEST18 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST18 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test19() {
        String respString = "#t\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]");
        logger.info("TEST19 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST19 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test20() {
        String respString = "#f\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":false}]");
        logger.info("TEST20 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST20 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test21() {
        String respString = "#\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":false}]");
        logger.info("TEST21 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST21 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test22() {
        String respString = "*3\r\n$5\r\nHello\r\n:2345\r\n#t\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":2345},{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]}]");
        logger.info("TEST22 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST22 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test23() {
        String respString = "(12345678901234556777888224433434\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"NA\",\"type\":\"BIG_NUMBER\",\"value\":12345678901234556777888224433434}]");
        logger.info("TEST23 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST23 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test24() {
        String respString = "(\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"0\",\"type\":\"BIG_NUMBER\",\"value\":\"0\"}]");
        logger.info("TEST24 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST24 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test25() {
        String respString = "*2\r\n+Hello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST25 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST25 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test26() {
        String respString = "*2\r\n+Hello\r\n-World";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]");
        logger.info("TEST26 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST26 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    @Ignore // Need to check manually
    public void Test27() {
        String respString = "~3\r\n$5\r\nHello\r\n$5\r\nworld\r\n#t\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"3\",\"type\":\"SET\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"},{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]}]");
        logger.info("TEST27 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST27 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test28() {
        String respString = "*2\r\n+Hello\r\n*2\r\n+Hello\r\n-World";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]}]");
        logger.info("TEST28 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST28 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test29() {
        String respString = "=5\r\nHello";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"5\",\"type\":\"VERBATIM_STRING\",\"value\":\"Hello\"}]");
        logger.info("TEST29 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST29 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test30() {
        String respString = "=5\r\nHell";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST30 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST30 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test31() {
        String respString = "%2\r\n+first\r\n:1\r\n+second\r\n:2\r\n";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":2},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]");
        logger.info("TEST31 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST31 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test32() {
        String respString = "%2\r\n+first\r\n:1\r\n+second";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST32 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST32 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test33() {
        String respString = "%2\r\n+first\r\n:1\r\n+second\r\n*2\r\n+Hello\r\n-World";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]");
        logger.info("TEST33 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST33 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test34() {
        String respString = "%2\r\n+first\r\n:1\r\n*2\r\n+Hello\r\n-World\r\n+VALUE";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1},\"{\\\"length\\\":\\\"2\\\",\\\"type\\\":\\\"ARRAY\\\",\\\"value\\\":[{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"Hello\\\"},{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"ERROR_STRING\\\",\\\"value\\\":\\\"World\\\"}]}\":{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"VALUE\"}}}]");
        logger.info("TEST34 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST34 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test35() {
        String respString = "%2\r\n+first\r\n:1";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray();
        logger.info("TEST35 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST35 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }

    @Test
    public void Test36() {
        String respString = "%2\r\n+first\r\n:1\r\n+second\r\n*2\r\n+Hello\r\n+World";
        Parser parser = new Parser(respString);
        JSONArray parsedArray = parser.parse();
        JSONArray expectedOutput = new JSONArray("[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"World\"}]},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]");
        logger.info("TEST36 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        logger.info("TEST36 : Parsed Tree : {}", expectedOutput);
        assertEquals(expectedOutput.toString(), parsedArray.toString());
    }
}
