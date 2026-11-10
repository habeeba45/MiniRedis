
package database;
import persistence.DatabaseSnapshot;
import java.util.*;

public class InMemoryDatabase {


    private final Map<String, String> storage = new HashMap<>();


    private final Map<String, LinkedList<String>> lists =
            new HashMap<>();


    private final Map<String, LinkedHashSet<String>> sets =
            new HashMap<>();

    private final Map<String, Long> expiry = new HashMap<>();


    private void checkWrongType(String key, String allowedType) {
        removeIfExpired(key);
        if (!allowedType.equals("STRING")
                && storage.containsKey(key)) {
            throw new IllegalStateException("WRONGTYPE");
        }

        if (!allowedType.equals("LIST")
                && lists.containsKey(key)) {
            throw new IllegalStateException("WRONGTYPE");
        }

        if (!allowedType.equals("SET")
                && sets.containsKey(key)) {
            throw new IllegalStateException("WRONGTYPE");
        }
    }

    // ---------------- STRING OPERATIONS ----------------

    public synchronized void set(String key, String value) {
        removeIfExpired(key);
        checkWrongType(key, "STRING");
        storage.put(key, value);


        expiry.remove(key);
    }

    public synchronized String get(String key) {
        removeIfExpired(key);
        checkWrongType(key, "STRING");
        return storage.get(key);
    }

    private void removeIfExpired(String key) {

        Long expirationTime = expiry.get(key);

        if (expirationTime != null
                && System.currentTimeMillis() >= expirationTime) {

            storage.remove(key);
            lists.remove(key);
            sets.remove(key);
            expiry.remove(key);
        }
    }

    // ---------------- GENERAL OPERATIONS ----------------

    public synchronized boolean containsKey(String key) {
        removeIfExpired(key);

        return storage.containsKey(key)
                || lists.containsKey(key)
                || sets.containsKey(key);
    }

    public synchronized boolean delete(String key) {
        removeIfExpired(key);

        boolean removedString = storage.remove(key) != null;
        boolean removedList = lists.remove(key) != null;
        boolean removedSet = sets.remove(key) != null;

        expiry.remove(key);

        return removedString || removedList || removedSet;
    }

    // ---------------- LIST OPERATIONS ----------------

    public synchronized int lpush(String key, String value) {
        removeIfExpired(key);
        checkWrongType(key, "LIST");

        LinkedList<String> list =
                lists.computeIfAbsent(key, k -> new LinkedList<>());

        list.addFirst(value);
        return list.size();
    }

    public synchronized int rpush(String key, String value) {
        removeIfExpired(key);
        checkWrongType(key, "LIST");

        LinkedList<String> list =
                lists.computeIfAbsent(key, k -> new LinkedList<>());

        list.addLast(value);
        return list.size();
    }

    public synchronized String lpop(String key) {
        removeIfExpired(key);
        checkWrongType(key, "LIST");

        LinkedList<String> list = lists.get(key);

        if (list == null || list.isEmpty()) {
            return null;
        }

        String value = list.removeFirst();

        if (list.isEmpty()) {
            lists.remove(key);
        }

        return value;
    }

    public synchronized String rpop(String key) {
        removeIfExpired(key);
        checkWrongType(key, "LIST");

        LinkedList<String> list = lists.get(key);

        if (list == null || list.isEmpty()) {
            return null;
        }

        String value = list.removeLast();

        if (list.isEmpty()) {
            lists.remove(key);
        }

        return value;
    }

    public synchronized int llen(String key) {
        removeIfExpired(key);
        checkWrongType(key, "LIST");

        LinkedList<String> list = lists.get(key);
        return list == null ? 0 : list.size();
    }

