package main.java.repository;

import main.java.domain.AccountModel;

public class AccountsRepository {
    public static final InMemoryDatabase<AccountModel> INSTANCE = new InMemoryDatabase<>();
}
