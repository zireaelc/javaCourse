package ru.promo;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> map = new CustomHashMap<>(10);

        map.put("First", 1);
        map.put("Second", 2);
        map.put("Third", 3);

        System.out.println("Value for key 'First': " + map.get("First"));
        System.out.println("Value for key 'Second': " + map.get("Second"));

        map.remove("First");

        System.out.println("Map contain the 'First' key? " + map.containsKey("First"));
        System.out.println("Map contain the 'Second' key? " + map.containsKey("Second"));

        System.out.println("Keys: " + map.keySet());
        System.out.println("Values: " + map.values());

        System.out.println("Map size: " + map.size());
    }
}