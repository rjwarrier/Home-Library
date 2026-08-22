package com.mj.homelibrary.data

/**
 * Thread-safe fixed-capacity LRU cache. Evicts the least-recently-used entry once
 * [maxEntries] is exceeded, so long-running sessions (e.g. bulk scanning) can't grow
 * this unbounded the way a plain ConcurrentHashMap would.
 */
class BoundedLruCache<K, V>(private val maxEntries: Int) {
    private val map = object : LinkedHashMap<K, V>(maxEntries, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean = size > maxEntries
    }

    @Synchronized
    fun get(key: K): V? = map[key]

    @Synchronized
    fun put(key: K, value: V) {
        map[key] = value
    }

    @Synchronized
    fun remove(key: K) {
        map.remove(key)
    }
}
