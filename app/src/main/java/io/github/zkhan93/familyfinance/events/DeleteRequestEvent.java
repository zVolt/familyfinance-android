package io.github.zkhan93.familyfinance.events;

/**
 * Created by zeeshan on 16/7/17.
 */

public class DeleteRequestEvent {
    String familyId;

    public DeleteRequestEvent(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyId() {
        return familyId;
    }
}
