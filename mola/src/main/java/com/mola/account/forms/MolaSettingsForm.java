package com.mola.account.forms;

import com.mola.account.AccountType;

/**
 * Created by bilgi on 3/23/15.
 */
public class MolaSettingsForm {
    String host;
    String username;
    String password;
    AccountType accountType;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
