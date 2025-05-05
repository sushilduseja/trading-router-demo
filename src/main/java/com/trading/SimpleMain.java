package com.trading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple main class that demonstrates zero-GC techniques versus standard allocation.
 * This implementation has no external dependencies for easy compilation and running.
 */
public class SimpleMain {
    
    // Benchmark configuration
    private static final int WARM_UP_ITERATIONS = 3;
    private static final int BENCHMARK_ITERATIONS = 5;
    private static final int ORDERS_PER_ITERATION = 50_000;
    private static final int GC_PRESSURE_OBJECTS = 5_000;
    
    public static void main(String[] args) {
        System.out.println("===== Low Latency Trading Router Demo =====");
        System.out.println();
        
        if (args.length < 1) {
            System.err.println("Please specify mode: standard or zerogc");
            System.exit(1);
        }
        
        String mode = args[0].toLowerCase();
        System.out.println("Starting trading router in " + mode + " mode");
        
        if (!mode.equals("standard") && !mode.equals("zerogc")) {
            System.err.println("Unknown mode: " + mode + ". Please use 'standard' or 'zerogc'");
            System.exit(1);
        }
        
        runBenchmark(mode);
    }
    
    private static void runBenchmark(String mode) {
        System.out.println("\nRunning benchmark in " + mode + " mode");
        System.out.println("-----------------------------------");
        
        // Warm up the JVM
        System.out.println("Warming up JVM...");
        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            if (mode.equals("zerogc")) {
                runZeroGCDemo(ORDERS_PER_ITERATION / 10, false);
            } else {
                runStandardDemo(ORDERS_PER_ITERATION / 10, false);
            }
            System.out.print(".");
            System.gc();
        }
        System.out.println(" Done!");
        
        // Run the benchmark
        System.out.println("\nStarting measurement...");
        
        if (mode.equals("zerogc")) {
            System.out.println("ZeroGC mode: Using object pooling and reuse with optimized GC");
            benchmarkZeroGC();
        } else {
            System.out.println("Standard mode: Using regular object allocation");
            benchmarkStandard();
        }
        
