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
import java.util.List;


public class Parser {

    private static Logger logger = LoggerFactory.getLogger(Parser.class);

    private List<String> dataStore;

    private JSONArray parsedTree = new JSONArray();
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
        String test = "*2\r\n$5\r\nHello\r\n$5\r\nworld\r\n";
        String test1 = "$-1\r\n";
        String test2 = "$-1\r\n\r\n";
        String test3 = "+PONG\r\n\r\n";
        String test4 = "+PONG\r\n";
        String test5 = "+\r\n";
        String test6 = "$2\r\nHe";
        String test7 = "$2\r\nHello";
        String test8 = "$\r\nHello";
        String test9 = "$5\r\nHello";
        String test10 = "$5\r\nworld\r\nWORLD\r\n";
        String test11 = "+PING\r\n+PING\r\n";
        Parser parser = new Parser(test11);
        System.out.println(parser.parse());

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

    public JSONArray parse() {

        while (this.currentParseIndex < this.dataStore.size()) {
            try {
                String token = this.dataStore.get(this.currentParseIndex);

                if (token.startsWith(ParserConstants.SIMPLE_STRING)) {
                    parseSimpleString();
                } else if (token.startsWith(ParserConstants.BULK_STRING)) {
                    parseBulkString();
                }
                else
                {
                    break;
                }
            } catch (Exception exception) {
                this.currentParseIndex = this.dataStore.size();
                logger.error("Exception occurred : ", exception);
            }

        }

        return this.parsedTree;
    }

    private JSONObject buildParsedTreeItem(String type, Object value, String length) {
        return new JSONObject()
                .put("type", type)
                .put("value", value)
                .put("length", length);
    }

    private void parseSimpleString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (!token.isEmpty()) {
            String simpleString = token.substring(1);
            this.parsedTree.put(this.buildParsedTreeItem("SIMPLE_STRING", simpleString, String.valueOf(simpleString.length())));
        } else {
            this.parsedTree.put(this.buildParsedTreeItem("SIMPLE_STRING", "", "0"));
        }
        this.currentParseIndex++;
    }

    private void parseBulkString() {
        String token = this.dataStore.get(this.currentParseIndex);
        if (token.length() >= 2) {
            int bulkStringLength = Integer.parseInt(token.substring(1));
            if (bulkStringLength != -1) {
                this.currentParseIndex++;
                String bulkString = this.dataStore.get(currentParseIndex);
                if (bulkStringLength != bulkString.length()) {
                    throw new IllegalStateException(String.format("The bulk string length defined i.e $%d and actual provided string length does not match i.e %s : %d", bulkStringLength, bulkString, bulkString.length()));
                } else {
                    this.parsedTree.put(this.buildParsedTreeItem("BULK_STRING", bulkString, String.valueOf(bulkStringLength)));
                }
            } else {
                this.parsedTree.put(this.buildParsedTreeItem("BULK_STRING", "", String.valueOf(bulkStringLength)));
            }
        } else {
            throw new IllegalStateException(String.format("The bulk string length is not defined i.e %s", token));
        }

        this.currentParseIndex++;

    }

}
