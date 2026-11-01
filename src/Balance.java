import java.util.Objects;

public final class Balance {

    private final long amountInKopecks;
    private final Currency currency;

    public Balance(long amountInKopecks, Currency currency) {
        if (amountInKopecks < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной: " + amountInKopecks);
        }
        this.amountInKopecks = amountInKopecks;
        this.currency = Objects.requireNonNull(currency, "Валюта не может быть null");
    }

    public long getAmountInKopecks() { return amountInKopecks; }
    public Currency getCurrency()    { return currency; }

    /** Прибавление. Отрицательные суммы запрещены. */
    public Balance add(Balance other) {
        Objects.requireNonNull(other, "Другой баланс не может быть null");
        if (other.amountInKopecks < 0) {
            throw new IllegalArgumentException("Нельзя прибавлять отрицательную сумму");
        }
        if (this.currency != other.currency) {
            // если валюты разные — приводим other к валюте this
            other = other.convertTo(this.currency);
        }
        return new Balance(
                Math.addExact(this.amountInKopecks, other.amountInKopecks),
                this.currency
        );
    }

    /** Вычитание. Нельзя вычесть больше, чем есть, и нельзя вычесть отрицательное. */
    public Balance subtract(Balance other) {
        Objects.requireNonNull(other, "Другой баланс не может быть null");
        if (other.amountInKopecks < 0) {
            throw new IllegalArgumentException("Нельзя вычитать отрицательную сумму");
        }
        if (this.currency != other.currency) {
            other = other.convertTo(this.currency);
        }
        if (other.amountInKopecks > this.amountInKopecks) {
            throw new IllegalArgumentException(
                    "Недостаточно средств: есть " + this.amountInKopecks
                            + ", требуется " + other.amountInKopecks);
        }
        return new Balance(this.amountInKopecks - other.amountInKopecks, this.currency);
    }

    /** Конвертация по курсу из Currency (цены заданы в рублёвых копейках). */
    public Balance convertTo(Currency target) {
        Objects.requireNonNull(target, "Целевая валюта не может быть null");
        if (target == this.currency) {
            return this;
        }
        long inRubKopecks = Math.multiplyExact(
                this.amountInKopecks, this.currency.getPriceInRublesKopecks());
        long result = inRubKopecks / target.getPriceInRublesKopecks();
        return new Balance(result, target);
    }

    @Override
    public String toString() {
        return (amountInKopecks / 100) + "." + String.format("%02d", amountInKopecks % 100)
                + " " + currency.getSymbol();
    }
}