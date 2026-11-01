public class Main {
    public static void main(String[] args) {
        BankAccount alice = new BankAccount("ACC-001", "Alice", Currency.RUB);
        BankAccount bob   = new BankAccount("ACC-002", "Bob",   Currency.USD);

        System.out.println("Пополнение");
        alice.deposit(new Balance(100_000, Currency.RUB)); // 1000.00 ₽
        bob.deposit(new Balance(5_000, Currency.USD));     // 50.00 $
        System.out.println(alice);
        System.out.println(bob);

        System.out.println("\n Перевод 500 ₽ с Alice на Bob (USgit --versiongit --versiongit --versionD) ");
        alice.transferTo(bob, new Balance(50_000, Currency.RUB));
        System.out.println(alice);
        System.out.println(bob);

        System.out.println("\n Лимит на вывод");
        alice.setWithdrawLimit(10_000); // 100 ₽
        try {
            alice.withdraw(new Balance(20_000, Currency.RUB));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\nНедостаточно средств");
        try {
            bob.withdraw(new Balance(1_000_000, Currency.RUB));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\nЗаморозка");
        bob.freeze();
        try {
            bob.deposit(new Balance(100, Currency.USD));
        } catch (IllegalStateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\n Отрицательная сумма");
        try {
            alice.deposit(new Balance(-1, Currency.RUB));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("\nКонвертация");
        Balance rub = new Balance(96_000, Currency.RUB); // 960 ₽
        System.out.println(rub + " = " + rub.convertTo(Currency.USD));
    }
}