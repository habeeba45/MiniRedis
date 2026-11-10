package client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RedisClient {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        try (Socket socket = new Socket("localhost", 6379)) {

            System.out.println("Connected to MiniRedis!");

            OutputStream output = socket.getOutputStream();
            InputStream input = socket.getInputStream();

            while (true) {

                System.out.print("MiniRedis> ");

                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("EXIT")) {
                    break;
                }

                List<String> parts = parseInput(command);

                if (parts.isEmpty()) {
                    continue;
                }

                sendCommand(output, parts);

                String response = readResponse(input);

                System.out.println(response);
            }

            System.out.println("Disconnected from MiniRedis!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    private static List<String> parseInput(String command) {

        String trimmed = command.trim();

        List<String> parts = new ArrayList<>();

        if (trimmed.isEmpty()) {
            return parts;
        }


        if (trimmed.toUpperCase().startsWith("ECHO ")) {

            int index = trimmed.indexOf(' ');

            parts.add("ECHO");
            parts.add(trimmed.substring(index + 1).trim());

            return parts;
        }


        if (trimmed.toUpperCase().startsWith("SET ")) {

            String[] tokens = trimmed.split("\\s+", 3);

            for (String token : tokens) {
                parts.add(token);
            }

            return parts;
        }


        String[] tokens = trimmed.split("\\s+");

        for (String token : tokens) {
            parts.add(token);
        }

        return parts;
    }

    private static void sendCommand(
            OutputStream output,
            List<String> parts) throws IOException {

        String header = "*" + parts.size() + "\r\n";

        output.write(header.getBytes(StandardCharsets.UTF_8));

        for (String part : parts) {

            byte[] data = part.getBytes(StandardCharsets.UTF_8);

            String bulkHeader = "$" + data.length + "\r\n";

            output.write(bulkHeader.getBytes(StandardCharsets.UTF_8));
            output.write(data);
            output.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }

        output.flush();
    }

    private static String readResponse(InputStream input)
            throws IOException {

        int marker = input.read();

        if (marker == -1) {
            throw new EOFException("Server disconnected");
        }


        if (marker == '+') {
            return readLine(input);
        }


        if (marker == '-') {
            return "(error) " + readLine(input);
        }


        if (marker == ':') {
            return readLine(input);
        }


        if (marker == '$') {

            int length = Integer.parseInt(readLine(input));

            if (length == -1) {
                return "(nil)";
            }

            byte[] data = input.readNBytes(length);

            if (data.length != length) {
                throw new EOFException("Incomplete response");
            }

            if (input.read() != '\r' || input.read() != '\n') {
                throw new IOException("Invalid response ending");
            }

            return new String(data, StandardCharsets.UTF_8);
        }


        if (marker == '*') {

            int count = Integer.parseInt(readLine(input));

            List<String> values = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                values.add(readResponse(input));
            }

            return values.toString();
        }

        throw new IOException("Unknown RESP response type");
    }

    private static String readLine(InputStream input)
            throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        while (true) {

            int current = input.read();

            if (current == -1) {
                throw new EOFException("Incomplete response header");
            }

            if (current == '\r') {

                if (input.read() != '\n') {
                    throw new IOException("Invalid CRLF");
                }

                break;
            }

            buffer.write(current);
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}

