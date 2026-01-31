package dev.simplix.cirrus.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class LoadingCache<K, V> {

    private final Map<K, V> map = new HashMap<>();
    private final long duration;
    private final TimeUnit timeUnit;
    private final CacheLoader<K, V> cacheLoader;

    public synchronized V get(K k) {
        if (!this.map.containsKey(k)) {
            put(k, this.cacheLoader.load(k));
        }

        return this.map.get(k);
    }

    public synchronized void put(K k, V v) {
        this.map.put(k, v);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!LoadingCache.this.map.containsKey(k)) {
                    return;
                }

                LoadingCache.this.map.remove(k);
            }
        }, this.timeUnit.toMillis(this.duration));
    }

    public static abstract class CacheLoader<K, V> {

        public abstract V load(K k);
    }
}

