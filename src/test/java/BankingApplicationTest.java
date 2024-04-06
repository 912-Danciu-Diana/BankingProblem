package test.java;

import main.java.domain.*;
import main.java.repository.AccountsRepository;
import main.java.services.SavingsManagerService;
import main.java.services.TransactionManagerService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static main.java.seed.AccountsSeedData.savingsAccountA;
import static main.java.seed.AccountsSeedData.savingsAccountB;
import static main.java.seed.CardsSeedData.card1;
import static main.java.seed.CardsSeedData.card2;
import static main.java.seed.CardsSeedData.card3;
import static org.junit.Assert.*;

public class BankingApplicationTest {

    private TransactionManagerService transactionManagerService;
    private SavingsManagerService savingsManagerService;
    private String fromAccountId;
    private String toAccountId;

    @Before
    public void setUp() {
        transactionManagerService = new TransactionManagerService();
        savingsManagerService = new SavingsManagerService();

        clearAccountsRepository();

        AccountModel fromAccount = new CheckingAccountModel("fromAccount", new MoneyModel(1000, CurrencyType.EUR), new ArrayList<>(),
                card1);
        AccountModel toAccount = new CheckingAccountModel("toAccount", new MoneyModel(500, CurrencyType.EUR), new ArrayList<>(),
                card2);

        fromAccountId = fromAccount.getId();
        toAccountId = toAccount.getId();

        AccountsRepository.INSTANCE.add(fromAccount.getId(), fromAccount);
        AccountsRepository.INSTANCE.add(toAccount.getId(), toAccount);
    }

    private void clearAccountsRepository() {
        for (AccountModel account : AccountsRepository.INSTANCE.getAll()) {
            AccountsRepository.INSTANCE.remove(account.getId());
        }
    }

    @Test
    public void successfulTransaction() {
        MoneyModel transferAmount = new MoneyModel(100, CurrencyType.EUR);
        TransactionModel transaction = transactionManagerService.transfer(fromAccountId, toAccountId, transferAmount);

        assertNotNull(transaction);
        assertEquals(900, AccountsRepository.INSTANCE.get(fromAccountId).getBalance().getAmount(), 0);
        assertEquals(600, AccountsRepository.INSTANCE.get(toAccountId).getBalance().getAmount(), 0);
    }

    @Test(expected = RuntimeException.class)
    public void transferBetweenUnsupportedAccounts() {
        AccountModel savingsAccount = new SavingsAccountModel("ROBMSG100001",
                new MoneyModel(1000, CurrencyType.RON),
                new ArrayList<>(),
                InterestRate.THREE_MONTH_ACCOUNT,
                CapitalizationFrequency.MONTHLY,
                LocalDate.now());
        AccountModel anotherSavingsAccount = new SavingsAccountModel("ROBMSG100002",
                new MoneyModel(2000, CurrencyType.EUR),
                new ArrayList<>(),
                InterestRate.SIX_MONTH_ACCOUNT,
                CapitalizationFrequency.QUARTERLY,
                LocalDate.now());

        AccountsRepository.INSTANCE.add(savingsAccount.getId(), savingsAccount);
        AccountsRepository.INSTANCE.add(anotherSavingsAccount.getId(), anotherSavingsAccount);

        transactionManagerService.transfer(savingsAccount.getId(), anotherSavingsAccount.getId(), new MoneyModel(100, CurrencyType.EUR));
    }

    @Test(expected = RuntimeException.class)
    public void transferWithInsufficientFunds() {
        MoneyModel transferAmount = new MoneyModel(2000, CurrencyType.EUR);
        transactionManagerService.transfer(fromAccountId, toAccountId, transferAmount);
    }

    @Test(expected = RuntimeException.class)
    public void transferNegativeAmount() {
        MoneyModel negativeAmount = new MoneyModel(-100, CurrencyType.EUR);
        transactionManagerService.transfer(fromAccountId, toAccountId, negativeAmount);
    }

