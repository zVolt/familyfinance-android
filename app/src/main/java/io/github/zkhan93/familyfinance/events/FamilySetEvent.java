package io.github.zkhan93.familyfinance.events;

import io.github.zkhan93.familyfinance.models.Request;

/**
 * Created by zeeshan on 17/7/17.
 */

public class FamilySetEvent {
    private Request request;

    public FamilySetEvent(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
