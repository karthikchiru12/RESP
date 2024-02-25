package me.chiranjeevikarthik.encoding;

import me.chiranjeevikarthik.constants.EncodingConstants;
import me.chiranjeevikarthik.constants.ParserConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Encoder {

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);
    private final StringBuilder encodedString = new StringBuilder();
    private JSONArray parsedArray = new JSONArray();

    public Encoder(String parsedArray) {
        try {
            this.parsedArray = new JSONArray(parsedArray);
        } catch (Exception exception) {
            logger.error("An exception occurred while parsing parsed array string :", exception);
        }
    }

    public Encoder(JSONArray jsonArray) {
        this.parsedArray = jsonArray;
    }

    public static void main(String[] args) {
        List<String> tests = new ArrayList<>(Arrays.asList("[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]}]",
                "[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"PINGO\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Pongo\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"Hello\"}]}]",
                "[{\"length\":\"0\",\"type\":\"NULL\",\"value\":\"BULK_STRING\"}]",
                "[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PONG\"}]",
                "[{\"length\":\"0\",\"type\":\"SIMPLE_STRING\",\"value\":\"\"}]",
                "[{\"length\":\"2\",\"type\":\"BULK_STRING\",\"value\":\"He\"}]",
                "[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]",
                "[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"}]",
                "[{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"},{\"length\":\"4\",\"type\":\"SIMPLE_STRING\",\"value\":\"PING\"}]",
                "[{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":4848}]",
                "[{\"length\":\"NA\",\"type\":\"DOUBLE\",\"value\":4.0121}]",
                "[{\"length\":\"21\",\"type\":\"BLOB_ERROR\",\"value\":\"SYNTAX invalid syntax\"}]",
                "[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]",
                "[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":false}]",
                "[{\"length\":\"3\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"},{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true}]}]",
                "[{\"length\":\"NA\",\"type\":\"BIG_NUMBER\",\"value\":12345678901234556777888224433434}]",
                "[{\"length\":\"0\",\"type\":\"BIG_NUMBER\",\"value\":\"0\"}]",
                "[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]",
                "[{\"length\":\"3\",\"type\":\"SET\",\"value\":[{\"length\":\"1\",\"type\":\"BOOLEAN_TYPE\",\"value\":true},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"world\"},{\"length\":\"5\",\"type\":\"BULK_STRING\",\"value\":\"Hello\"}]}]",
                "[{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]}]}]",
                "[{\"length\":\"5\",\"type\":\"VERBATIM_STRING\",\"value\":\"Hello\"}]",
                "[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":2},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]",
                "[{\"length\":\"2\",\"type\":\"MAP\",\"value\":{\"{\\\"length\\\":\\\"6\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"second\\\"}\":{\"length\":\"2\",\"type\":\"ARRAY\",\"value\":[{\"length\":\"5\",\"type\":\"SIMPLE_STRING\",\"value\":\"Hello\"},{\"length\":\"5\",\"type\":\"ERROR_STRING\",\"value\":\"World\"}]},\"{\\\"length\\\":\\\"5\\\",\\\"type\\\":\\\"SIMPLE_STRING\\\",\\\"value\\\":\\\"first\\\"}\":{\"length\":\"NA\",\"type\":\"INTEGER\",\"value\":1}}}]"));
        for (String test : tests) {
            Encoder encoder = new Encoder(new JSONArray(test));
            System.out.println("TEST : " + test);
            System.out.println("RESULT : " + encoder.encode().replaceAll("\r\n", "\\\\r\\\\n"));
            System.out.println("#########$$$$$$$$$$$$$$$$$\n");

        }
    }

    public String encode() {
        for (int idx = 0; idx < this.parsedArray.length(); idx++) {
            JSONObject parsedItem = this.parsedArray.getJSONObject(idx);
            this.encodedString.append(this.encodeParsedItem(parsedItem));
        }
        return encodedString.toString();
    }

    public String encodeParsedItem(JSONObject parsedItem) {
        try {
            String type = parsedItem.optString(ParserConstants.TYPE);

            if (type.startsWith(EncodingConstants.SIMPLE_STRING_TYPE)) {
                return this.encodeSimpleString(parsedItem);
            } else if (type.startsWith(EncodingConstants.BULK_STRING_TYPE)) {
                return this.encodeBulkString(parsedItem);
            } else if (type.startsWith(EncodingConstants.INTEGER_TYPE)) {
                return this.encodeInteger(parsedItem);
            } else if (type.startsWith(EncodingConstants.ERROR_STRING_TYPE)) {
                return this.encodeErrorString(parsedItem);
            } else if (type.startsWith(EncodingConstants.DOUBLE_TYPE)) {
                return this.encodeDouble(parsedItem);
            } else if (type.startsWith(EncodingConstants.ARRAY_TYPE)) {
                return this.encodeArray(parsedItem);
            } else if (type.startsWith(EncodingConstants.BLOB_ERROR_TYPE)) {
                return this.encodeBlobError(parsedItem);
            } else if (type.startsWith(EncodingConstants.BOOLEAN_TYPE)) {
                return this.encodeBoolean(parsedItem);
            } else if (type.startsWith(EncodingConstants.BIG_NUMBER_TYPE)) {
                return this.encodeBigNumber(parsedItem);
            } else if (type.startsWith(EncodingConstants.SET_TYPE)) {
                return this.encodeSet(parsedItem);
            } else if (type.startsWith(EncodingConstants.VERBATIM_STRING_TYPE)) {
                return this.encodeVerbatimString(parsedItem);
            } else if (type.startsWith(EncodingConstants.MAP_TYPE)) {
                return this.encodeMap(parsedItem);
            } else if (type.startsWith(EncodingConstants.NULL_TYPE)) {
                return this.encodeNull(parsedItem);
            } else {
                throw new IllegalStateException(String.format("The parsedItem type %s is invalid", type));
            }

        } catch (Exception exception) {
            logger.error("Exception occurred : ", exception);
        }
        return null;
    }

    private String encodeSimpleString(JSONObject parsedItem) {
        return ParserConstants.SIMPLE_STRING +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeBulkString(JSONObject parsedItem) {
        return ParserConstants.BULK_STRING +
                parsedItem.optString(ParserConstants.LENGTH) +
                ParserConstants.CRLF +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeInteger(JSONObject parsedItem) {
        return ParserConstants.INTEGER +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeErrorString(JSONObject parsedItem) {
        return ParserConstants.ERROR_STRING +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeDouble(JSONObject parsedItem) {
        return ParserConstants.DOUBLE +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeBlobError(JSONObject parsedItem) {
        return ParserConstants.BLOB_ERROR +
                parsedItem.optString(ParserConstants.LENGTH) +
                ParserConstants.CRLF +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeBoolean(JSONObject parsedItem) {
        String value = Boolean.toString(parsedItem.optBoolean(ParserConstants.VALUE)).equalsIgnoreCase("true") ? "t" : "f";
        return ParserConstants.BOOLEAN +
                value +
                ParserConstants.CRLF;
    }

    private String encodeBigNumber(JSONObject parsedItem) {
        return ParserConstants.BIG_NUMBER +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeVerbatimString(JSONObject parsedItem) {
        return ParserConstants.VERBATIM_STRING +
                parsedItem.optString(ParserConstants.LENGTH) +
                ParserConstants.CRLF +
                parsedItem.optString(ParserConstants.VALUE) +
                ParserConstants.CRLF;
    }

    private String encodeArray(JSONObject parsedItem) {
        StringBuilder arrayString = new StringBuilder(ParserConstants.ARRAY + parsedItem.optString(ParserConstants.LENGTH) + ParserConstants.CRLF);
        JSONArray recordsJsonArray = parsedItem.optJSONArray(ParserConstants.VALUE);
        for (int idx = 0; idx < Integer.parseInt(parsedItem.optString(ParserConstants.LENGTH)); idx++) {
            JSONObject recordObject = (JSONObject) recordsJsonArray.get(idx);
            arrayString.append(this.encodeParsedItem(recordObject));
        }
        return arrayString.toString();
    }

    private String encodeSet(JSONObject parsedItem) {
        StringBuilder setString = new StringBuilder(ParserConstants.SET + parsedItem.optString(ParserConstants.LENGTH) + ParserConstants.CRLF);
        JSONArray recordsSetJsonArray = (JSONArray) parsedItem.get(ParserConstants.VALUE);
        for (int idx = 0; idx < Integer.parseInt(parsedItem.optString(ParserConstants.LENGTH)); idx++) {
            JSONObject recordObject = (JSONObject) recordsSetJsonArray.get(idx);
            setString.append(this.encodeParsedItem(recordObject));
        }
        return setString.toString();
    }

    private String encodeMap(JSONObject parsedItem) {
        StringBuilder mapString = new StringBuilder(ParserConstants.MAP + parsedItem.optString(ParserConstants.LENGTH) + ParserConstants.CRLF);
        JSONObject recordsMapObject = (JSONObject) parsedItem.get(ParserConstants.VALUE);
        Iterator<String> recordsMapObjectIterator = recordsMapObject.keySet().iterator();
        while (recordsMapObjectIterator.hasNext()) {
            String recordObjectKey = recordsMapObjectIterator.next();
            mapString.append(this.encodeParsedItem(new JSONObject(recordObjectKey)));
            mapString.append(this.encodeParsedItem(recordsMapObject.getJSONObject(recordObjectKey)));
        }
        return mapString.toString();
    }

    private String encodeNull(JSONObject parsedItem) {
        String nullitemType = parsedItem.optString(ParserConstants.VALUE);
        if (nullitemType.equalsIgnoreCase(EncodingConstants.BULK_STRING_TYPE)) {
            return ParserConstants.BULK_STRING + EncodingConstants.NULL_VALUE;
        } else if (nullitemType.equalsIgnoreCase(EncodingConstants.ARRAY_TYPE)) {
            return ParserConstants.ARRAY + EncodingConstants.NULL_VALUE;
        } else if (nullitemType.equalsIgnoreCase(EncodingConstants.SET_TYPE)) {
            return ParserConstants.SET + EncodingConstants.NULL_VALUE;
        } else if (nullitemType.equalsIgnoreCase(EncodingConstants.BLOB_ERROR_TYPE)) {
            return ParserConstants.BLOB_ERROR + EncodingConstants.NULL_VALUE;
        } else if (nullitemType.equalsIgnoreCase(EncodingConstants.VERBATIM_STRING_TYPE)) {
            return ParserConstants.VERBATIM_STRING + EncodingConstants.NULL_VALUE;
        } else if (nullitemType.equalsIgnoreCase(EncodingConstants.MAP_TYPE)) {
            return ParserConstants.MAP + EncodingConstants.NULL_VALUE;
        } else {
            throw new IllegalStateException("Null value does not belong to any valid types");
        }
    }
}
