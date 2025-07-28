package ru.netology.javaqadiplom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CreditAccountTest {


    /**
     * Тестирование конструктора
     */

    // Создание кредитного счета с валидными данными (позитивный сценарий):
    // - граничные значения начального баланса (0,1)
    // - граничные значения кредитного лимита (1,2)
    // - граничные значения ставки (1,2)
    @ParameterizedTest
    @CsvSource({
            "0,1,1,0,1",
            "1,2,2,1,2"
    })
    public void shouldCreateCreditAccount(int initialBalance, int creditLimit, int rate, int expectedInitialBalance, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, creditLimit, rate);

        Assertions.assertEquals(expectedInitialBalance, account.getBalance());
        Assertions.assertEquals(expected, account.getCreditLimit());
        Assertions.assertEquals(expected, account.getRate());
    }

    // Создание кредитного счета с невалиными данными (негативный сценарий):
    // - выкидывание исключения при начальном отрицательном балансе

    @Test
    public void shouldIllegalArgumentExceptionIfInitialBalanceIsIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CreditAccount(-1, 5_000, 15);
        });
    }

    // Создание кредитного счета c невалидными данными (негативный сценарий):
    // - выкидывание исключения при нулевом кредитном лимите
    @Test
    public void shouldIllegalArgumentExceptionIfCreditLimitIsIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CreditAccount(3_000, 0, 15);
        });
    }

    // Создание кредитного счета с невалидными данными (негативный сценарий):
    // - выкидывание исключения при нулевой ставке
    @Test
    public void shouldIllegalArgumentExceptionIfRateIsIncorrect() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CreditAccount(3_000, 5_000, 0);
        });
    }

    /**
     * Тестирование метода Pay
     */

    // Списание с кредитного счета с положительным/нулевым балансом
    // суммуы не превышающей лимит (позитивный сценарий)
    @ParameterizedTest
    @CsvSource({
            "0,5000,-5000",  //Списание с нулевого баланса до лимита
            "0,4999,-4999",  //Списание с нулевого баланса до граничного значения
            "1,5001,-5000",  //Списание с положительного баланса до лимита
            "1,5000,-4999",  //Списание с положительного баланса до граничного значения
            "1,1,0"          //Списания с положительного баланса до обнуления счета
    })
    public void shouldPayToDecreaseBalanceIfBalanceIsPositiveOrZero(int initialBalance, int amount, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, 15);

        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Списание с кредитного счета с отрицательным балансом
    // суммы не превышающей лимит (позитивный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1, 4998,-4999",  //Списание до граничного значения
            "1, 4999,-5000",  //Списание до лимита
            "4999,1,-5000"    //Списание до лимита
    })
    public void shouldPayToDecreaseBalanceIfBalanceIsNegative(int firstPay, int amount, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, 15);
        account.pay(firstPay);
        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Списание с кредитного счета с положительным/нулевым балансом
    // суммы превышающей лимит (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "0,5001,0",
            "1,5002,1"
    })
    public void shouldPayToNotChangeBalanceIfBalanceIsPositiveOrZero(int initialBalance, int amount, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, 15);

        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Списание с кредитного счета с отрицательным балансом
    // суммы превыщающий лимит (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1, 5000,-1",
            "4999,2,-4999",
            "5000,1,-5000"
    })
    public void shouldPayToNotChangeBalanceIfBalanceIsNegative(int firstPay, int amount, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, 15);

        account.pay(firstPay);
        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Списание с кредитного счета с положительным/нулевым балансом
    // некорректной суммы: отрицательная сумма списания, 0 сумма списания (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "0,0,0",
            "1,0,1",
            "0,-1,0",
            "1,-1,1"
    })
    public void shouldPayToNotChangeBalanceWithIncorrectAmountIfBalanceIsPositiveOrZero(int initialBalance, int amount, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, 15);

        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Списание с кредитного счета с отрицательным балансом
    // некорректной суммы: отрицательная сумма списания, 0 сумма списания (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1,0,-1",
            "1,-1,-1",
            "4999,0,-4999",
            "4999,-1,-4999",
            "5000,0,-5000",
            "5000,-1,-5000"
    })
    public void shouldPayToNotChangeBalanceWithIncorrectAmountIfBalanceIsNegative(int firstPay, int amount, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, 15);

        account.pay(firstPay);
        account.pay(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }


    /**
     * Тестирование метода Add
     */

    // Пополнение кредитного счета с положительным/нулевым балансом
    // на сумму 3_000 (позитивный сценарий)
    @ParameterizedTest
    @CsvSource({
            "0,3000",
            "1,3001",
    })
    public void shouldAddToIncreaseBalanceIfBalanceIsPositiveOrZero(int initialBalance, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, 15);

        account.add(3_000);

        Assertions.assertEquals(expected, account.getBalance());
    }

    // Пополнение кредитного счета с отрицательным балансом
    // до погашения долга (позитивный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1,1,0",        // Полное погашения малого долга
            "4999,4999,0",  // Полное погашения среднего долга
            "5000,5000,0",  // Полное погашение максимального долга
            "1,2,1",        // Переход из отрицательного в положительный баланс
            "4999,5000,1",  // Переход с избыточным пополнением
            "5000,5001,1",   // Переход с избыточным пополнением
            "4999,1,-4998", // Частичное погашение среднего долга
            "5000,4999,-1" // Частичное погашение максимального долга
    })
    public void shouldAddToIncreaseBalanceIfBalanceIsNegative(int pay, int amount, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, 15);

        account.pay(pay);
        account.add(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    //Пополнение кредитного счета с положительным/нулевым балансом
    // на некорретную сумму: отрицательная сумма пополнения, 0 сумма пополнения (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "0,-1,0",
            "1,-1,1",
            "0,0,0",
            "1,0,1"
    })
    public void shouldAddToNotChangeBalanceIfBalanceIsPositiveOrZero(int initialBalance, int amount, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, 15);

        account.add(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }

    //Пополнение кредитного счета с отрицательным балансом
    // на некорретную сумму: отрицательная сумма пополнения, 0 сумма пополнения (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1,0,-1",
            "1,-1,-1",
            "4999,0,-4999",
            "4999,-1,-4999",
            "5000,0,-5000",
            "5000,-1,-5000"
    })
    public void shouldAddToNotChangeBalanceIfBalanceIsNegative(int pay, int amount, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, 15);

        account.pay(pay);
        account.add(amount);

        Assertions.assertEquals(expected, account.getBalance());
    }


    /**
     * Тестирование метода yearChange
     */

    //Расчет процента при отрицательном балансе за год (позитивный сценарий)
    @ParameterizedTest
    @CsvSource({
            "5000,1,-50",
            "4999,1,-49",
            "1,1,0",
            "5000,2,-100",
            "4999,2,-99",
            "1,2,0"
    })
    public void shouldCalculatePercentIfNegativeBalance(int pay, int rate, int expected) {
        CreditAccount account = new CreditAccount(0, 5_000, rate);

        account.pay(pay);

        Assertions.assertEquals(expected, account.yearChange());
    }

    //Расчет процента при положительном/нулевом балансе за год (негативный сценарий)
    @ParameterizedTest
    @CsvSource({
            "1,1,0",
            "0,1,0",
            "1,2,0",
            "0,2,0"
    })
    public void shouldNotCalculatePercentIfPositiveBalance(int initialBalance, int rate, int expected) {
        CreditAccount account = new CreditAccount(initialBalance, 5_000, rate);

        Assertions.assertEquals(expected, account.yearChange());
    }
}