package main.java.seed;

import main.java.repository.AccountsRepository;

public class SeedInitializer {

    public static void seedData() {
        System.out.println("[Seeder] -------------Seeding data----------------\n");
        AccountsRepository.INSTANCE.add(AccountsSeedData.savingsAccountA.getId(), AccountsSeedData.savingsAccountA);
        AccountsRepository.INSTANCE.add(AccountsSeedData.savingsAccountB.getId(), AccountsSeedData.savingsAccountB);
        AccountsRepository.INSTANCE.add(AccountsSeedData.checkingAccountA.getId(), AccountsSeedData.checkingAccountA);
        AccountsRepository.INSTANCE.add(AccountsSeedData.checkingAccountB.getId(), AccountsSeedData.checkingAccountB);
        AccountsRepository.INSTANCE.add(AccountsSeedData.checkingAccountC.getId(), AccountsSeedData.checkingAccountC);
        AccountsRepository.INSTANCE.add(AccountsSeedData.checkingAccountD.getId(), AccountsSeedData.checkingAccountD);
    }
}