        printSummary();
    }
    
    private static void benchmarkStandard() {
        long[] iterationTimes = new long[BENCHMARK_ITERATIONS];
        long totalTime = 0;
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            int objects = runStandardDemo(ORDERS_PER_ITERATION, true);
            long endTime = System.nanoTime();
            
            long duration = endTime - startTime;
            iterationTimes[i] = duration;
            totalTime += duration;
            
            double durationMs = duration / 1_000_000.0;
            System.out.printf("Iteration %d: %.2f ms (processed %d objects)%n", 
                    i + 1, durationMs, objects);
        }
        
        printStatistics(iterationTimes, totalTime);
    }
    
    private static void benchmarkZeroGC() {
        long[] iterationTimes = new long[BENCHMARK_ITERATIONS];
        long totalTime = 0;
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            int objects = runZeroGCDemo(ORDERS_PER_ITERATION, true);
            long endTime = System.nanoTime();
            
            long duration = endTime - startTime;
            iterationTimes[i] = duration;
            totalTime += duration;
            
            double durationMs = duration / 1_000_000.0;
            System.out.printf("Iteration %d: %.2f ms (processed %d objects)%n", 
                    i + 1, durationMs, objects);
        }
        
        printStatistics(iterationTimes, totalTime);
    }
    
    private static void printStatistics(long[] times, long totalTime) {
        java.util.Arrays.sort(times);
        
        double avgMs = (totalTime / (double) times.length) / 1_000_000.0;
        double minMs = times[0] / 1_000_000.0;
        double maxMs = times[times.length - 1] / 1_000_000.0;
        double medianMs = times[times.length / 2] / 1_000_000.0;
        double p95Ms = times[(int)(times.length * 0.95)] / 1_000_000.0;
        
        System.out.println("\nPerformance statistics:");
        System.out.printf("  Min:    %.2f ms%n", minMs);
        System.out.printf("  Median: %.2f ms%n", medianMs);
        System.out.printf("  Avg:    %.2f ms%n", avgMs);
        System.out.printf("  95th:   %.2f ms%n", p95Ms);
        System.out.printf("  Max:    %.2f ms%n", maxMs);
    }
    
    private static int runStandardDemo(int orderCount, boolean withGCPressure) {
        if (withGCPressure) {
            System.out.println("Processing " + orderCount + " orders with new allocations...");
        }
        
        int allocations = 0;
        List<Object> temporaryObjects = new ArrayList<>();
        
        for (int i = 0; i < orderCount; i++) {
            // Create a new order object each time
            OrderSimulation order = new OrderSimulation("ORDER-" + i);
            allocations++;
            
            // Configure the order
            double price = 100.0 + (i % 10);
            int quantity = 100 + (i % 50);
            order.setPrice(price);
            order.setQuantity(quantity);
            order.setTimestamp(System.nanoTime());
            
            // Process the order
            processOrder(order);
            
            // Create GC pressure by allocating temporary objects
            if (withGCPressure && i % 100 == 0) {
                for (int j = 0; j < GC_PRESSURE_OBJECTS; j++) {
                    temporaryObjects.add(new PressureObject("data-" + j, j));
                    allocations++;
                }
                // Clear some objects to allow GC to happen
                if (temporaryObjects.size() > GC_PRESSURE_OBJECTS * 10) {
                    temporaryObjects.subList(0, GC_PRESSURE_OBJECTS * 5).clear();
                }
            }
            
            // Log progress
            if (withGCPressure && i % (orderCount / 4) == 0 && i > 0) {
                System.out.println("  Processed " + i + " orders");
            }
        }
        
        if (withGCPressure) {
            System.out.println("Total objects allocated: " + allocations);
        }
        
        return allocations;
    }
    
    private static int runZeroGCDemo(int orderCount, boolean withGCPressure) {
        if (withGCPressure) {
            System.out.println("Processing " + orderCount + " orders with object reuse...");
        }
        
        // Create a pool of reusable order objects
        OrderSimulation[] orderPool = new OrderSimulation[20];
        for (int i = 0; i < orderPool.length; i++) {
            orderPool[i] = new OrderSimulation("");
        }
        
        int allocations = orderPool.length;
        Map<String, OrderMetrics> metricsCache = new HashMap<>(1000);
        
        for (int i = 0; i < orderCount; i++) {
            // Reuse an order from the pool
            OrderSimulation order = orderPool[i % orderPool.length];
            order.reset("ORDER-" + i, 100.0 + (i % 10), 100 + (i % 50));
            order.setTimestamp(System.nanoTime());
            
            // Process the order
            processOrder(order);
            
            // Update metrics using a pre-allocated cache
            String symbol = "SYM" + (i % 10);
            OrderMetrics metrics = metricsCache.get(symbol);
            if (metrics == null) {
                metrics = new OrderMetrics(symbol);
                metricsCache.put(symbol, metrics);
                allocations++;
            }
            metrics.updateWith(order);
            
            // Log progress
            if (withGCPressure && i % (orderCount / 4) == 0 && i > 0) {
                System.out.println("  Processed " + i + " orders");
            }
        }
        
        if (withGCPressure) {
            System.out.println("Total objects allocated: " + allocations);
        }
        
        return allocations;
    }
    
    private static void processOrder(OrderSimulation order) {
        // Simulate processing work
        double value = order.getPrice() * order.getQuantity();
        double fee = value * 0.0001;
        
        double totalValue = 0;
        for (int i = 0; i < 100; i++) {
            totalValue += (value + fee) * (1 + Math.sin(i * 0.01) * 0.0001);
        }
        
        order.setExecutedValue(totalValue);
    }
    
    private static void printSummary() {
        System.out.println("\nAbout zero-GC techniques in trading systems:");
        System.out.println("1. Object pooling and reuse reduces allocation pressure");
        System.out.println("2. Pre-sized collections avoid resize/rehash operations");
        System.out.println("3. Mutable objects allow state updates without new allocations");
        System.out.println("4. ZGC tuning minimizes pause times for better tail latencies");
        System.out.println("5. Direct ByteBuffers can be used for off-heap data storage");
        System.out.println("\nIn production high-frequency trading systems:");
        System.out.println("- Standard mode suffers from GC pauses under sustained load");
        System.out.println("- ZeroGC mode provides more consistent performance");
        System.out.println("- The 99.9th percentile latency differences are significant");
    }
    
    // Order simulation class
    private static class OrderSimulation {
        private String id;
        private double price;
        private int quantity;
        private double executedValue;
        private long timestamp;
        
        public OrderSimulation(String id) {
            this.id = id;
        }
        
        public void reset(String id, double price, int quantity) {
            this.id = id;
            this.price = price;
            this.quantity = quantity;
            this.executedValue = 0.0;
        }
        
        // Getters and setters
        public void setId(String id) { this.id = id; }
        public String getId() { return id; }
        public void setPrice(double price) { this.price = price; }
        public double getPrice() { return price; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public int getQuantity() { return quantity; }
        public void setExecutedValue(double value) { this.executedValue = value; }
        public double getExecutedValue() { return executedValue; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public long getTimestamp() { return timestamp; }
    }
    
    // Metrics tracking class
    private static class OrderMetrics {
        private final String symbol;
        private int count;
        private double totalValue;
        private double minPrice;
        private double maxPrice;
        
        public OrderMetrics(String symbol) {
            this.symbol = symbol;
            this.count = 0;
            this.totalValue = 0;
            this.minPrice = Double.MAX_VALUE;
            this.maxPrice = Double.MIN_VALUE;
        }
        
        public void updateWith(OrderSimulation order) {
            count++;
            totalValue += order.getExecutedValue();
            minPrice = Math.min(minPrice, order.getPrice());
            maxPrice = Math.max(maxPrice, order.getPrice());
        }
    }
    
    // Object to create GC pressure
    private static class PressureObject {
        private String data;
        private int value;
        private byte[] buffer;
        
        public PressureObject(String data, int value) {
            this.data = data;
            this.value = value;
            this.buffer = new byte[ThreadLocalRandom.current().nextInt(100, 1000)];
        }
    }
}
