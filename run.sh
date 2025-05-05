#!/bin/bash

# Simple script for running the trading router demo

if [ $# -ne 1 ]; then
  echo "Usage: $0 [standard|zerogc]"
  exit 1
fi

MODE=$1

# Validate mode
if [ "$MODE" != "standard" ] && [ "$MODE" != "zerogc" ]; then
  echo "Invalid mode: $MODE. Use 'standard' or 'zerogc'"
  exit 1
fi

echo "Building project..."

# Create build directory
mkdir -p build/classes

# Compile main class
echo "Compiling source files..."
javac -d build/classes src/main/java/com/trading/SimpleMain.java

# Create JAR file
echo "Creating JAR file..."
jar cfe build/trading-router-demo.jar com.trading.SimpleMain -C build/classes .

echo "Running in $MODE mode..."

if [ "$MODE" == "standard" ]; then
  echo "Using standard allocation mode with default GC settings"
  java -jar build/trading-router-demo.jar standard
else
  echo "Using ZeroGC mode with optimized settings for low latency"
  java -XX:+UseZGC -XX:+AlwaysPreTouch -XX:+DisableExplicitGC \
       -jar build/trading-router-demo.jar zerogc
fi
