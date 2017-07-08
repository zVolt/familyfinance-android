package io.github.zkhan93.familyfinance.helpers;

/**
 * Created by zeeshan on 8/7/17.
 */

public interface MemberItemActionClbk {
    void removeMember(String memberId);

    void toggleMemberSms(String memberId);
}
