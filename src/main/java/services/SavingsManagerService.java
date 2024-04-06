package main.java.services;

import main.java.domain.CapitalizationFrequency;
import main.java.domain.SavingsAccountModel;
import main.java.repository.AccountsRepository;

import java.time.LocalDate;
import java.util.List;

public class SavingsManagerService {
    private LocalDate systemDate = LocalDate.now();
    private LocalDate systemDateQuarterly = LocalDate.now();

    public void passTime() {
        List<SavingsAccountModel> savingAccounts = AccountsRepository.INSTANCE.getAll().stream()
                .filter(account -> account instanceof SavingsAccountModel)
                .map(account -> (SavingsAccountModel) account).toList();

        LocalDate nextSystemDate = systemDate.plusMonths(1);
        LocalDate nextSystemDateQuarterly = systemDateQuarterly.plusMonths(3);

        savingAccounts.forEach(savingAccount -> {
            if (savingAccount.getInterestFrequency() == CapitalizationFrequency.MONTHLY) {
                addMonthlyInterest(savingAccount, nextSystemDate);
            } else if (savingAccount.getInterestFrequency() == CapitalizationFrequency.QUARTERLY) {
                addQuarterlyInterest(savingAccount, nextSystemDateQuarterly);
            }
        });

        systemDate = nextSystemDate;
        systemDateQuarterly =  nextSystemDateQuarterly;
    }

    private void addMonthlyInterest(SavingsAccountModel savingAccount, LocalDate currentInterestMonth) {
        LocalDate nextInterestDateForAccount = savingAccount.getLastInterestAppliedDate().plusMonths(1);

        if (isSameMonthAndYear(currentInterestMonth, nextInterestDateForAccount)) {
            addInterest(savingAccount);
            savingAccount.setLastInterestAppliedDate(currentInterestMonth);
        }
    }

    private void addQuarterlyInterest(SavingsAccountModel savingAccount, LocalDate currentInterestMonth) {
        LocalDate nextInterestDateForAccount = savingAccount.getLastInterestAppliedDate().plusMonths(3);

        if (isSameMonthAndYear(currentInterestMonth, nextInterestDateForAccount)) {
            addInterest(savingAccount);
            savingAccount.setLastInterestAppliedDate(currentInterestMonth);
        }
    }

    private void addInterest(SavingsAccountModel savingAccount) {
        double interest = savingAccount.getBalance().getAmount() * savingAccount.getInterest();
        savingAccount.getBalance().setAmount(savingAccount.getBalance().getAmount() + interest);
    }

    private boolean isSameMonthAndYear(LocalDate date1, LocalDate date2) {
        return date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear();
    }
}


