/**
 * EDUCATIONAL RESOURCE - NOT REQUIRED FOR SIMPLE DEMO
 * 
 * Benchmark and demonstration classes for comparing standard and zero-GC modes
 * in a high-throughput trading system.
 * <p>
 * The classes in this package include:
 * <ul>
 *   <li>ObjectPoolDemo - Demonstrates how object pooling can reduce GC pressure</li>
 *   <li>StringInternDemo - Shows how string interning avoids duplicate strings</li>
 *   <li>ThreadAffinityDemo - Demonstrates thread-to-core pinning for reduced jitter</li>
 *   <li>DirectBufferDemo - Shows how to use off-heap memory with DirectByteBuffers</li>
 * </ul>
 * <p>
 * Note: These classes are not used by SimpleMain and are kept for educational purposes.
 * They would require external dependencies to run properly.
 * <p>
 * These classes together demonstrate various techniques to achieve consistent
 * low-latency performance in Java applications.
 */
package com.trading.benchmark;
