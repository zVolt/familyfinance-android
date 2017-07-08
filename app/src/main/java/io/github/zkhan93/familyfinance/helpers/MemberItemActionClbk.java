package io.github.zkhan93.familyfinance.helpers;

/**
 * Created by zeeshan on 8/7/17.
 */

public interface MemberItemActionClbk {
    void remove(String memberId);

    void toggleSms(String memberId);
}