    @Test(expected = RuntimeException.class)
    public void transferToSameAccount() {
        transactionManagerService.transfer(fromAccountId, fromAccountId, new MoneyModel(10, CurrencyType.EUR));
    }

    @Test
    public void transferWithDifferentCurrencies() {
        AccountModel toAccountRON = new CheckingAccountModel("toAccount", new MoneyModel(500, CurrencyType.RON), new ArrayList<>(),
                card3);

        AccountsRepository.INSTANCE.add(toAccountRON.getId(), toAccountRON);

        MoneyModel transferAmount = new MoneyModel(10, CurrencyType.EUR);

        double expectedAmountInRON = 10 * 4.98;

        transactionManagerService.transfer(fromAccountId, toAccountRON.getId(), transferAmount);

        double updatedBalanceRON = AccountsRepository.INSTANCE.get(toAccountRON.getId()).getBalance().getAmount();
        assertEquals("The balance after conversion and transfer should match the expected value in RON.",
                500 + expectedAmountInRON, updatedBalanceRON, 0.01);

        AccountsRepository.INSTANCE.remove(toAccountRON.getId());
    }

    @Test
    public void successfulWithdrawal() {
        double initialAmount = AccountsRepository.INSTANCE.get(fromAccountId).getBalance().getAmount();
        double withdrawAmount = 200.0;

        TransactionModel transaction = transactionManagerService.withdraw(fromAccountId, new MoneyModel(withdrawAmount, CurrencyType.EUR));

        assertNotNull(transaction);
        assertEquals(initialAmount - withdrawAmount, AccountsRepository.INSTANCE.get(fromAccountId).getBalance().getAmount(), 0.0);
    }

    @Test(expected = RuntimeException.class)
    public void withdrawalFromNonExistentAccount() {
        transactionManagerService.withdraw("nonExistentAccountId", new MoneyModel(100, CurrencyType.EUR));
    }

    @Test(expected = RuntimeException.class)
    public void withdrawalLeadingToNegativeBalance() {
        transactionManagerService.withdraw(fromAccountId, new MoneyModel(2000, CurrencyType.EUR));
    }

    @Test(expected = RuntimeException.class)
    public void withdrawNegativeOrZeroAmount() {
        transactionManagerService.withdraw(fromAccountId, new MoneyModel(-100, CurrencyType.EUR));
    }

    @Test(expected = RuntimeException.class)
    public void withdrawWithDifferentCurrency() {
        transactionManagerService.withdraw(fromAccountId, new MoneyModel(10, CurrencyType.RON));
    }

    @Test
    public void applyMonthlyInterestToSavingsAccount() {
        AccountsRepository.INSTANCE.add(savingsAccountA.getId(), savingsAccountA);

        double expectedInterest = savingsAccountA.getBalance().getAmount() * savingsAccountA.getInterest();
        double expectedBalance = savingsAccountA.getBalance().getAmount() + expectedInterest;

        savingsManagerService.passTime();

        AccountModel updatedAccount = AccountsRepository.INSTANCE.get(savingsAccountA.getId());
        assertEquals("The balance should be updated with monthly interest",
                expectedBalance,
                updatedAccount.getBalance().getAmount(),
                0.01);
    }

    @Test
    public void applyQuarterlyInterestToSavingsAccount() {
        AccountsRepository.INSTANCE.add(savingsAccountB.getId(), savingsAccountB);

        double expectedInterest = savingsAccountB.getBalance().getAmount() * savingsAccountB.getInterest();
        double expectedBalance = savingsAccountB.getBalance().getAmount() + expectedInterest;

        savingsManagerService.passTime();

        AccountModel updatedAccount = AccountsRepository.INSTANCE.get(savingsAccountB.getId());
        assertEquals("The balance should be updated with monthly interest",
                expectedBalance,
                updatedAccount.getBalance().getAmount(),
                0.01);
    }

}
