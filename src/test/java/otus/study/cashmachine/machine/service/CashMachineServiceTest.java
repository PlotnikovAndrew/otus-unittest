package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static otus.study.cashmachine.TestUtil.getHash;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    private final BigDecimal TEST_START_AMOUNT_VALUE = BigDecimal.valueOf(2000);
    private final String TEST_CARD_NUMBER = "7777";
    private final String TEST_PIN = "1111";
    private final Card TEST_CARD = new Card(0, TEST_CARD_NUMBER, 0L, getHash(TEST_PIN));
    private final String NEW_TEST_PIN = "7777";

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
        BigDecimal amountToGet = BigDecimal.valueOf(1600);
        List<Integer> notepadsToGet = List.of(0,1,1,1);

        doReturn(amountToGet).when(cardService).getMoney(TEST_CARD_NUMBER, TEST_PIN,amountToGet);
        doReturn(notepadsToGet).when(moneyBoxService).getMoney(any(),anyInt());

        List<Integer> result = Assertions.assertDoesNotThrow(() -> cashMachineService.getMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, amountToGet));
        assertEquals(notepadsToGet, result);
    }

    @Test
    void putMoney() {
        List<Integer> banknotes = List.of(1,1,1,1);
        BigDecimal amountToPut = BigDecimal.valueOf(6600);

        when(cardsDao.getCardByNumber(any())).thenReturn(TEST_CARD);
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_PIN)).thenReturn(TEST_START_AMOUNT_VALUE);
        when(cardService.putMoney(TEST_CARD_NUMBER,TEST_PIN,amountToPut)).thenReturn(amountToPut);

        BigDecimal result= cashMachineService.putMoney(cashMachine, TEST_CARD_NUMBER, TEST_PIN, banknotes);
        assertEquals(result, amountToPut);
    }

    @Test
    void checkBalance() {
        when(cardsDao.getCardByNumber(any())).thenReturn(TEST_CARD);
        when(cardService.getBalance(TEST_CARD_NUMBER, TEST_PIN)).thenReturn(TEST_START_AMOUNT_VALUE);

        assertEquals(TEST_START_AMOUNT_VALUE, cashMachineService.checkBalance(cashMachine, TEST_CARD_NUMBER, TEST_PIN));
    }

    @Test
    void changePin() {
//create change pin test using spy as implementation and ArgumentCaptor and thenReturn
        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(TEST_CARD_NUMBER)).thenReturn(TEST_CARD);

        cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, NEW_TEST_PIN);

        verify(cardsDao) .saveCard(argumentCaptor.capture());
        Card captorValue = argumentCaptor.getValue();
        assertEquals(TEST_CARD_NUMBER, captorValue.getNumber());
        assertEquals(getHash(NEW_TEST_PIN), captorValue.getPinCode());
    }

    @Test
    void changePinWithAnswer() {
// create change pin test using spy as implementation and mock an thenAnswer
        ArgumentCaptor<Card> argumentCaptor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.getCardByNumber(TEST_CARD_NUMBER)).thenReturn(TEST_CARD);
        when(cardsDao.saveCard(TEST_CARD)).thenAnswer(card -> new Card(TEST_CARD.getId(),TEST_CARD.getNumber(), TEST_CARD.getAccountId(), TEST_CARD.getPinCode()));

        cashMachineService.changePin(TEST_CARD_NUMBER, TEST_PIN, NEW_TEST_PIN);

        verify(cardsDao) .saveCard(argumentCaptor.capture());
        Card captorValue = argumentCaptor.getValue();
        assertEquals(TEST_CARD_NUMBER, captorValue.getNumber());
        assertEquals(getHash(NEW_TEST_PIN), captorValue.getPinCode());
    }
}