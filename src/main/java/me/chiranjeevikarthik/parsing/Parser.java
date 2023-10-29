package parsing;

import constants.ParserConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        List<String> tests = new ArrayList<String>(Arrays.asList("*2\r\n$5\r\nHello\r\n$5\r\nworld\r\n","*3\r\n$5\r\nPINGO\r\n$5\r\nPongo\r\n-Hello",
        "$-1\r\n", "$-1\r\n\r\n", "+PONG\r\n\r\n", "+PONG\r\n", "+\r\n", "$2\r\nHe", "$2\r\nHello", "$\r\nHello", "$5\r\nHello", "$5\r\nworld\r\nWORLD\r\n", "+PING\r\n+PING\r\n", ":4848\r\n"));
        for(String test : tests)
        {
            Parser parser = new Parser(test);
            System.out.println("TEST : "+test);
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
        return null;
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

        return null;
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
            } else if (token.startsWith(ParserConstants.ARRAY)) {
                return this.parseArray();
            }
        } catch (Exception exception) {
            logger.error("Exception occurred : ", exception);
        }
        return null;
    }

    public JSONArray parse() {

        while (this.currentParseIndex < this.dataStore.size()) {
            JSONObject data = this.parseData();
            if(data!=null)
            {
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

    private JSONObject parseSimpleString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (!token.isEmpty()) {
            String simpleString = token.substring(1);
            return this.buildParsedTreeItem("SIMPLE_STRING", simpleString, String.valueOf(simpleString.length()));
        } else {
            return this.buildParsedTreeItem("SIMPLE_STRING", "", "0");
        }
    }

    private JSONObject parseErrorString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (!token.isEmpty()) {
            String errorString = token.substring(1);
            return this.buildParsedTreeItem("ERROR_STRING", errorString, String.valueOf(errorString.length()));
        } else {
            return this.buildParsedTreeItem("ERROR_STRING", "", "0");
        }
    }

    private JSONObject parseInteger() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (!token.isEmpty()) {
            String number = token.substring(1);
            return this.buildParsedTreeItem("INTEGER", number, number);
        } else {
            return this.buildParsedTreeItem("INTEGER", "0", "0");
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
                return this.buildParsedTreeItem("BULK_STRING", "", String.valueOf(bulkStringLength));
            }
        } else {
            throw new IllegalStateException(String.format("The bulk string length is not defined i.e %s", token));
        }
    }

    private JSONObject parseArray() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (!token.isEmpty()) {
            int number = Integer.parseInt(token.substring(1));
            if (number > 0) {
                this.currentParseIndex++;
                List<JSONObject> arrayItems = new ArrayList<>();
                int idx = 1;
                while (idx <= number) {
                    arrayItems.add(this.parseData());
                    this.currentParseIndex++;
                    idx++;
                }
                if (idx <= number) {
                    throw new IllegalStateException(String.format("The number of items declared v/s present does not match i.e %s", token));
                } else {
                    return this.buildParsedTreeItem("ARRAY", arrayItems, String.valueOf(arrayItems.size()));
                }

            }
            return this.buildParsedTreeItem("ARRAY", new ArrayList<JSONObject>(), String.valueOf(number));
        } else {
            return this.buildParsedTreeItem("ARRAY", new ArrayList<JSONObject>(), "0");
        }
    }

}
