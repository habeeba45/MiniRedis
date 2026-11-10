package persistence;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

public class DatabaseSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    public Map<String, String> storage;
    public Map<String, LinkedList<String>> lists;
    public Map<String, LinkedHashSet<String>> sets;
    public Map<String, Long> expiry;

    public DatabaseSnapshot(
            Map<String, String> storage,
            Map<String, LinkedList<String>> lists,
            Map<String, LinkedHashSet<String>> sets,
            Map<String, Long> expiry) {

        this.storage = storage;
        this.lists = lists;
        this.sets = sets;
        this.expiry = expiry;
    }
}
