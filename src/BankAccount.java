import java.util.Objects;

public final class BankAccount {

    private final String accountNumber;
    private final String ownerName;
    private Balance balance;
    /** Лимит на вывод в копейках (0 — без лимита). */
    private long withdrawLimitKopecks;
    private boolean frozen;

    public BankAccount(String accountNumber, String ownerName, Currency currency) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Номер счёта не может быть пустым");
        }
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Имя владельца не может быть пустым");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = new Balance(0, Objects.requireNonNull(currency, "Валюта не может быть null"));
        this.withdrawLimitKopecks = 0;
        this.frozen = false;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName()     { return ownerName; }
    public Balance getBalance()      { return balance; }
    public boolean isFrozen()        { return frozen; }

    public void setWithdrawLimit(long limitKopecks) {
        if (limitKopecks < 0) {
            throw new IllegalArgumentException("Лимит не может быть отрицательным");
        }
        this.withdrawLimitKopecks = limitKopecks;
    }

    public void freeze()   { this.frozen = true; }
    public void unfreeze() { this.frozen = false; }

    private void ensureNotFrozen() {
        if (frozen) {
            throw new IllegalStateException("Счёт " + accountNumber + " заморожен");
        }
    }

    public void deposit(Balance amount) {
        Objects.requireNonNull(amount, "Сумма не может быть null");
        ensureNotFrozen();
        this.balance = this.balance.add(amount.convertTo(this.balance.getCurrency()));
    }

    public void withdraw(Balance amount) {
        Objects.requireNonNull(amount, "Сумма не может быть null");
        ensureNotFrozen();
        Balance inAccountCurrency = amount.convertTo(this.balance.getCurrency());
        if (withdrawLimitKopecks > 0
                && inAccountCurrency.getAmountInKopecks() > withdrawLimitKopecks) {
            throw new IllegalArgumentException(
                    "Превышен лимит на вывод: " + inAccountCurrency.getAmountInKopecks()
                            + " > " + withdrawLimitKopecks);
        }
        this.balance = this.balance.subtract(inAccountCurrency);
    }

    /** Перевод на другой счёт с учётом лимита списания и валют получателя. */
    public void transferTo(BankAccount target, Balance amount) {
        Objects.requireNonNull(target, "Счёт-получатель не может быть null");
        Objects.requireNonNull(amount, "Сумма не может быть null");
        if (target == this) {
            throw new IllegalArgumentException("Нельзя переводить на тот же счёт");
        }
        ensureNotFrozen();
        target.ensureNotFrozen();

        Balance inSourceCurrency = amount.convertTo(this.balance.getCurrency());
        if (withdrawLimitKopecks > 0
                && inSourceCurrency.getAmountInKopecks() > withdrawLimitKopecks) {
            throw new IllegalArgumentException(
                    "Превышен лимит на вывод при переводе: "
                            + inSourceCurrency.getAmountInKopecks() + " > " + withdrawLimitKopecks);
        }
        this.balance = this.balance.subtract(inSourceCurrency);

        Balance credited = inSourceCurrency.convertTo(target.balance.getCurrency());
        target.balance = target.balance.add(credited);
    }

    @Override
    public String toString() {
        return "BankAccount[" + accountNumber + ", " + ownerName
                + ", " + balance + (frozen ? ", ЗАМОРОЖЕН" : "") + "]";
    }
}