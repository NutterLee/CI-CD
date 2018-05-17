package gr.tsialiam.interviews.tesco;

import gr.tsialiam.interviews.tesco.model.Coin;
import gr.tsialiam.interviews.tesco.model.Item;
import gr.tsialiam.interviews.tesco.model.Outputter;
import gr.tsialiam.interviews.tesco.model.bank.Bank;
import gr.tsialiam.interviews.tesco.exceptions.VendingMachineIllegalStateException;
import gr.tsialiam.interviews.tesco.exceptions.VendingMachineInsufficientFundsException;
import gr.tsialiam.interviews.tesco.exceptions.VendingMachineInsufficientMoney;
import gr.tsialiam.interviews.tesco.model.storage.Storage;

import java.util.LinkedList;
import java.util.List;

public class VendingMachine {

    private enum State {OFF, ON}

    private final Outputter outputter;
    private final Bank bank;
    private final Storage storage;
    private State state;

    private List<Coin> insertedCoins = new LinkedList<>();
    private Item selectedItem = null;

    public VendingMachine(Outputter outputter, Bank bank, Storage storage) {
        this.state = State.OFF;
        this.outputter = outputter;
        this.bank = bank;
        this.storage = storage;
    }

    public boolean isOn() {

        return state == State.ON;
    }

    public void setOn() {
        this.state = State.ON;
    }

    public void setOff() {
        returnSelectedItem();
        returnMoney();
        this.state = State.OFF;
    }

    public void insertCoin(String coinStr) {
        if (this.state != State.ON) {
            throw new VendingMachineIllegalStateException();
        }

        Coin coin = Coin.recogniseCoin(coinStr);
        insertedCoins.add(coin);

        int sumOfInsertedCoins = sumOfInsertedCoins();
        if (this.selectedItem != null && sumOfInsertedCoins >= this.selectedItem.getPrice()) {
            purchase(selectedItem, sumOfInsertedCoins);
        }
    }

    public void selectItem(Item selectedItem) {
        if (this.state != State.ON) {
            throw new VendingMachineIllegalStateException();
        }

        returnSelectedItem();

        storage.remove(selectedItem);
        this.selectedItem = selectedItem;

        int sumOfInsertedCoins = sumOfInsertedCoins();
        if (sumOfInsertedCoins >= selectedItem.getPrice()) {
            purchase(selectedItem, sumOfInsertedCoins);
        }
        //else throw new VendingMachineInsufficientMoney();
    }

    public void returnMoney() {
        if (this.state != State.ON) {
            throw new VendingMachineIllegalStateException();
        }

        outputter.returnCoin(insertedCoins);
        returnSelectedItem();
    }

    Item getSelectedItem() {
        return this.selectedItem;
    }

    private int sumOfInsertedCoins() {
        return insertedCoins.stream()
                .mapToInt(Coin::getValueInPence)
                .sum();
    }

    private void returnSelectedItem() {
        if (this.selectedItem != null) {
            storage.add(this.selectedItem);
            this.selectedItem = null;
        }
    }

    private void purchase(Item selectedItem, int sumOfInsertedCoins) {
        try {
            bank.addAll(insertedCoins);
            List<Coin> change = bank.getChange(sumOfInsertedCoins - selectedItem.getPrice());
            outputter.returnCoin(change);
            outputter.vendItem(selectedItem);
            insertedCoins.clear();
            this.selectedItem = null;
        } catch (VendingMachineInsufficientFundsException vmife) {
            outputter.returnCoin(bank.getChange(sumOfInsertedCoins));
        }
    }
}
