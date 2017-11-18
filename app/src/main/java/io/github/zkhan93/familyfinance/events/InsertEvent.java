package io.github.zkhan93.familyfinance.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeeshan on 13/7/17.
 */

public class InsertEvent<T> extends BaseEvent {
    private List<T> items;

    public InsertEvent() {
        items = new ArrayList<>();
    }

    public InsertEvent(List<T> items) {
        if (items == null)
            items = new ArrayList<>();
        else
            this.items = items;
    }

    public List<T> getItems() {
        return items;
    }
}
