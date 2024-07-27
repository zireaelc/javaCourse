package ru.promo;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CustomHashMap <K, V> implements Map<K, V> {

    private final LinkedList<Entry<K, V>>[] table;
    private final ReentrantReadWriteLock[] locks;
    private final int capacity;

    private static class Entry<K, V> implements Map.Entry<K, V>{
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    public CustomHashMap(int capacity) {
        this.capacity = capacity;
        table = new LinkedList[capacity];
        locks = new ReentrantReadWriteLock[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    private int getIndex(Object key) {
        return key.hashCode() % capacity;
    }

    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < capacity; i++) {
            locks[i].readLock().lock();
            try {
                size += table[i].size();
            } finally {
                locks[i].readLock().unlock();
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        locks[index].readLock().lock();
        try {
            for (Entry<K, V> entry : table[index]) {
                if (entry.key.equals(key)) {
                    return true;
                }
            }
            return false;
        } finally {
            locks[index].readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < capacity; i++) {
            locks[i].readLock().lock();
            try {
                for (Entry<K, V> entry : table[i]) {
                    if (entry.value.equals(value)) {
                        return true;
                    }
                }
            } finally {
                locks[i].readLock().unlock();
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        int index = getIndex(key);
        locks[index].readLock().lock();
        try {
            for (Entry<K, V> entry : table[index]) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
            return null;
        } finally {
            locks[index].readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        int index = getIndex(key);
        locks[index].writeLock().lock();
        try {
            LinkedList<Entry<K, V>> list = table[index];
            for (Entry<K, V> entry : list) {
                if (entry.key.equals(key)) {
                    V oldValue = entry.value;
                    entry.value = value;
                    return oldValue;
                }
            }
            list.add(new Entry<>(key, value));
            return null;
        } finally {
            locks[index].writeLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {
        int index = getIndex(key);
        locks[index].writeLock().lock();
        try {
            LinkedList<Entry<K, V>> list = table[index];
            Iterator<Entry<K, V>> iterator = list.iterator();
            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();
                if (entry.key.equals(key)) {
                    V value = entry.value;
                    iterator.remove();
                    return value;
                }
            }
            return null;
        } finally {
            locks[index].writeLock().unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            locks[i].writeLock().lock();
            try {
                table[i].clear();
            } finally {
                locks[i].writeLock().unlock();
            }
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            locks[i].readLock().lock();
            try {
                for (Entry<K, V> entry : table[i]) {
                    keys.add(entry.key);
                }
            } finally {
                locks[i].readLock().unlock();
            }
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            locks[i].readLock().lock();
            try {
                for (Entry<K, V> entry : table[i]) {
                    values.add(entry.value);
                }
            } finally {
                locks[i].readLock().unlock();
            }
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            locks[i].readLock().lock();
            try {
                for (Entry<K, V> entry : table[i]) {
                    entries.add(entry);
                }
            } finally {
                locks[i].readLock().unlock();
            }
        }
        // return entries;      решить, что сделать с возвращаемым типом
        return null;
    }
}
