package gr.tsialiam.interviews.tesco.model.storage;

import gr.tsialiam.interviews.tesco.model.Item;

public interface Storage {

    void remove(Item item);

    void add(Item item);

}
