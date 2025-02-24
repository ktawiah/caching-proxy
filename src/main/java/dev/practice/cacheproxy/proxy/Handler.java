package dev.practice.cacheproxy.proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.practice.cacheproxy.cache.Cache;
import dev.practice.cacheproxy.cache.CachedResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler implements HttpHandler {
    private final String originServer;

    public Handler(String originServer) {
        this.originServer = originServer;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestURL = httpExchange.getRequestURI().getRawQuery() == null ?
                httpExchange.getRequestURI().getPath() :
                httpExchange.getRequestURI().getPath() + "?" + httpExchange.getRequestURI().getRawQuery();
        CachedResponse cachedResponse = Cache.get(requestURL);

        if (httpExchange.getRequestMethod().equalsIgnoreCase("GET") && cachedResponse != null) {
            System.out.println("Cache HIT: " + requestURL);
            sendResponse(httpExchange, 200, cachedResponse.body, cachedResponse.headers, "HIT");
        } else {
            System.out.println("Cache MISS: " + requestURL);
            fetchAndCacheResponse(httpExchange, requestURL);
        }
    }

    private void fetchAndCacheResponse(HttpExchange httpExchange, String requestURL) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL originUrl = new URL(originServer + requestURL);
            connection = (HttpURLConnection) originUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            InputStream inputStream = (responseCode < 400) ? connection.getInputStream() : connection.getErrorStream();
            byte[] responseBody = (inputStream != null) ? inputStream.readAllBytes() : new byte[0];

            // Get headers (filter out restricted ones)
            Map<String, List<String>> headers = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                if (entry.getKey() != null && !entry.getKey().equalsIgnoreCase("Transfer-Encoding")) {
                    headers.put(entry.getKey(), entry.getValue());
                }
            }

            if (responseCode == 200) {
                Cache.put(requestURL, new CachedResponse(responseBody, headers));
            }

            sendResponse(httpExchange, responseCode, responseBody, headers, "MISS");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(httpExchange, 500, "Internal Server Error".getBytes(), Collections.emptyMap(), "ERROR");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] body, Map<String, List<String>> headers, String cacheStatus) throws IOException {
        exchange.getResponseHeaders().add("X-Cache", cacheStatus);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                exchange.getResponseHeaders().add(entry.getKey(), value);
            }
        }
        exchange.sendResponseHeaders(statusCode, body.length);
        exchange.getResponseBody().write(body);
        exchange.getResponseBody().close();
    }
}
