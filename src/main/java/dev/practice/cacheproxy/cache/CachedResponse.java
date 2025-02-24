package dev.practice.cacheproxy.cache;

import java.util.List;
import java.util.Map;

public class CachedResponse {
    public byte[] body;
    public Map<String, List<String>> headers;

    public CachedResponse(byte[] body, Map<String, List<String>> headers) {
        this.body = body;
        this.headers = headers;
    }
}
