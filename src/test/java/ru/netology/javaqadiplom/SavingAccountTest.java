package ru.netology.javaqadiplom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SavingAccountTest {

    // Тесты конструктора

    // Негативный сценарий: конструктор должен бросать IllegalArgumentException, если minBalance > maxBalance
    @Test
    public void shouldThrowWhenMinGreaterThanMax() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SavingAccount(
                        5_000,   // initialBalance
                        2_000,   // minBalance (больше maxBalance!)
                        1_000,   // maxBalance
                        5        // rate
                )
        );
    }

    // Позитивный сценарий: корректные параметры конструктора создают объект
    @ParameterizedTest
    @CsvSource({
            "0,     0,    10000, 5",  // минимальные значения
            "1000,  500,  1500,  10"  // типичные значения
    })
    public void shouldCreateAccountWithValidParams(int initial, int min, int max, int rate) {
        SavingAccount acct = new SavingAccount(initial, min, max, rate);
        Assertions.assertEquals(initial, acct.getBalance());
        Assertions.assertEquals(min, acct.getMinBalance());
        Assertions.assertEquals(max, acct.getMaxBalance());
    }

    // Негативный сценарий: конструктор должен бросать IllegalArgumentException при отрицательной ставке
    @Test
    public void shouldThrowIfRateNegative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SavingAccount(0, 0, 1000, -1)
        );
    }

    // Негативный сценарий: конструктор должен бросать IllegalArgumentException при отрицательном minBalance
    @Test
    public void shouldThrowIfMinBalanceNegative() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SavingAccount(0, -1, 1000, 5)
        );
    }

    // Негативный сценарий: конструктор должен бросать IllegalArgumentException, если initialBalance < minBalance
    @Test
    public void shouldThrowIfInitialBelowMin() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SavingAccount(400, 500, 1000, 5)
        );
    }

    // Негативный сценарий: конструктор должен бросать IllegalArgumentException, если initialBalance > maxBalance
    @Test
    public void shouldThrowIfInitialAboveMax() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SavingAccount(1500, 0, 1000, 5)
        );
    }

    //Тесты метода add()

    // Оригинальный тест: позитивный сценарий пополнения ниже maxBalance


    @Test
    public void shouldAddLessThanMaxBalance() {
        SavingAccount account = new SavingAccount(
                2_000,
                1_000,
                10_000,
                5
        );

        account.add(3_000);
        Assertions.assertEquals(5_000, account.getBalance());
    }

    // Позитивный сценарий: метод add увеличивает баланс в пределах maxBalance
    @ParameterizedTest
    @CsvSource({
            "2000, 3000, true, 5000",   // обычное пополнение
            "2000, 8000, true,10000"    // пополнение ровно до maxBalance
    })
    public void shouldAddWithinLimits(int initial, int amount, boolean wantOk, int wantBal) {
        SavingAccount acct = new SavingAccount(initial, 0, 10000, 5);
        boolean ok = acct.add(amount);
        Assertions.assertEquals(wantOk, ok);
        Assertions.assertEquals(wantBal, acct.getBalance());
    }

    // Негативный сценарий: метод add не изменяет баланс при некорректной сумме или превышении maxBalance
    @ParameterizedTest
    @CsvSource({
            "2000,    0, false, 2000",  // ноль
            "2000,  -100, false,2000",  // отрицательное
            "2000, 9001, false,2000"    // превышение maxBalance
    })
    public void shouldRejectInvalidAdd(int initial, int amount, boolean wantOk, int wantBal) {
        SavingAccount acct = new SavingAccount(initial, 0, 10000, 5);
        boolean ok = acct.add(amount);
        Assertions.assertEquals(wantOk, ok);
        Assertions.assertEquals(wantBal, acct.getBalance());
    }

    // Тесты метода pay()


    // Метод  pay

    // Позитивны сценарий: метод pay изменяет баланс в границах допустимого
    @ParameterizedTest
    @CsvSource({
            "2000, 500,  true, 1500",  // списание в пределах
            "2000, 1000, true, 1000",    // списание ровно до minBalance
            "10000, 9000,  true, 1000"  // начальный баланс = maxBalance, списываем до minBalance
    })
    public void shouldPayWithinLimits(int initial, int amount, boolean wantOk, int wantBal) {
        SavingAccount acct = new SavingAccount(initial, 1000, 10000, 5);
        boolean ok = acct.pay(amount);
        Assertions.assertEquals(wantOk, ok);
        Assertions.assertEquals(wantBal, acct.getBalance());
    }

    // Негативный сценарий: метод pay не изменяет баланс при некорректной сумме или выходе ниже minBalance
    @ParameterizedTest
    @CsvSource({
            "2000,   0,  false, 2000",  // ноль
            "2000,  -10, false, 2000",  // отрицательное
            "2000, 1501, false, 2000"   // выход ниже minBalance
    })
    public void shouldRejectInvalidPay(int initial, int amount, boolean wantOk, int wantBal) {
        SavingAccount acct = new SavingAccount(initial, 1000, 10000, 5);
        boolean ok = acct.pay(amount);
        Assertions.assertEquals(wantOk, ok);
        Assertions.assertEquals(wantBal, acct.getBalance());
    }

    //Тесты метода yearChange()

    // Позитивный сценарий: расчет процентов по окончании года с усечением дробной части
    @ParameterizedTest
    @CsvSource({
            "2000,  10, 200",   // обычный расчёт
            "1001,  15, 150",   // дробная часть отбрасывается
    })
    public void shouldCalculateYearChange(int balance, int rate, int want) {
        SavingAccount acct = new SavingAccount(balance, 0, 1000000, rate);
        Assertions.assertEquals(want, acct.yearChange());
    }

    // Расчет процентов при минусовом балансе
    @Test
    public void shouldCalculateYearChangeWithNegativeBalance() {
        SavingAccount acct = new SavingAccount(0, 0, 1000000, 10);
        // вручную выставляем отрицательный баланс
        acct.balance = -100;

        Assertions.assertEquals(-10, acct.yearChange());
    }
}