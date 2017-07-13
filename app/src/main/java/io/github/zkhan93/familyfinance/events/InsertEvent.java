package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.Account;

/**
 * Created by zeeshan on 13/7/17.
 */

public class InsertEvent<T> {
    private T item;

    public InsertEvent() {
    }

    public InsertEvent(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
