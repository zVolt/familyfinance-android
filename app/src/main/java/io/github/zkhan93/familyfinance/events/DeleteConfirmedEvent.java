package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.BaseModel;

/**
 * todo: split this to seperate events, this leads to call all the confirmevent listeners
 * Created by zeeshan on 15/7/17.
 */

public class DeleteConfirmedEvent<T extends BaseModel> extends BaseEventWithModel {
    public DeleteConfirmedEvent(BaseModel item) {
        super(item);
    }
}
