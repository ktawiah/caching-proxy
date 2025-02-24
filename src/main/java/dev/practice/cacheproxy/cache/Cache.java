package dev.practice.cacheproxy.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();

    public static CachedResponse get(String url) {
        return cache.get(url);
    }

    public static synchronized void put(String url, CachedResponse response) {
        cache.put(url, response);
    }

    public static synchronized void clear() {
        cache.clear();
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
