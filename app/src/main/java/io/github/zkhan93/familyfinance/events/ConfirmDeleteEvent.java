package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

public class ConfirmDeleteEvent<T extends BaseModel> extends BaseEventWithModel{
    public ConfirmDeleteEvent(BaseModel item) {
        super(item);
    }
}
