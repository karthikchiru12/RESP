package encoding;

import me.chiranjeevikarthik.resp.encoding.Encoder;
import org.json.JSONArray;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class EncodingTests {

    private static final Logger logger = LoggerFactory.getLogger(EncodingTests.class);

    @Test
    public void Test1() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "*2\r\n$5\r\nHello\r\n$5\r\nworld\r\n";
        logger.info("TEST1 : Parsed Tree : {}", parsedTree);
        logger.info("TEST1 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test2() {
        String parsedTree = "[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"PINGO\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Pongo\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"Hello\"}]}]\n";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "*3\r\n$5\r\nPINGO\r\n$5\r\nPongo\r\n-Hello\r\n";
        logger.info("TEST2 : Parsed Tree : {}", parsedTree);
        logger.info("TEST2 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test3() {
        String parsedTree = "[{\"length\":\"0\",\"type\":\"NULL\",\"value\":\"BULK_STRING\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "$-1\r\n";
        logger.info("TEST3 : Parsed Tree : {}", parsedTree);
        logger.info("TEST3 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test4() {
        String parsedTree = "[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PONG\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "+PONG\r\n";
        logger.info("TEST4 : Parsed Tree : {}", parsedTree);
        logger.info("TEST4 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test5() {
        String parsedTree = "[{\"length\":\"0\",\"type\":\"SIMPLE_STRING\",\"value\":\"\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "+\r\n";
        logger.info("TEST5 : Parsed Tree : {}", parsedTree);
        logger.info("TEST5 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test6() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"BULK_STRING\",\"value\":\"He\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "$2\r\nHe\r\n";
        logger.info("TEST6 : Parsed Tree : {}", parsedTree);
        logger.info("TEST6 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test7() {
        String parsedTree = "[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "$5\r\nHello\r\n";
        logger.info("TEST7 : Parsed Tree : {}", parsedTree);
        logger.info("TEST7 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test8() {
        String parsedTree = "[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "$5\r\nworld\r\n";
        logger.info("TEST8 : Parsed Tree : {}", parsedTree);
        logger.info("TEST8 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test9() {
        String parsedTree = "[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"},{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "+PING\r\n+PING\r\n";
        logger.info("TEST9 : Parsed Tree : {}", parsedTree);
        logger.info("TEST9 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test10() {
        String parsedTree = "[{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":4848}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = ":4848\r\n";
        logger.info("TEST10 : Parsed Tree : {}", parsedTree);
        logger.info("TEST10 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test11() {
        String parsedTree = "[{\"length\":\"NA\",\"type\":\"DOUBLE\",\"value\":4.0121}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = ",4.0121\r\n";
        logger.info("TEST11 : Parsed Tree : {}", parsedTree);
        logger.info("TEST11 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test12() {
        String parsedTree = "[{\"length\":\"NA\",\"type\":\"DOUBLE\",\"value\":0.0}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = ",0.0\r\n";
        logger.info("TEST12 : Parsed Tree : {}", parsedTree);
        logger.info("TEST12 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test13() {
        String parsedTree = "[{\"length\":\"21\",\"type\":\"BLOB_ERROR\",\"value\":\"SYNTAX invalid syntax\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "!21\r\nSYNTAX invalid syntax\r\n";
        logger.info("TEST13 : Parsed Tree : {}", parsedTree);
        logger.info("TEST13 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test14() {
        String parsedTree = "[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "#t\r\n";
        logger.info("TEST14 : Parsed Tree : {}", parsedTree);
        logger.info("TEST14 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test15() {
        String parsedTree = "[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":false}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "#f\r\n";
        logger.info("TEST15 : Parsed Tree : {}", parsedTree);
        logger.info("TEST15 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test16() {
        String parsedTree = "[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"},{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "*3\r\n$5\r\nHello\r\n$5\r\nworld\r\n#t\r\n";
        logger.info("TEST16 : Parsed Tree : {}", parsedTree);
        logger.info("TEST16 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test17() {
        String parsedTree = "[{\"length\":\"NA\",\"type\":\"BIG_NUMBER\",\"value\":12345678901234556777888224433434}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "(12345678901234556777888224433434\r\n";
        logger.info("TEST17 : Parsed Tree : {}", parsedTree);
        logger.info("TEST17 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test18() {
        String parsedTree = "[{\"length\":\"0\",\"type\":\"BIG_NUMBER\",\"value\":\"0\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "(0\r\n";
        logger.info("TEST18 : Parsed Tree : {}", parsedTree);
        logger.info("TEST18 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test19() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "*2\r\n+Hello\r\n-World\r\n";
        logger.info("TEST19 : Parsed Tree : {}", parsedTree);
        logger.info("TEST19 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test20() {
        String parsedTree = "[{\"length\":\"3\",\"type\":\"SET\",\"value\":[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "~3\r\n#t\r\n$5\r\nworld\r\n$5\r\nHello\r\n";
        logger.info("TEST20 : Parsed Tree : {}", parsedTree);
        logger.info("TEST20 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test21() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "*2\r\n+Hello\r\n*2\r\n+Hello\r\n-World\r\n";
        logger.info("TEST21 : Parsed Tree : {}", parsedTree);
        logger.info("TEST21 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test22() {
        String parsedTree = "[{\"length\":\"5\",\"type\":\"VERBATIM_STRING\",\"value\":\"Hello\"}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "=5\r\nHello\r\n";
        logger.info("TEST22 : Parsed Tree : {}", parsedTree);
        logger.info("TEST22 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test23() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":2},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "%2\r\n+second\r\n:2\r\n+first\r\n:1\r\n";
        logger.info("TEST23 : Parsed Tree : {}", parsedTree);
        logger.info("TEST23 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }

    @Test
    public void Test24() {
        String parsedTree = "[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]";
        Encoder encoder = new Encoder(new JSONArray(parsedTree));
        String respString = encoder.encode();
        String expectedOutput = "%2\r\n+second\r\n*2\r\n+Hello\r\n-World\r\n+first\r\n:1\r\n";
        logger.info("TEST24 : Parsed Tree : {}", parsedTree);
        logger.info("TEST24 : Resp String : {}", respString.replaceAll("\r\n", "\\\\r\\\\n"));
        assertEquals(expectedOutput, respString);
    }
}
