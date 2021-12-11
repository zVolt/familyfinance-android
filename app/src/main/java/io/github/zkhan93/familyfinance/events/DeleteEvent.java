package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

/**
 * Created by zeeshan on 22/7/17.
 */

public class DeleteEvent<T extends BaseModel> extends BaseEventWithModel {
    public DeleteEvent(BaseModel item) {
        super(item);
    }
}
