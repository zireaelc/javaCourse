package ru.promo;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomHashMapTest {

    private Map<String, Integer> map;

    @Before
    public void create() {
        map = new CustomHashMap<>(100);
    }

    @Test
    public void testPut() {
        var val = map.put("key1", 1);
        assertNull(val);
        assertEquals(1, map.size());

        val = map.put("key1", 2);
        assertEquals(Integer.valueOf(1), val);
        assertEquals(1, map.size());
    }

    @Test
    public void testGet(){
        map.put("key3", 3);
        var val = map.get("key3");
        assertEquals(Integer.valueOf(3), val);

        val = map.get("nonkey");
        assertNull(val);
    }

    @Test
    public void testSize() {
        assertEquals(0, map.size());
        map.put("key1", 1);
        map.put("key2", 2);
        assertEquals(2, map.size());
    }

    @Test
    public void testRemove() {
        map.put("key1", 1);
        map.put("key2", 2);

        assertEquals(Integer.valueOf(1), map.remove("key1"));
        assertNull(map.get("key1"));
        assertEquals(1, map.size());
    }

    @Test
    public void testContainsKey() {
        map.put("key1", 1);
        map.put("key2", 2);

        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key3"));
    }

    @Test
    public void testContainsValue() {
        map.put("key1", 1);
        map.put("key2", 2);

        assertTrue(map.containsValue(1));
        assertFalse(map.containsValue(3));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());

        map.put("key1", 1);

        assertFalse(map.isEmpty());
    }

    @Test
    public void testClear() {
        map.put("key1", 1);
        map.put("key2", 2);

        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    public void testConcurrent() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 100; i++) {
            map.put("Value" + i, i);
        }

        for (int i = 0; i < 100; i++) {
            String key = "Value" + i;
            executor.submit(() -> {
                map.remove(key);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }

        assertEquals(0, map.size());
    }
}
