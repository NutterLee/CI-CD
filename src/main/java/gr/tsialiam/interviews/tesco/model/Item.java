package gr.tsialiam.interviews.tesco.model;

public enum Item {

    A(60),
    B(100),
    C(170);

    private final int price;

    Item(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}
