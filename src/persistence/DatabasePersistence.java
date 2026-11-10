
package persistence;

import database.InMemoryDatabase;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DatabasePersistence {

    private static final String FILE_NAME = "dump.dat";


    public static void save(InMemoryDatabase database)
            throws IOException {

        DatabaseSnapshot snapshot = database.createSnapshot();

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE_NAME))) {

            output.writeObject(snapshot);
        }

        System.out.println("Database saved successfully!");
    }


    public static void load(InMemoryDatabase database)
            throws IOException, ClassNotFoundException {

        Path path = Path.of(FILE_NAME);

        if (!Files.exists(path)) {
            System.out.println("No previous database found.");
            return;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(FILE_NAME))) {

            DatabaseSnapshot snapshot =
                    (DatabaseSnapshot) input.readObject();

            database.restoreSnapshot(snapshot);
        }

        System.out.println("Database loaded successfully!");
    }
}

