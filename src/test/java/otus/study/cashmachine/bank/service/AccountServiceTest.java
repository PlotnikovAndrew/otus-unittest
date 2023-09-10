package otus.study.cashmachine.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.AccountDao;
import otus.study.cashmachine.bank.data.Account;
import otus.study.cashmachine.bank.service.impl.AccountServiceImpl;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class AccountServiceTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    final Account accountForTests = new Account(1L, new BigDecimal(100));


    @BeforeEach
    void init() {
//        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
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
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        accountServiceImpl.createAccount(new BigDecimal(100));
        verify(accountDao).saveAccount(accountCaptor.capture());

        assertEquals(new BigDecimal(100), accountCaptor.getValue().getAmount());
    }

    @Test
    void addSum() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
        BigDecimal expectedBalance = new BigDecimal(0);
        BigDecimal accountBalance = accountServiceImpl.getMoney(accountForTests.getId(), accountForTests.getAmount());

        assertEquals(expectedBalance, accountBalance);
    }

    @Test
    void getSum() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
        BigDecimal expectedBalance = accountForTests.getAmount().add(accountForTests.getAmount());
        BigDecimal accountBalance = accountServiceImpl.putMoney(accountForTests.getId(), accountForTests.getAmount());

        assertEquals(expectedBalance, accountBalance);
    }

    @Test
    void getAccount() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
        assertEquals(accountForTests, accountServiceImpl.getAccount(accountForTests.getId()));
    }

    @Test
    void checkBalance() {
        when(accountDao.getAccount(anyLong())).thenReturn(accountForTests);
        assertEquals(accountForTests.getAmount(), accountServiceImpl.checkBalance(accountForTests.getId()));
    }
}
