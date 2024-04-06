package main.java;

import main.java.domain.CurrencyType;
import main.java.domain.MoneyModel;
import main.java.domain.TransactionModel;
import main.java.seed.SeedInitializer;
import main.java.services.SavingsManagerService;
import main.java.services.TransactionManagerService;

import static main.java.seed.AccountsSeedData.*;

public class BankingApplication {

    public static void main(String[] args) {
        System.out.println("[SYSTEM] Initialize Application \n");
        SeedInitializer.seedData();
        System.out.println("[SYSTEM] Running Application \n\n");

        // TRANSACTION MANAGER FUNCTIONALITY

        TransactionManagerService transactionManagerServiceInstance = new TransactionManagerService();
        SavingsManagerService savingsManagerServiceInstance = new SavingsManagerService();

        System.out.println("[Transaction Manager] 1. " + transactionManagerServiceInstance.checkFunds(checkingAccountA.getId()));
        System.out.println("[Transaction Manager] 2. " + transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()));

        TransactionModel transaction1 = transactionManagerServiceInstance.transfer(
                checkingAccountA.getId(),
                checkingAccountB.getId(),
                new MoneyModel(50, CurrencyType.RON)
        );

        System.out.println("[Transaction Manager] 3. " + transaction1);
        System.out.println("[Transaction Manager] 4. " + transactionManagerServiceInstance.checkFunds(checkingAccountA.getId()));
        System.out.println("[Transaction Manager] 5. " + transactionManagerServiceInstance.checkFunds(checkingAccountB.getId()));

        System.out.println("[Transaction Manager] 6. " + transactionManagerServiceInstance.checkFunds(checkingAccountC.getId()));

        System.out.println("[Transaction Manager] 7. " +
                 transactionManagerServiceInstance.withdraw(
                         checkingAccountC.getId(),
                         new MoneyModel(5, CurrencyType.EUR)
                 )
        );

        System.out.println("[Transaction Manager] 8. " + transactionManagerServiceInstance.checkFunds(checkingAccountC.getId()));

        System.out.println("\n------------------------------------\n");

        // SAVINGS MANAGER FUNCTIONALITY

        // MONTHLY AND QUARTERLY INTERESTS

        System.out.println("[Saving Manager] 1. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Saving Manager] 2. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));

        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 3. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Saving Manager] 4. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));

        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 5. " + transactionManagerServiceInstance.checkFunds(savingsAccountA.getId()));
        System.out.println("[Saving Manager] 6. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));

        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 7. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));

        // QUARTERLY INTEREST

        savingsManagerServiceInstance.passTime();
        savingsManagerServiceInstance.passTime();
        savingsManagerServiceInstance.passTime();
        System.out.println("[Saving Manager] 8. " + transactionManagerServiceInstance.checkFunds(savingsAccountB.getId()));

        System.out.println("\n[SYSTEM] Application closed\n");
    }
}