package gr.tsialiam.interviews.tesco.model.storage;

import gr.tsialiam.interviews.tesco.model.Item;
import gr.tsialiam.interviews.tesco.exceptions.VendingMachineInsufficientStockException;

import java.util.EnumMap;
import java.util.Map;

public class StorageImpl implements Storage {
    private final Map<Item, Integer> stock;

    public StorageImpl(Map<Item, Integer> stock) {
        this.stock = new EnumMap<>(stock);
    }

    @Override
    public void remove(Item item) {
        Integer quantity = stock.get(item);
        if (quantity == null || quantity < 1) {
            throw new VendingMachineInsufficientStockException(item);
        }
        stock.put(item, quantity - 1);
    }

    @Override
    public void add(Item item) {

        stock.merge(item, 1, (a, b) -> a + b);
    }

}
