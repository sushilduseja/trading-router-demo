package com.trading.benchmark;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Demonstrates simple object pooling as one of the zero-GC techniques.
 */
public class ObjectPoolDemo<T> {
    
    private final Deque<T> pool;
    private final Supplier<T> factory;
    private final int maxPoolSize;
    private final AtomicInteger allocations = new AtomicInteger();
    private final AtomicInteger reuses = new AtomicInteger();
    
    public ObjectPoolDemo(Supplier<T> factory, int initialSize, int maxPoolSize) {
        this.factory = factory;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ArrayDeque<>(initialSize);
        
        // Pre-allocate objects
        for (int i = 0; i < initialSize; i++) {
            pool.add(factory.get());
            allocations.incrementAndGet();
        }
    }
    
    public T borrow() {
        T obj = pool.poll();
        if (obj == null) {
            obj = factory.get();
            allocations.incrementAndGet();
        } else {
            reuses.incrementAndGet();
        }
        return obj;
    }
    
    public void release(T obj) {
        if (pool.size() < maxPoolSize) {
            pool.offer(obj);
        }
    }
    
    public void printStats() {
        System.out.println("Object Pool Stats:");
        System.out.println("  Allocations: " + allocations.get());
        System.out.println("  Reuses:      " + reuses.get());
        System.out.println("  Pool Size:   " + pool.size());
        System.out.println("  Reuse Ratio: " + 
                String.format("%.2f", (reuses.get() * 100.0) / (allocations.get() + reuses.get())) + "%");
    }
    
    /**
     * Simple demo of the object pool
     */
    public static void main(String[] args) {
        // Create a pool with 5 initial objects
        ObjectPoolDemo<SimpleOrder> orderPool = new ObjectPoolDemo<>(
                () -> new SimpleOrder("", "", 0.0, 0),
                5, 20);
        
        // Use and return 100 orders
        for (int i = 0; i < 100; i++) {
            SimpleOrder order = orderPool.borrow();
            order.reset("ORDER-" + i, "AAPL", 150.0 + i, 100 + i);
            
            // Use the order...
            System.out.println("Processing: " + order);
            
            // Return to pool
            orderPool.release(order);
        }
        
        // Print stats
        orderPool.printStats();
    }
    
    // Simple order class for demonstration
    private static class SimpleOrder {
        private String id;
        private String symbol;
        private double price;
        private int quantity;
        
        public SimpleOrder(String id, String symbol, double price, int quantity) {
            this.id = id;
            this.symbol = symbol;
            this.price = price;
            this.quantity = quantity;
        }
        
        public void reset(String id, String symbol, double price, int quantity) {
            this.id = id;
            this.symbol = symbol;
            this.price = price;
            this.quantity = quantity;
        }
        
        @Override
        public String toString() {
            return "Order{id='" + id + "', symbol='" + symbol + "', price=" + price + ", quantity=" + quantity + "}";
        }
    }
}
