package com.bank.service;

import com.bank.dto.AccountSummaryDto;
import com.bank.entity.Account;
import com.bank.entity.User;

public interface AccountService {

    Account createAccountForUser(User user);

    Account getAccountByEmail(String email);

    AccountSummaryDto getAccountSummary(String email);
}
