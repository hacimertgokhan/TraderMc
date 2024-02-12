package net.mixium.tradermc.bist;

import net.mixium.tradermc.bist.interfaces.Indices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class BISTIndices implements Indices {

    private BISTIndicesSymbols BISTIndicesSymbols;

    public BISTIndices(BISTIndicesSymbols BISTIndicesSymbols) {
        this.BISTIndicesSymbols = BISTIndicesSymbols;
    }

    @Override
    public String getCurrentlyPrice() {return getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(1);}

    @Override
    public double getCurrentlyPriceAsDouble() {
        String str = getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(1);
        return Double.parseDouble(str.replace(",", "."));
    }

    @Override
    public String getCurrentlyRate() {
        return getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(2);
    }

    @Override
    public String getLatestUpdateTime() {
        return getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(3);
    }

    @Override
    public String getStockSymbolName() {
        return getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(0);
    }

    @Override
    public String getCurrentlyChangeOfPrice() {return getStockSymbol(BISTIndicesSymbols, getCurrentlyRequest()).get(4);}

    private ArrayList<String> getStockSymbol(BISTIndicesSymbols BISTIndicesSymbols, ArrayList<String> arrayList) {
        ArrayList<String> stockSymbolListFeatures = new ArrayList<>();
        int findIndex = -1;
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).contains(BISTIndicesSymbols.getStockSymbol())) {
                findIndex = i;
                break;
            }
        }
        if (findIndex != -1) {
            String findLine = arrayList.get(findIndex);
            String[] veriler = findLine.split(", ");
            for (String veri : veriler) {
                String[] parcalar = veri.split("\\|");
                for (String parca : parcalar) {
                    stockSymbolListFeatures.add(parca);
                }
            }
        }
        else {
            throw new RuntimeException("StockSymbol can't find.");

        }
        return stockSymbolListFeatures;
    }

    private ArrayList<String> getCurrentlyRequest() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            String apiUrl = "https://www.sabah.com.tr/json/canli-borsa-verileri";

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            String[] kelimeler = new String[0];

            while ((line = reader.readLine()) != null) {
                kelimeler = line.split("~");
            }
            reader.close();

            for (String s : kelimeler) {
                arrayList.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

}