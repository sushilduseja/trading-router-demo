package com.trading.benchmark;

import java.nio.ByteBuffer;

/**
 * Demonstrates using DirectByteBuffer for off-heap memory access.
 * This is another zero-GC technique for handling large data without GC pressure.
 */
public class DirectBufferDemo {
    
    // A simple order structure in binary format
    // orderId(8) + symbol(4) + type(1) + price(8) + quantity(4) + clientId(4) = 29 bytes
    private static final int ORDER_SIZE = 29;  
    
    private final ByteBuffer buffer;
    private final int capacity;
    
    public DirectBufferDemo(int orderCapacity) {
        this.capacity = orderCapacity;
        // Allocate direct buffer - this memory is off-heap
        this.buffer = ByteBuffer.allocateDirect(ORDER_SIZE * orderCapacity);
        System.out.println("Created direct buffer with capacity for " + orderCapacity + 
                " orders (" + (ORDER_SIZE * orderCapacity) + " bytes)");
    }
    
    public void writeOrder(int index, long orderId, int symbolCode, byte type, 
                           double price, int quantity, int clientCode) {
        if (index >= capacity) {
            throw new IndexOutOfBoundsException("Buffer index out of bounds: " + index);
        }
        
        int position = index * ORDER_SIZE;
        buffer.position(position);
        
        // Write order data
        buffer.putLong(orderId);
        buffer.putInt(symbolCode);
        buffer.put(type);
        buffer.putDouble(price);
        buffer.putInt(quantity);
        buffer.putInt(clientCode);
    }
    
    public void readOrder(int index) {
        if (index >= capacity) {
            throw new IndexOutOfBoundsException("Buffer index out of bounds: " + index);
        }
        
        int position = index * ORDER_SIZE;
        buffer.position(position);
        
        // Read order data
        long orderId = buffer.getLong();
        int symbolCode = buffer.getInt();
        byte type = buffer.get();
        double price = buffer.getDouble();
        int quantity = buffer.getInt();
        int clientCode = buffer.getInt();
        
        System.out.println("Read order - id: " + orderId + 
                ", symbol: " + symbolCode + 
                ", type: " + type + 
                ", price: " + price + 
                ", qty: " + quantity + 
                ", client: " + clientCode);
    }
    
    public static void main(String[] args) {
        // Create buffer for 1 million orders (~29MB)
        DirectBufferDemo demo = new DirectBufferDemo(1_000_000);
        
        // Write some sample orders
        for (int i = 0; i < 10; i++) {
            // Use an int code to represent the symbol (e.g., first 4 bytes of ASCII)
            int symbolCode = ('A' << 24) | ('A' << 16) | ('P' << 8) | 'L';
            demo.writeOrder(i, 1000 + i, symbolCode, (byte)1, 150.0 + i, 100 + i, 42);
        }
        
        // Read back a few orders
        for (int i = 0; i < 5; i++) {
            demo.readOrder(i);
        }
        
        System.out.println("Direct buffer demo completed");
        System.out.println("Benefits of direct buffers in low latency trading:");
        System.out.println("1. No GC overhead for large data structures");
        System.out.println("2. Memory layout optimized for sequential access");
        System.out.println("3. Potential for zero-copy operations with network/disk I/O");
        System.out.println("4. Predictable memory access patterns");
    }
}
