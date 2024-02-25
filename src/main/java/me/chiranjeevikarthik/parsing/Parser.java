package me.chiranjeevikarthik.parsing;

import me.chiranjeevikarthik.constants.EncodingConstants;
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
                "(12345678901234556777888224433434\r\n", "(\r\n", "*2\r\n+Hello", "*2\r\n+Hello\r\n-World", "~3\r\n$5\r\nHello\r\n$5\r\nworld\r\n#t\r\n",
                "*2\r\n+Hello\r\n*2\r\n+Hello\r\n-World", "=5\r\nHello", "=5\r\nHell", "%2\r\n+first\r\n:1\r\n+second\r\n:2\r\n",
                "%2\r\n+first\r\n:1\r\n+second", "%2\r\n+first\r\n:1\r\n+second\r\n*2\r\n+Hello\r\n-World",
                "%2\r\n+first\r\n:1\r\n*2\r\n+Hello\r\n-World\r\n+VALUE", "%2\r\n+first\r\n:1",
                "%2\r\n+first\r\n:1\r\n+second\r\n*2\r\n+Hello\r\n+World"));
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
            } else if (token.startsWith(ParserConstants.VERBATIM_STRING)) {
                return this.parseVerbatimString();
            } else if (token.startsWith(ParserConstants.MAP)) {
                return this.parseMap();
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
                .put(ParserConstants.TYPE, type)
                .put(ParserConstants.VALUE, value)
                .put(ParserConstants.LENGTH, length);
    }

    private JSONObject parseBoolean() {
        String token = this.dataStore.get(this.currentParseIndex);
        String booleanString = null;
        if (token.length() >= 2) {
            booleanString = token.substring(1);
            if (booleanString.equalsIgnoreCase("t") || booleanString.equalsIgnoreCase("f")) {
                boolean booleanValue = booleanString.equalsIgnoreCase("t");
                return this.buildParsedTreeItem(EncodingConstants.BOOLEAN_TYPE, booleanValue, "1");
            } else {
                throw new IllegalStateException(String.format("The boolean value is not valid i.e %s", booleanString));
            }
        } else {
            logger.warn("The boolean value is empty, so defaulting it to false");
            return this.buildParsedTreeItem(EncodingConstants.BOOLEAN_TYPE, false, "1");

        }
    }

    private JSONObject parseSimpleString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            String simpleString = token.substring(1);
            return this.buildParsedTreeItem(EncodingConstants.SIMPLE_STRING_TYPE, simpleString, String.valueOf(simpleString.length()));
        } else {
            logger.warn("An empty simple string is provided, so defaulting it to empty string");
            return this.buildParsedTreeItem(EncodingConstants.SIMPLE_STRING_TYPE, "", "0");
        }
    }

    private JSONObject parseErrorString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            String errorString = token.substring(1);
            return this.buildParsedTreeItem(EncodingConstants.ERROR_STRING_TYPE, errorString, String.valueOf(errorString.length()));
        } else {
            logger.warn("An empty error string is provided, so defaulting it to empty string");
            return this.buildParsedTreeItem(EncodingConstants.ERROR_STRING_TYPE, "", "0");
        }
    }

    private JSONObject parseInteger() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int number = Integer.parseInt(token.substring(1));
            return this.buildParsedTreeItem(EncodingConstants.INTEGER_TYPE, number, "NA");
        } else {
            logger.warn("An empty integer is provided, so defaulting it to 0");
            return this.buildParsedTreeItem(EncodingConstants.INTEGER_TYPE, "0", "NA");
        }
    }

    private JSONObject parseDouble() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            Double number = Double.parseDouble(token.substring(1));
            return this.buildParsedTreeItem(EncodingConstants.DOUBLE_TYPE, number, "NA");
        } else {
            logger.warn("An empty double is provided, so defaulting it to 0.0");
            return this.buildParsedTreeItem(EncodingConstants.DOUBLE_TYPE, "0.0", "0");
        }
    }

    private JSONObject parseBigNumber() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            BigInteger number = new BigInteger(token.substring(1));
            return this.buildParsedTreeItem(EncodingConstants.BIG_NUMBER_TYPE, number, "NA");
        } else {
            logger.warn("An empty big number is provided, so defaulting it to 0");
            return this.buildParsedTreeItem(EncodingConstants.BIG_NUMBER_TYPE, "0", "0");
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
                    return this.buildParsedTreeItem(EncodingConstants.BULK_STRING_TYPE, bulkString, String.valueOf(bulkStringLength));
                }
            } else {
                logger.warn("An empty bulk string is provided, so defaulting it to null");
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.BULK_STRING_TYPE, "0");
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
                    return this.buildParsedTreeItem(EncodingConstants.BLOB_ERROR_TYPE, blobString, String.valueOf(blobStringLength));
                }
            } else {
                logger.warn("An empty blob string is provided, so defaulting it to null");
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.BLOB_ERROR_TYPE, "0");
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
                        arrayItems.add(parsedData);
                        this.currentParseIndex++;
                        idx++;
                    }
                }
                //If in a map, the key value is array, then we land into index out of bounds exception
                //So putting the index penultimate level, just for that
                this.currentParseIndex--;
                return this.buildParsedTreeItem(EncodingConstants.ARRAY_TYPE, arrayItems, String.valueOf(arrayItems.size()));

            } else if (number < 0) {
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.ARRAY_TYPE, "0");
            } else {
                return this.buildParsedTreeItem(EncodingConstants.ARRAY_TYPE, new ArrayList<JSONObject>(), String.valueOf(number));
            }
        } else {
            return this.buildParsedTreeItem(EncodingConstants.ARRAY_TYPE, new ArrayList<JSONObject>(), "0");
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
                        setItems.add(parsedData);
                        this.currentParseIndex++;
                        idx++;
                    }
                }
                //If in a map, the key value is set, then we land into index out of bounds exception
                //So putting the index penultimate level, just for that
                this.currentParseIndex--;
                return this.buildParsedTreeItem(EncodingConstants.SET_TYPE, setItems, String.valueOf(setItems.size()));

            } else if (number < 0) {
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.SET_TYPE, "0");
            } else {
                return this.buildParsedTreeItem(EncodingConstants.SET_TYPE, new HashSet<JSONObject>(), String.valueOf(number));
            }
        } else {
            return this.buildParsedTreeItem(EncodingConstants.SET_TYPE, new HashSet<JSONObject>(), "0");
        }
    }

    private JSONObject parseVerbatimString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int verbatimStringLength = Integer.parseInt(token.substring(1));
            if (verbatimStringLength != -1) {
                this.currentParseIndex++;
                String verbatimString = this.dataStore.get(currentParseIndex);
                if (verbatimStringLength != verbatimString.length()) {
                    throw new IllegalStateException(String.format("The verbatim string length defined i.e $%d and actual provided string length does not match i.e %s : %d", verbatimStringLength, verbatimString, verbatimString.length()));
                } else {
                    return this.buildParsedTreeItem(EncodingConstants.VERBATIM_STRING_TYPE, verbatimString, String.valueOf(verbatimStringLength));
                }
            } else {
                logger.warn("An empty verbatim string is provided, so defaulting it to null");
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.VERBATIM_STRING_TYPE, "0");
            }
        } else {
            throw new IllegalStateException(String.format("The verbatim string length is not defined i.e %s", token));
        }
    }

    private JSONObject parseMap() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int number = Integer.parseInt(token.substring(1));
            if (number > 0) {
                this.currentParseIndex++;
                Map<JSONObject, JSONObject> map = new HashMap<>();
                int idx = 0;
                while (idx < number * 2) {
                    if (this.currentParseIndex == this.dataStore.size()) {
                        throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                    }
                    JSONObject parsedKey = this.parseData();
                    this.currentParseIndex++;
                    JSONObject parsedValue = this.parseData();
                    if (parsedKey == null || parsedValue == null) {
                        throw new IllegalStateException("The provided map seems to be invalid...");
                    } else {
                        map.put(parsedKey, parsedValue);
                        this.currentParseIndex++;
                        idx = idx + 2;
                    }
                }
                return this.buildParsedTreeItem(EncodingConstants.MAP_TYPE, map, String.valueOf(map.size()));

            } else if (number < 0) {
                return this.buildParsedTreeItem(EncodingConstants.NULL_TYPE, EncodingConstants.MAP_TYPE, "0");
            } else {
                return this.buildParsedTreeItem(EncodingConstants.MAP_TYPE, new JSONObject(), String.valueOf(number));
            }
        } else {
            return this.buildParsedTreeItem(EncodingConstants.MAP_TYPE, new JSONObject(""), "0");
        }
    }

}
