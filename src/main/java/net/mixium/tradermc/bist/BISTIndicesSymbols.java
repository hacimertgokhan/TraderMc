package net.mixium.tradermc.bist;

public class BISTIndicesSymbols {

    private final String stockSymbol;
    public BISTIndicesSymbols(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    @Override
    public String toString() {
        return "net.mixium.tradermc.api.StockSymbol{" +
                "stockSymbol='" + stockSymbol + '\'' +
                '}';
    }
}