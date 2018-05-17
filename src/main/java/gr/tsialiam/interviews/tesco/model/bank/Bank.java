package gr.tsialiam.interviews.tesco.model.bank;

import gr.tsialiam.interviews.tesco.model.Coin;

import java.util.List;

public interface Bank {
    List<Coin> getChange(final int amount);
    void addAll(List<Coin> coins);
}
