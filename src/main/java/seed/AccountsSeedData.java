package main.java.seed;

import main.java.domain.CapitalizationFrequency;
import main.java.domain.CheckingAccountModel;
import main.java.domain.CurrencyType;
import main.java.domain.InterestRate;
import main.java.domain.MoneyModel;
import main.java.domain.SavingsAccountModel;

import java.time.LocalDate;
import java.util.ArrayList;

import static main.java.seed.CardsSeedData.card1;
import static main.java.seed.CardsSeedData.card2;
import static main.java.seed.CardsSeedData.card3;
import static main.java.seed.CardsSeedData.card4;

public class AccountsSeedData {

    public static final SavingsAccountModel savingsAccountA = new SavingsAccountModel(
            "ROBMSG100001",
            new MoneyModel(1000, CurrencyType.RON),
            new ArrayList<>(),
            InterestRate.THREE_MONTH_ACCOUNT,
            CapitalizationFrequency.MONTHLY,
            LocalDate.now()
    );

    public static final SavingsAccountModel savingsAccountB = new SavingsAccountModel(
            "ROBMSG100002",
            new MoneyModel(2000, CurrencyType.EUR),
            new ArrayList<>(),
            InterestRate.SIX_MONTH_ACCOUNT,
            CapitalizationFrequency.QUARTERLY,
            LocalDate.now()
    );

    public static final CheckingAccountModel checkingAccountA = new CheckingAccountModel(
            "ROBMSG200001",
            new MoneyModel(100, CurrencyType.RON),
            new ArrayList<>(),
            card1
    );

    public static final CheckingAccountModel checkingAccountB = new CheckingAccountModel(
            "ROBMSG200002",
            new MoneyModel(300, CurrencyType.RON),
            new ArrayList<>(),
            card2
    );

    public static final CheckingAccountModel checkingAccountC = new CheckingAccountModel(
            "ROBMSG200003",
            new MoneyModel(10, CurrencyType.EUR),
            new ArrayList<>(),
            card3
    );

    public static final CheckingAccountModel checkingAccountD = new CheckingAccountModel(
            "ROBMSG200004",
            new MoneyModel(1000, CurrencyType.EUR),
            new ArrayList<>(),
            card4
    );
}
