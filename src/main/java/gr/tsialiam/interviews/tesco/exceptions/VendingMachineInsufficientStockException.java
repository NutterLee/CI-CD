package gr.tsialiam.interviews.tesco.exceptions;

import gr.tsialiam.interviews.tesco.model.Item;

public class VendingMachineInsufficientStockException extends RuntimeException {

    public VendingMachineInsufficientStockException(Item item) {
        super("Insufficient stock " + item);
    }
}
