/**
 * EDUCATIONAL RESOURCE - NOT REQUIRED FOR SIMPLE DEMO
 * 
 * Classes representing different garbage collection strategies for the trading demo.
 * <p>
 * The GCMode interface defines the contract for GC strategies, with two implementations:
 * <ul>
 *   <li>StandardGCMode - Uses standard allocation patterns with default GC settings</li>
 *   <li>ZeroGCMode - Configures ZGC and uses allocation reduction techniques</li>
 * </ul>
 * <p>
 * Note: These classes are not used by SimpleMain and are kept for educational purposes.
 * The GC strategies are now implemented directly within SimpleMain.
 * <p>
 * These classes allow the benchmark to compare how different GC strategies
 * impact latency and throughput in a high-frequency trading system.
 */
package com.trading.gc;
