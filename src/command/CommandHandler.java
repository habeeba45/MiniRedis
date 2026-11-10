package command;

import database.InMemoryDatabase;
import protocol.RespResponse;

import java.util.List;
import java.util.Locale;

public class CommandHandler {

    public static RespResponse handle(
            List<String> parts,
            InMemoryDatabase database) {

        if (parts == null || parts.isEmpty()
                || parts.get(0).isBlank()) {

            return RespResponse.error("ERR empty command");
        }

        String command = parts.get(0).toUpperCase(Locale.ROOT);

        try {

            switch (command) {

                case "PING":

                    if (parts.size() == 1) {
                        return RespResponse.simpleString("PONG");
                    }

                    if (parts.size() == 2) {
                        return RespResponse.bulkString(parts.get(1));
                    }

                    return wrongArguments("PING");

                case "ECHO":

                    if (parts.size() != 2) {
                        return wrongArguments("ECHO");
                    }

                    return RespResponse.bulkString(parts.get(1));

                case "SET":

                    if (parts.size() != 3) {
                        return wrongArguments("SET");
                    }

                    database.set(parts.get(1), parts.get(2));

                    return RespResponse.simpleString("OK");

                case "GET":

                    if (parts.size() != 2) {
                        return wrongArguments("GET");
                    }

                    String value = database.get(parts.get(1));

                    return value == null
                            ? RespResponse.nullBulkString()
                            : RespResponse.bulkString(value);

                case "DEL":

                    if (parts.size() != 2) {
                        return wrongArguments("DEL");
                    }

                    boolean deleted = database.delete(parts.get(1));

                    return RespResponse.integer(deleted ? 1 : 0);

                // Redis List commands

                case "LPUSH":

                    if (parts.size() != 3) {
                        return wrongArguments("LPUSH");
                    }

                    return RespResponse.integer(
                            database.lpush(parts.get(1), parts.get(2))
                    );

                case "RPUSH":

                    if (parts.size() != 3) {
                        return wrongArguments("RPUSH");
                    }

                    return RespResponse.integer(
                            database.rpush(parts.get(1), parts.get(2))
                    );

                case "LPOP":

                    if (parts.size() != 2) {
                        return wrongArguments("LPOP");
                    }

                    return RespResponse.bulkString(
                            database.lpop(parts.get(1))
                    );

                case "RPOP":

                    if (parts.size() != 2) {
                        return wrongArguments("RPOP");
                    }

                    return RespResponse.bulkString(
                            database.rpop(parts.get(1))
                    );

                case "LLEN":

                    if (parts.size() != 2) {
                        return wrongArguments("LLEN");
                    }

                    return RespResponse.integer(
                            database.llen(parts.get(1))
                    );

                case "LRANGE":

                    if (parts.size() != 4) {
                        return wrongArguments("LRANGE");
                    }

                    int start = Integer.parseInt(parts.get(2));
                    int stop = Integer.parseInt(parts.get(3));

                    return RespResponse.array(
                            database.lrange(parts.get(1), start, stop)
                    );

                case "SADD":
                    if (parts.size() != 3) {
                        return wrongArguments("SADD");
                    }

                    return RespResponse.integer(
                            database.sadd(parts.get(1), parts.get(2))
                    );

                case "SREM":
                    if (parts.size() != 3) {
                        return wrongArguments("SREM");
                    }

                    return RespResponse.integer(
                            database.srem(parts.get(1), parts.get(2))
                    );

                case "SISMEMBER":
                    if (parts.size() != 3) {
                        return wrongArguments("SISMEMBER");
                    }

                    return RespResponse.integer(
                            database.sismember(parts.get(1), parts.get(2))
                    );

                case "SMEMBERS":
                    if (parts.size() != 2) {
                        return wrongArguments("SMEMBERS");
                    }

                    return RespResponse.array(
                            database.smembers(parts.get(1))
                    );

                case "SCARD":
                    if (parts.size() != 2) {
                        return wrongArguments("SCARD");
                    }

                    return RespResponse.integer(
                            database.scard(parts.get(1))
                    );
                case "EXPIRE": {
                    if (parts.size() != 3) {
                        return wrongArguments("EXPIRE");
                    }

                    int seconds = Integer.parseInt(parts.get(2));

                    return RespResponse.integer(
                            database.expire(parts.get(1), seconds)
                    );
                }

                case "TTL": {
                    if (parts.size() != 2) {
                        return wrongArguments("TTL");
                    }

                    return RespResponse.integer(
                            (int) database.ttl(parts.get(1))
                    );
                }

                case "PERSIST": {
                    if (parts.size() != 2) {
                        return wrongArguments("PERSIST");
                    }

                    return RespResponse.integer(
                            database.persist(parts.get(1))
                    );
                }

                case "SETEX": {
                    if (parts.size() != 4) {
                        return wrongArguments("SETEX");
                    }

                    int seconds = Integer.parseInt(parts.get(2));

                    database.setex(
                            parts.get(1),
                            seconds,
                            parts.get(3)
                    );

                    return RespResponse.simpleString("OK");
                }



                default:

                    return RespResponse.error(
                            "ERR unknown command '" + parts.get(0) + "'"
                    );
            }

        } catch (IllegalStateException e) {

            return RespResponse.error(
                    "WRONGTYPE Operation against a key holding the wrong kind of value"
            );

        } catch (NumberFormatException e) {

            return RespResponse.error("ERR value is not an integer");
        }
        catch (IllegalArgumentException e) {
            return RespResponse.error(e.getMessage());
        }
    }

    private static RespResponse wrongArguments(String command) {

        return RespResponse.error(
                "ERR wrong number of arguments for '" + command + "' command"
        );
    }
}

