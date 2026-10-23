package com.bookverse.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Service Locator pattern: single place that looks up and caches business services. */
public final class ServiceLocator {
    public static final String LIBRARY_SERVICE = "libraryService";
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    private ServiceLocator() { }

    public static <T> T lookup(String name, Class<T> type) {
        return type.cast(CACHE.computeIfAbsent(name, ServiceLocator::create));
    }

    private static Object create(String name) {
        if (LIBRARY_SERVICE.equals(name)) return LibraryService.getInstance();
        throw new IllegalArgumentException("Unknown service: " + name);
    }
}
