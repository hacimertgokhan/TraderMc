package net.mixium.tradermc.bist.interfaces;

public interface Indices {

    String getCurrentlyPrice();
    double getCurrentlyPriceAsDouble();

    String getCurrentlyRate();

    String getCurrentlyChangeOfPrice();

    String getLatestUpdateTime();

    String getStockSymbolName();

}