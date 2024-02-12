package net.mixium.tradermc.storage.yaml.interfaces;

import java.util.List;

public interface StockAdapter {

    boolean isUserLoaded();
    List<String> getUserIndicesList();
    int getUserIndicesAmount(String inds);
    int getUserIndicesValue(String inds);
    double getIndicesTotalValue();
    void createNewUser();
    void BuyIndices(String inds, int amount);
    void SellIndices(String inds, int amount);

}
