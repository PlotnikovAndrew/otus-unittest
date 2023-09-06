package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    AccountDao accountDao = Mockito.mock(AccountDao.class);

    AccountServiceImpl accountServiceImpl = new AccountServiceImpl(accountDao);


    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    final Account accountForTests = new Account(1L, new BigDecimal(100));


    @BeforeEach
    void init() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
    }

    @Test
    void createAccountMock() {
        when(accountDao.saveAccount(argThat(account -> account.getAmount().equals(accountForTests.getAmount())))).thenReturn(new Account(accountForTests.getId(), accountForTests.getAmount()));

        Account createdAccountTest = accountServiceImpl.createAccount(accountForTests.getAmount());
        assertEquals(accountForTests, createdAccountTest);
    }

    @Test
    void createAccountCaptor() {
//  @TODO test account creation with ArgumentCaptor
        when(accountDao.saveAccount(accountCaptor.capture())).then(mock -> accountCaptor.getValue());
        Account createdAccountTest = accountServiceImpl.createAccount(accountForTests.getAmount());
        assertEquals(0L, createdAccountTest.getId());
    }

    @Test
    void addSum() {
        BigDecimal expectedBalance = new BigDecimal(0);
        BigDecimal accountBalance = accountServiceImpl.getMoney(accountForTests.getId(), accountForTests.getAmount());

        assertEquals(expectedBalance, accountBalance);
    }

    @Test
    void getSum() {
        BigDecimal expectedBalance = accountForTests.getAmount().add(accountForTests.getAmount());
        BigDecimal accountBalance = accountServiceImpl.putMoney(accountForTests.getId(), accountForTests.getAmount());

        assertEquals(expectedBalance, accountBalance);
    }

    @Test
    void getAccount() {
        assertEquals(accountForTests, accountServiceImpl.getAccount(accountForTests.getId()));
    }

    @Test
    void checkBalance() {
        assertEquals(accountForTests.getAmount(), accountServiceImpl.checkBalance(accountForTests.getId()));
    }
}
