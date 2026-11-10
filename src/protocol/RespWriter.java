package protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RespWriter {

    public static void writeResponse(
            OutputStream output,
            RespResponse response) throws IOException {

        switch (response.type()) {

            case SIMPLE_STRING:
                writeLine(output, "+", (String) response.value());
                break;

            case ERROR:
                writeLine(output, "-", (String) response.value());
                break;

            case INTEGER:
                writeLine(output, ":", response.value().toString());
                break;

            case BULK_STRING:
                writeBulkString(output, (String) response.value());
                break;

            case ARRAY:
                writeArray(output, (List<String>) response.value());
                break;
        }

        output.flush();
    }

    private static void writeLine(
            OutputStream output,
            String marker,
            String value) throws IOException {

        String response = marker + value + "\r\n";

        output.write(response.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeBulkString(
            OutputStream output,
            String value) throws IOException {

        if (value == null) {
            output.write("$-1\r\n".getBytes(StandardCharsets.UTF_8));
            return;
        }

        byte[] data = value.getBytes(StandardCharsets.UTF_8);

        String header = "$" + data.length + "\r\n";

        output.write(header.getBytes(StandardCharsets.UTF_8));
        output.write(data);
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private static void writeArray(
            OutputStream output,
            List<String> values) throws IOException {

        String header = "*" + values.size() + "\r\n";

        output.write(header.getBytes(StandardCharsets.UTF_8));

        for (String value : values) {
            writeBulkString(output, value);
        }
    }
}