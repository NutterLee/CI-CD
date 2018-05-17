package gr.tsialiam.interviews.tesco.model.bank;

import gr.tsialiam.interviews.tesco.exceptions.VendingMachineInsufficientFundsException;
import gr.tsialiam.interviews.tesco.model.Coin;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FakeBank implements Bank {

    private final Map<Coin, Integer> money;

    public FakeBank(Map<Coin, Integer> money) {
        this.money = new EnumMap<>(money);
    }

    @Override
    public List<Coin> getChange(final int amount) {
        if(amount == 0) {
            return Collections.emptyList();
        } else if(amount == 90) {
            return Arrays.asList(Coin.FIFTY_PENCE, Coin.TWENTY_PENCE, Coin.TWENTY_PENCE);
        } else if(amount == 30){
            return Arrays.asList(Coin.TWENTY_PENCE, Coin.TEN_PENCE);
        }else {
            throw new VendingMachineInsufficientFundsException();
        }
    }

    @Override
    public void addAll(List<Coin> coins) {
        coins.forEach(coin -> {
            Integer quantity = money.get(coin);
            money.put(coin, quantity == null ? 1 : quantity + 1);
        });
    }
}
