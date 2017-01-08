package com.mola.account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bilgi on 3/23/15.
 */
public enum AccountType{

    oanda(1), fxtrade(2), mola(3);
    int type;

    AccountType(int type){
        this.type=type;
    }

    public int getValue() {
        return type;
    }

    public static List<AccountType> getTypes(){
        List<AccountType> accountTypes = new ArrayList<>();
        AccountType[] values = AccountType.values();
        for(AccountType type: values){
            accountTypes.add(type);
        }
        return accountTypes;
    }
}
