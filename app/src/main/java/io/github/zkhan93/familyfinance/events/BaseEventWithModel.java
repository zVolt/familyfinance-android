package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

/**
 * Created by zeeshan on 20/7/17.
 */

public abstract class BaseEventWithModel<T extends BaseModel> extends BaseEvent{
    private final T item;

    public BaseEventWithModel(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }
}
