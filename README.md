# MiniRedis — In-Memory Key-Value Database in Java

MiniRedis is a lightweight Redis-inspired database built from scratch using Java. It uses TCP socket communication and the RESP (Redis Serialization Protocol) to allow clients to interact with an in-memory data store.

The project demonstrates core backend engineering concepts including networking, multithreading, data structures, protocol parsing, persistence, and key expiration.

## Features

- **TCP Client-Server Architecture:** Communicates through TCP sockets on port 6379.
- **RESP Protocol:** Parses and generates RESP-style requests and responses.
- **Multithreading:** Handles multiple client connections concurrently.
- **String Storage:** Stores and retrieves key-value pairs.
- **List Operations:** Supports insertion, removal, indexing, and range retrieval.
- **Set Operations:** Supports adding, removing, and checking set members.
- **Data Persistence:** Saves database snapshots to a file and restores them after restart.
- **Key Expiration:** Supports TTL-based expiration and persistent keys.
- **Thread Safety:** Uses synchronized database operations to protect shared data.

## Technologies Used

- Java
- TCP Socket Programming
- RESP Protocol
- Java Collections Framework
- Multithreading
- Java Object Serialization

## Supported Commands

### General Commands
| Command | Description |
|---|---|
| PING | Checks whether the server is responding |
| ECHO message | Returns the supplied message |

### String Commands
| Command | Description |
|---|---|
| SET key value | Stores a string |
| GET key | Retrieves a string |
| DEL key | Deletes a key |

### List Commands
| Command | Description |
|---|---|
| LPUSH key value | Inserts an element at the beginning |
| RPUSH key value | Inserts an element at the end |
| LPOP key | Removes the first element |
| RPOP key | Removes the last element |
| LLEN key | Returns the list length |
| LRANGE key start stop | Retrieves a range of elements |

### Set Commands
| Command | Description |
|---|---|
| SADD key member | Adds a set member |
| SREM key member | Removes a set member |
| SISMEMBER key member | Checks set membership |
| SMEMBERS key | Returns all set members |
| SCARD key | Returns the number of members |

### Expiration Commands
| Command | Description |
|---|---|
| EXPIRE key seconds | Sets a key expiration time |
| TTL key | Returns remaining time to live |
| PERSIST key | Removes a key's expiration |
| SETEX key seconds value | Stores a string with an expiration |

## Project Structure

```text
minireddis/
├── src/
│   ├── Main.java
│   ├── client/
│   │   └── RedisClient.java
│   ├── command/
│   │   └── CommandHandler.java
│   ├── database/
│   │   └── InMemoryDatabase.java
│   ├── protocol/
│   │   ├── RespParser.java
│   │   ├── RespParserTest.java
│   │   ├── RespWriter.java
│   │   └── RespResponse.java
│   └── persistence/
│       ├── DatabaseSnapshot.java
│       └── DatabasePersistence.java
├── README.md
└── dump.dat (generated at runtime)
```

## How to Run

### Prerequisites
- Java Development Kit (JDK 17 or later)
- Windows PowerShell or a terminal
- IntelliJ IDEA (optional)

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
cd minireddis
```

### 2. Compile the project

On Windows PowerShell:

```powershell
javac -d out src\Main.java src\client\RedisClient.java src\command\CommandHandler.java src\database\InMemoryDatabase.java src\protocol\RespParser.java src\protocol\RespWriter.java src\protocol\RespResponse.java src\persistence\DatabaseSnapshot.java src\persistence\DatabasePersistence.java
```

### 3. Start the server

```powershell
java -cp out Main
```

### 4. Connect a client

Open a second terminal in the project directory:

```powershell
java -cp out client.RedisClient
```

You can now execute database commands through the interactive client.

## Example Usage

```text
MiniRedis> PING
PONG

MiniRedis> SET name Habeeba
OK

MiniRedis> GET name
Habeeba

MiniRedis> LPUSH tasks Java
1

MiniRedis> RPUSH tasks Python
2

MiniRedis> LLEN tasks
2

MiniRedis> SADD languages Java
1

MiniRedis> SMEMBERS languages
[Java]

MiniRedis> SETEX session 60 active
OK

MiniRedis> TTL session
approximately 60
```

## Persistence

MiniRedis saves database snapshots to `dump.dat` when the server shuts down. When the server starts again, it loads the previous snapshot, including stored values and expiration timestamps.

## Learning Outcomes

This project helped explore:

- Client-server communication using TCP sockets
- Protocol design and parsing
- Java collections and data structures
- Concurrent client handling
- Thread-safe database operations
- Serialization and persistence
- Time-based key expiration

## Project Scope

MiniRedis is an educational Redis-inspired implementation. It supports a selected subset of Redis-style commands and RESP functionality; it is not intended to be a complete replacement for Redis.

## Author

**Habeeba Khatoon**

Computer Science and Engineering Student
