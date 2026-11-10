
package protocol;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RespParserTest {

    public static void main(String[] args) throws Exception {

        String resp =
                "*3\r\n" +
                        "$3\r\n" +
                        "SET\r\n" +
                        "$4\r\n" +
                        "name\r\n"+
                        "$7\r\n"+
                        "Habeeba\r\n";

        ByteArrayInputStream input =
                new ByteArrayInputStream(
                        resp.getBytes(StandardCharsets.UTF_8)
                );

        List<String> command = RespParser.readCommand(input);

        System.out.println("Parsed command: " + command);
    }
}

