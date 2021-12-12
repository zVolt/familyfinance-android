package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

public class UpdateEvent<T extends BaseModel> extends BaseEventWithModel{
    public UpdateEvent(T item) {
        super(item);
    }
}
