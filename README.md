# Low Latency Trading Router Demo

This project demonstrates the impact of zeroGC techniques on a realistic trading system built with Java 21.

## Key Features

- **Real-world use case**: Simulates a trading order processing system
- **Zero GC mode**: Object reuse, pooling and ZGC optimization
- **Benchmark**: Side-by-side comparison of standard vs. zero GC approaches
- **Simple implementation**: No external dependencies required

## About the "ZeroGC" Mode

The term "ZeroGC" in this project is used to illustrate object pooling and reuse concepts, but is not truly "zero garbage collection" in the strict sense. In production trading systems, true zero-GC approaches typically involve:

1. **Off-heap memory**: Using DirectByteBuffers and Unsafe for manual memory management
2. **Value types**: Using stack allocation where possible (Project Valhalla will improve this)
3. **Custom memory management**: Implementing specialized allocators for specific use cases
4. **Disruptor pattern**: Using pre-allocated ring buffers for inter-thread communication

This simplified demo focuses on the basic techniques of object pooling and reuse to reduce allocation pressure, which is just the first step toward building truly pauseless systems.

## Project Structure

This project is designed to be simple and self-contained:

- **SimpleMain**: The entry point that requires no external dependencies
- **Benchmark package**: Contains additional zero-GC technique examples (for educational purposes)
- **No external dependencies**: Everything runs with standard Java libraries

> **Note:** The original version of this project included a `Main` class that used LMAX Disruptor,
> QuickFIX/J, and other external libraries to demonstrate these techniques in a more realistic
> trading environment. The current version uses `SimpleMain` for simplicity and ease of use.

## Educational Resources

This project includes additional code that is not used by SimpleMain but is kept for educational purposes:

- **Benchmark package**: Examples of various zero-GC techniques:
  - ObjectPoolDemo - Demonstrates object pooling
  - StringInternDemo - Shows string interning for avoiding duplicates
  - ThreadAffinityDemo - Demonstrates thread-to-core pinning
  - DirectBufferDemo - Shows off-heap memory usage
  
- **Additional packages**: Original implementations that used external dependencies:
  - disruptor - LMAX Disruptor pattern implementation 
  - fix - QuickFIX/J implementation
  - gc - GC strategy patterns
  - model - Domain model classes
  - util - Utility classes

These packages are not required to run SimpleMain but provide valuable examples of advanced techniques.

## Performance Comparison

The system can be run in two modes:

1. **Standard Mode**: Uses regular object allocation with default GC settings
2. **ZeroGC Mode**: Uses object pooling/reuse with ZGC tuned for low latency

Benchmark results typically show:
- Lower median latency in ZeroGC mode
- Significantly reduced outliers and "hiccups" from GC pauses
- Better throughput under sustained load

## Quick Setup Guide

1. Clone the repository
2. Make the run script executable: `chmod +x run.sh`
3. Run `./run.sh standard` to test standard GC mode
4. Run `./run.sh zerogc` to test zeroGC mode
5. Compare the benchmark results

The run script will automatically detect if you have Gradle installed and fallback to manual compilation if needed.