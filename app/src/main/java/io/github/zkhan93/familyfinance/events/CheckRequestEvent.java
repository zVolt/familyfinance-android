package io.github.zkhan93.familyfinance.events;

/**
 * Created by zeeshan on 16/7/17.
 */

public class CheckRequestEvent {
    String familyId;

    public CheckRequestEvent(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyId() {
        return familyId;
    }
}
