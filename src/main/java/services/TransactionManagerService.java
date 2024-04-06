package main.java.services;

import main.java.domain.*;
import main.java.repository.AccountsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionManagerService {

    public TransactionModel transfer(String fromAccountId, String toAccountId, MoneyModel value) {
        AccountModel fromAccount = AccountsRepository.INSTANCE.get(fromAccountId);
        AccountModel toAccount = AccountsRepository.INSTANCE.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Specified account does not exist");
        }

        if(fromAccount.getAccountType() == AccountType.SAVINGS && (toAccount.getAccountType() == AccountType.CHECKING || toAccount.getAccountType() == AccountType.SAVINGS)) {
            throw new RuntimeException("You cannot perform the transfer functionality between the following types of accounts: " +
                    "Savings Accounts => Checking Accounts, Savings Accounts => Savings Accounts");
        }

        if(fromAccount.getBalance().getAmount() < value.getAmount()) {
            throw new RuntimeException("The result of a transaction must not lead to negative account balance.");
        }

        if(value.getAmount() < 0) {
            throw new RuntimeException("The transfer amount should be greater than zero.");
        }

        if(fromAccount == toAccount) {
            throw new RuntimeException("A transfer from an account to the same account is not allowed.");
        }

        if(fromAccount.getBalance().getCurrency() != toAccount.getBalance().getCurrency()) {
            value = convertCurrency(value, fromAccount.getBalance().getCurrency(), toAccount.getBalance().getCurrency());
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                fromAccountId,
                toAccountId,
                value,
                LocalDate.now()
        );

        fromAccount.getBalance().setAmount(fromAccount.getBalance().getAmount() - value.getAmount());
        fromAccount.getTransactions().add(transaction);

        toAccount.getBalance().setAmount(toAccount.getBalance().getAmount() + value.getAmount());
        toAccount.getTransactions().add(transaction);

        return transaction;
    }

    private MoneyModel convertCurrency(MoneyModel amount, CurrencyType fromCurrency, CurrencyType toCurrency) {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        double convertedAmount = amount.getAmount() * exchangeRate;
        return new MoneyModel(convertedAmount, toCurrency);
    }

    private double getExchangeRate(CurrencyType fromCurrency, CurrencyType toCurrency) {
        if (fromCurrency == CurrencyType.EUR && toCurrency == CurrencyType.RON) {
            return 4.98;
        } else if (fromCurrency == CurrencyType.RON && toCurrency == CurrencyType.EUR) {
            return 0.20;
        }
        throw new IllegalArgumentException("Unsupported currency conversion");
    }

    public TransactionModel withdraw(String accountId, MoneyModel amount) {
        AccountModel account = AccountsRepository.INSTANCE.get(accountId);

        if(account == null) {
            throw new RuntimeException("Specified account does not exist.");
        }

        if(account.getBalance().getAmount() < amount.getAmount()) {
            throw new RuntimeException("The result of a withdrawal must not lead to negative account balance.");
        }

        if(amount.getAmount() < 0) {
            throw new RuntimeException("The withdraw amount should be greater than zero.");
        }

        if(account.getBalance().getCurrency() != amount.getCurrency()) {
            throw new RuntimeException("The withdrawal currency can't be different from the account's primary currency.");
        }

        TransactionModel transaction = new TransactionModel(
                UUID.randomUUID(),
                accountId,
                accountId,
                amount,
                LocalDate.now()
        );

        account.getBalance().setAmount(account.getBalance().getAmount() - amount.getAmount());

        return transaction;
    }

    public MoneyModel checkFunds(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return AccountsRepository.INSTANCE.get(accountId).getBalance();
    }

    public List<TransactionModel> retrieveTransactions(String accountId) {
        if (!AccountsRepository.INSTANCE.exist(accountId)) {
            throw new RuntimeException("Specified account does not exist");
        }
        return new ArrayList<>(AccountsRepository.INSTANCE.get(accountId).getTransactions());
    }

}

