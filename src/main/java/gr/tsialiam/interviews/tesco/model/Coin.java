package gr.tsialiam.interviews.tesco.model;

import gr.tsialiam.interviews.tesco.exceptions.VendingMachineUnrecognizedCoinException;

import java.util.Arrays;

public enum Coin {

    TEN_PENCE("£0.10", 10),
    TWENTY_PENCE("£0.20", 20),
    FIFTY_PENCE("£0.50", 50),
    ONE_POUND("£1", 100);

    private final String id;
    private final int valueInPence;

    Coin(String id, int valueInPence) {
        this.id = id;
        this.valueInPence = valueInPence;
    }

    public String getId() {
        return this.id;
    }

    public int getValueInPence() {
        return this.valueInPence;
    }

    public static Coin recogniseCoin(String coinStr) {
        return Arrays.stream(Coin.values())
                .filter(coin -> coin.id.equals(coinStr))
                .findFirst()
                .orElseThrow(() -> new VendingMachineUnrecognizedCoinException(coinStr));
    }
}
