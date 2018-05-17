package gr.tsialiam.interviews.tesco.exceptions;

public class VendingMachineUnrecognizedCoinException extends RuntimeException {
    public VendingMachineUnrecognizedCoinException(String coinStr){
        super("Unable to recognise coin " + coinStr);
    }
}
