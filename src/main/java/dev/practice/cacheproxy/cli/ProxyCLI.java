package dev.practice.cacheproxy.cli;

import com.sun.net.httpserver.HttpServer;
import dev.practice.cacheproxy.cache.Cache;
import dev.practice.cacheproxy.proxy.Handler;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

@Command(command = "caching-proxy")
public class ProxyCLI {

    @Command(command = "start")
    public void start(
            @Option(longNames = "port", shortNames = 'p') String port,
            @Option(longNames = "origin", shortNames = 'o') String origin
    ) throws Exception {
        if (port == null || port.isBlank() || origin == null || origin.isBlank()) {
            System.out.println("Both --port and --origin are required to start proxy server.");
            return;
        }

        try {
            int portNumber = Integer.parseInt(port);
            startProxyServer(portNumber, origin);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Please enter a valid integer.");
        }
    }

    @Command(command = "clear-cache")
    public void clearCache() {
        Cache.clear();
        System.out.println("Cache cleared successfully!");
    }

    private void startProxyServer(int port, String originUrl) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new Handler(originUrl));
        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Proxy server started at http://localhost:" + port);
        server.start();
    }
}
