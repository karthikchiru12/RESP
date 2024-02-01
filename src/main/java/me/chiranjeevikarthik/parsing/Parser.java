package me.chiranjeevikarthik.parsing;

import me.chiranjeevikarthik.constants.ParserConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.*;


public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    private final List<String> dataStore;

    private final JSONArray parsedTree = new JSONArray();
    private int currentParseIndex = 0;

    private String stringData;

    private InputStream inputStream;
    private int length = 0;

    public Parser(String stringData) {
        this.stringData = stringData;
        this.dataStore = this.formatStringData();
    }

    public Parser(InputStream inputStream) {
        this.inputStream = inputStream;
        this.dataStore = this.readAndFormatInputStream();
    }

    public static void main(String[] args) {
        List<String> tests = new ArrayList<>(Arrays.asList("*2\r\n$5\r\nHello\r\n$5\r\nworld\r\n", "*3\r\n$5\r\nPINGO\r\n$5\r\nPongo\r\n-Hello",
                "$-1\r\n", "$-1\r\n\r\n", "+PONG\r\n\r\n", "+PONG\r\n", "+\r\n", "$2\r\nHe", "$2\r\nHello", "$\r\nHello", "$5\r\nHello", "$5\r\nworld\r\nWORLD\r\n", "+PING\r\n+PING\r\n", ":4848\r\n", ",4.0121\r\n",
                "!21\r\nSYNTAX invalid syntax\r\n", "#t\r\n", "#f\r\n", "#\r\n", "*3\r\n$5\r\nHello\r\n$5\r\nworld\r\n#t\r\n",
                "(12345678901234556777888224433434\r\n", "(\r\n", "*2\r\n+Hello","*2\r\n+Hello\r\n-World","~3\r\n$5\r\nHello\r\n$5\r\nworld\r\n#t\r\n",
                "*2\r\n+Hello\r\n*2\r\n+Hello\r\n-World"));
        for (String test : tests) {
            Parser parser = new Parser(test);
            System.out.println("TEST : " + test.replaceAll("\r\n", "\\\\r\\\\n"));
            System.out.println("RESULT : " + parser.parse());
            System.out.println("#########$$$$$$$$$$$$$$$$$\n");

        }

    }

    private List<String> formatStringData() {
        List<String> respTokens = new ArrayList<>();
        if (this.stringData != null) {
            String[] tokens = this.stringData.split(ParserConstants.CRLF);
            for (String token : tokens) {
                respTokens.add(token);
                this.length++;
            }
            return respTokens;
        }
        return Collections.emptyList();
    }

    private List<String> readAndFormatInputStream() {
        try {
            List<String> respTokens = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                respTokens.add(line);
            }
            return respTokens;
        } catch (IOException ioException) {
            logger.error("IOException occurred while reading and formatting input stream: ", ioException);
        }

        return Collections.emptyList();
    }

    private JSONObject parseData() {
        try {
            String token = this.dataStore.get(this.currentParseIndex);

            if (token.startsWith(ParserConstants.SIMPLE_STRING)) {
                return this.parseSimpleString();
            } else if (token.startsWith(ParserConstants.BULK_STRING)) {
                return this.parseBulkString();
            } else if (token.startsWith(ParserConstants.INTEGER)) {
                return this.parseInteger();
            } else if (token.startsWith(ParserConstants.ERROR_STRING)) {
                return this.parseErrorString();
            } else if (token.startsWith(ParserConstants.DOUBLE)) {
                return this.parseDouble();
            } else if (token.startsWith(ParserConstants.ARRAY)) {
                return this.parseArray();
            } else if (token.startsWith(ParserConstants.BLOB_ERROR)) {
                return this.parseBlobError();
            } else if (token.startsWith(ParserConstants.BOOLEAN)) {
                return this.parseBoolean();
            } else if (token.startsWith(ParserConstants.BIG_NUMBER)) {
                return this.parseBigNumber();
            } else if (token.startsWith(ParserConstants.SET)) {
                return this.parseSet();
            } else {
                throw new IllegalStateException(String.format("The token %s is not a valid token", token));
            }

        } catch (Exception exception) {
            logger.error("Exception occurred : ", exception);
        }
        return null;
    }

    public JSONArray parse() {

        while (this.currentParseIndex < this.dataStore.size()) {
            JSONObject data = this.parseData();
            if (data != null) {
                this.parsedTree.put(data);
            }
            this.currentParseIndex++;
        }

        return this.parsedTree;
    }

    private JSONObject buildParsedTreeItem(String type, Object value, String length) {
        return new JSONObject()
                .put("type", type)
                .put("value", value)
                .put("length", length);
    }

    private JSONObject parseBoolean() {
        String token = this.dataStore.get(this.currentParseIndex);
        String booleanString = null;
        if (token.length() >= 2) {
            booleanString = token.substring(1);
            if (booleanString.equalsIgnoreCase("t") || booleanString.equalsIgnoreCase("f")) {
                boolean booleanValue = booleanString.equalsIgnoreCase("t");
                return this.buildParsedTreeItem("BOOLEAN", booleanValue, "1");
            } else {
                throw new IllegalStateException(String.format("The boolean value is not valid i.e %s", booleanString));
            }
        } else {
            logger.warn("The boolean value is empty, so defaulting it to false");
            return this.buildParsedTreeItem("BOOLEAN", false, "1");

        }
    }

    private JSONObject parseSimpleString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            String simpleString = token.substring(1);
            return this.buildParsedTreeItem("SIMPLE_STRING", simpleString, String.valueOf(simpleString.length()));
        } else {
            logger.warn("An empty simple string is provided, so defaulting it to empty string");
            return this.buildParsedTreeItem("SIMPLE_STRING", "", "0");
        }
    }

    private JSONObject parseErrorString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            String errorString = token.substring(1);
            return this.buildParsedTreeItem("ERROR_STRING", errorString, String.valueOf(errorString.length()));
        } else {
            logger.warn("An empty error string is provided, so defaulting it to empty string");
            return this.buildParsedTreeItem("ERROR_STRING", "", "0");
        }
    }

    private JSONObject parseInteger() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int number = Integer.parseInt(token.substring(1));
            return this.buildParsedTreeItem("INTEGER", number, "NA");
        } else {
            logger.warn("An empty integer is provided, so defaulting it to 0");
            return this.buildParsedTreeItem("INTEGER", "0", "NA");
        }
    }

    private JSONObject parseDouble() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            Double number = Double.parseDouble(token.substring(1));
            return this.buildParsedTreeItem("DOUBLE", number, "NA");
        } else {
            logger.warn("An empty double is provided, so defaulting it to 0.0");
            return this.buildParsedTreeItem("DOUBLE", "0.0", "0");
        }
    }

    private JSONObject parseBigNumber() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            BigInteger number = new BigInteger(token.substring(1));
            return this.buildParsedTreeItem("BIG_NUMBER", number, "NA");
        } else {
            logger.warn("An empty big number is provided, so defaulting it to 0");
            return this.buildParsedTreeItem("BIG_NUMBER", "0", "0");
        }
    }

    private JSONObject parseBulkString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int bulkStringLength = Integer.parseInt(token.substring(1));
            if (bulkStringLength != -1) {
                this.currentParseIndex++;
                String bulkString = this.dataStore.get(currentParseIndex);
                if (bulkStringLength != bulkString.length()) {
                    throw new IllegalStateException(String.format("The bulk string length defined i.e $%d and actual provided string length does not match i.e %s : %d", bulkStringLength, bulkString, bulkString.length()));
                } else {
                    return this.buildParsedTreeItem("BULK_STRING", bulkString, String.valueOf(bulkStringLength));
                }
            } else {
                logger.warn("An empty bulk string is provided, so defaulting it to null");
                return this.buildParsedTreeItem("NULL", "_", "0");
            }
        } else {
            throw new IllegalStateException(String.format("The bulk string length is not defined i.e %s", token));
        }
    }

    private JSONObject parseBlobError() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int blobStringLength = Integer.parseInt(token.substring(1));
            if (blobStringLength != -1) {
                this.currentParseIndex++;
                String blobString = this.dataStore.get(currentParseIndex);
                if (blobStringLength != blobString.length()) {
                    throw new IllegalStateException(String.format("The blob string length defined i.e $%d and actual provided string length does not match i.e %s : %d", blobStringLength, blobString, blobString.length()));
                } else {
                    return this.buildParsedTreeItem("BLOB_ERROR", blobString, String.valueOf(blobStringLength));
                }
            } else {
                logger.warn("An empty blob string is provided, so defaulting it to null");
                return this.buildParsedTreeItem("NULL", "_", "0");
            }
        } else {
            throw new IllegalStateException(String.format("The blob string length is not defined i.e %s", token));
        }
    }

    private JSONObject parseArray() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int number = Integer.parseInt(token.substring(1));
            if (number > 0) {
                this.currentParseIndex++;
                List<JSONObject> arrayItems = new ArrayList<>();
                int idx = 0;
                while (idx < number) {
                    if (this.currentParseIndex == this.dataStore.size()) {
                        throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                    }
                    JSONObject parsedData = this.parseData();
                    if (parsedData == null) {
                        throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                    } else {
                        System.out.println(this.currentParseIndex + " : " + this.dataStore.size());
                        arrayItems.add(parsedData);
                        this.currentParseIndex++;
                        idx++;
                    }
                }
                return this.buildParsedTreeItem("ARRAY", arrayItems, String.valueOf(arrayItems.size()));

            } else if (number < 0) {
                return this.buildParsedTreeItem("NULL", "_", "0");
            } else {
                return this.buildParsedTreeItem("ARRAY", new ArrayList<JSONObject>(), String.valueOf(number));
            }
        } else {
            return this.buildParsedTreeItem("ARRAY", new ArrayList<JSONObject>(), "0");
        }
    }

    private JSONObject parseSet() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int number = Integer.parseInt(token.substring(1));
            if (number > 0) {
                this.currentParseIndex++;
                Set<JSONObject> setItems = new HashSet<>();
                int idx = 0;
                while (idx < number) {
                    if (this.currentParseIndex == this.dataStore.size()) {
                        throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                    }
                    JSONObject parsedData = this.parseData();
                    if (parsedData == null) {
                        throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                    } else {
                        System.out.println(this.currentParseIndex + " : " + this.dataStore.size());
                        setItems.add(parsedData);
                        this.currentParseIndex++;
                        idx++;
                    }
                }
                return this.buildParsedTreeItem("SET", setItems, String.valueOf(setItems.size()));

            } else if (number < 0) {
                return this.buildParsedTreeItem("NULL", "_", "0");
            } else {
                return this.buildParsedTreeItem("SET", new ArrayList<JSONObject>(), String.valueOf(number));
            }
        } else {
            return this.buildParsedTreeItem("SET", new ArrayList<JSONObject>(), "0");
        }
    }

}
