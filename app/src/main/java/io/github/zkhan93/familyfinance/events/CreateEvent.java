package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

public class CreateEvent<T extends BaseModel> extends BaseEventWithModel{
    public CreateEvent(T item) {
        super(item);
    }
}