    public synchronized List<String> lrange(
            String key, int start, int stop) {
        removeIfExpired(key);

        checkWrongType(key, "LIST");

        LinkedList<String> list = lists.get(key);

        if (list == null) {
            return new ArrayList<>();
        }

        int size = list.size();

        if (start < 0) start = size + start;
        if (stop < 0) stop = size + stop;

        start = Math.max(0, start);
        stop = Math.min(size - 1, stop);

        if (start > stop || start >= size) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                list.subList(start, stop + 1)
        );
    }

    // ---------------- SET OPERATIONS ----------------

    public synchronized int sadd(String key, String value)
    {
        removeIfExpired(key);
        checkWrongType(key, "SET");

        LinkedHashSet<String> set =
                sets.computeIfAbsent(key, k -> new LinkedHashSet<>());

        // add() returns true only if the value was new
        return set.add(value) ? 1 : 0;
    }

    public synchronized int srem(String key, String value) {
        removeIfExpired(key);
        checkWrongType(key, "SET");

        LinkedHashSet<String> set = sets.get(key);

        if (set == null) {
            return 0;
        }

        boolean removed = set.remove(value);

        if (set.isEmpty()) {
            sets.remove(key);
        }

        return removed ? 1 : 0;
    }

    public synchronized int sismember(String key, String value) {
        removeIfExpired(key);
        checkWrongType(key, "SET");

        LinkedHashSet<String> set = sets.get(key);

        return set != null && set.contains(value) ? 1 : 0;
    }

    public synchronized List<String> smembers(String key) {
        removeIfExpired(key);
        checkWrongType(key, "SET");

        LinkedHashSet<String> set = sets.get(key);

        if (set == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(set);
    }

    public synchronized int scard(String key) {
        removeIfExpired(key);
        checkWrongType(key, "SET");

        LinkedHashSet<String> set = sets.get(key);

        return set == null ? 0 : set.size();

    }

    public synchronized DatabaseSnapshot createSnapshot() {


        return new DatabaseSnapshot(
                new HashMap<>(storage),
                copyLists(),
                copySets(),
                new HashMap<>(expiry)
        );
    }

    public synchronized void restoreSnapshot(
            DatabaseSnapshot snapshot) {

        storage.clear();
        lists.clear();
        sets.clear();
        expiry.clear();

        storage.putAll(snapshot.storage);
        lists.putAll(snapshot.lists);
        sets.putAll(snapshot.sets);


        if (snapshot.expiry != null) {
            expiry.putAll(snapshot.expiry);
        }
    }

    private Map<String, LinkedList<String>> copyLists() {

        Map<String, LinkedList<String>> copy = new HashMap<>();

        for (Map.Entry<String, LinkedList<String>> entry
                : lists.entrySet()) {

            copy.put(
                    entry.getKey(),
                    new LinkedList<>(entry.getValue())
            );
        }

        return copy;
    }

    private Map<String, LinkedHashSet<String>> copySets() {

        Map<String, LinkedHashSet<String>> copy = new HashMap<>();

        for (Map.Entry<String, LinkedHashSet<String>> entry
                : sets.entrySet()) {

            copy.put(
                    entry.getKey(),
                    new LinkedHashSet<>(entry.getValue())
            );
        }

        return copy;
    }


    public synchronized int expire(String key, int seconds) {

        removeIfExpired(key);


        if (!containsKey(key)) {
            return 0;
        }

        if (seconds <= 0) {
            delete(key);
            return 1;
        }

        long expirationTime =
                System.currentTimeMillis() + seconds * 1000L;

        expiry.put(key, expirationTime);

        System.out.println("DEBUG expiry saved: " + expiry.get(key));

        return 1;
    }


    public synchronized long ttl(String key) {

        removeIfExpired(key);



        if (!containsKey(key)) {
            return -2;
        }

        Long expirationTime = expiry.get(key);

        if (expirationTime == null) {
            return -1;
        }

        return Math.max(
                0,
                (expirationTime - System.currentTimeMillis()) / 1000
        );
    }

    public synchronized int persist(String key) {

        removeIfExpired(key);

        if (!containsKey(key)) {
            return 0;
        }

        return expiry.remove(key) != null ? 1 : 0;
    }


    public synchronized int setex(
            String key, int seconds, String value) {

        if (seconds <= 0) {
            throw new IllegalArgumentException(
                    "ERR invalid expire time"
            );
        }

        set(key, value);

        return expire(key, seconds);
    }


}


