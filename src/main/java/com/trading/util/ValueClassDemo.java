package com.trading.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates using Java 21 features like record classes
 * to create more efficient immutable data objects.
 */
public class ValueClassDemo {
    private static final Logger LOG = LoggerFactory.getLogger(ValueClassDemo.class);
    
    // Record class for price levels in an order book
    public record PriceLevel(double price, int quantity) {
        // Records provide built-in equals, hashCode, and toString
    }
    
    // Record for market data updates
    public record MarketDataUpdate(
            String symbol,
            long timestamp,
            PriceLevel[] bids,
            PriceLevel[] asks) {
    }
    
    public static void main(String[] args) {
        LOG.info("Demonstrating Java 21 records for efficient value objects");
        
        // Create some price levels
        PriceLevel[] bids = {
            new PriceLevel(150.25, 100),
            new PriceLevel(150.20, 200),
            new PriceLevel(150.15, 300)
        };
        
        PriceLevel[] asks = {
            new PriceLevel(150.30, 150),
            new PriceLevel(150.35, 250),
            new PriceLevel(150.40, 350)
        };
        
        // Create a market data update
        MarketDataUpdate update = new MarketDataUpdate("AAPL", System.nanoTime(), bids, asks);
        
        LOG.info("Market data update: {}", update);
        LOG.info("Best bid: {}", update.bids()[0]);
        LOG.info("Best ask: {}", update.asks()[0]);
        LOG.info("Spread: {}", update.asks()[0].price() - update.bids()[0].price());
        
        LOG.info("Value class demo completed");
        LOG.info("Benefits in low latency trading:");
        LOG.info("1. Compact object representation");
        LOG.info("2. Immutability ensures thread safety without locks");
        LOG.info("3. Efficient garbage collection with generational hypotheses");
        LOG.info("4. Clear semantics with built-in equals/hashCode");
    }
}
