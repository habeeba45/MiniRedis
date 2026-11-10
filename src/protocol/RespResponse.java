package protocol;

import java.util.List;

public record RespResponse(Type type, Object value) {

    public enum Type {
        SIMPLE_STRING,
        ERROR,
        INTEGER,
        BULK_STRING,
        ARRAY
    }

    public static RespResponse simpleString(String value) {
        return new RespResponse(Type.SIMPLE_STRING, value);
    }

    public static RespResponse error(String value) {
        return new RespResponse(Type.ERROR, value);
    }

    public static RespResponse integer(int value) {
        return new RespResponse(Type.INTEGER, value);
    }

    public static RespResponse bulkString(String value) {
        return new RespResponse(Type.BULK_STRING, value);
    }

    public static RespResponse nullBulkString() {
        return new RespResponse(Type.BULK_STRING, null);
    }

    public static RespResponse array(List<String> values) {
        return new RespResponse(Type.ARRAY, values);
    }
}