package io.github.zkhan93.familyfinance.models;

import java.util.Date;

/**
 * Created by zeeshan on 7/7/17.
 */

public class Account {
    String name, bankName, ifsc, accountNumber;
    float amount;
    Date updatedOn;
    Member updatedBy;
}
