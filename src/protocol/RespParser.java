
package protocol;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RespParser {

    public static List<String> readCommand(InputStream input)
            throws IOException {


        int marker = input.read();

        if (marker == -1) {
            return null;
        }


        if (marker != '*') {
            throw new IOException("Expected RESP array");
        }


        int count = Integer.parseInt(readLine(input));

        if (count < 1 || count > 1024) {
            throw new IOException("Invalid array size");
        }

        List<String> command = new ArrayList<>();

        for (int i = 0; i < count; i++) {


            if (input.read() != '$') {
                throw new IOException("Expected bulk string");
            }

            int length = Integer.parseInt(readLine(input));

            if (length < 0 || length > 1024 * 1024) {
                throw new IOException("Invalid bulk string length");
            }


            byte[] data = input.readNBytes(length);

            if (data.length != length) {
                throw new EOFException("Incomplete bulk string");
            }


            if (input.read() != '\r' || input.read() != '\n') {
                throw new IOException("Invalid bulk string ending");
            }

            String value = new String(data, StandardCharsets.UTF_8);

            command.add(value);
        }

        return command;
    }


    private static String readLine(InputStream input)
            throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while (true) {

            int current = input.read();

            if (current == -1) {
                throw new EOFException("Incomplete RESP line");
            }

            if (current == '\r') {

                if (input.read() != '\n') {
                    throw new IOException("Expected LF after CR");
                }

                break;
            }

            buffer.write(current);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}

