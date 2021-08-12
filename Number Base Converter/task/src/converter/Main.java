package converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Scanner;

public class Main {

    private static final int BIG_DECIMAL_SCALE = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter two numbers in format: {source base} {target base} (To quit type /exit)");
            try (Scanner request = new Scanner(scanner.nextLine())) {
                if (request.hasNextInt()) {
                    final int sourceBase = request.nextInt();
                    final int targetBase = request.nextInt();
                    while (true) {
                        System.out
                                .printf("Enter number in base %d to convert to base %d (To go back type /back)%n", sourceBase, targetBase);
                        String number = scanner.nextLine();
                        if ("/back".equals(number)) {
                            break;
                        }
                        System.out
                                .printf("Conversion result: %s%n", convertFromAnyBaseToAnyBase(number, sourceBase, targetBase));
                    }
                } else {
                    return;
                }
            }
        }
    }

    public static String convertFromAnyBaseToAnyBase(String number, int sourceBase, int targetBase) {
        return convertFromDecimal(convertToDecimal(number, sourceBase), targetBase, number.contains("."));
    }

    public static String convertFromDecimal(BigDecimal decimal, int base, boolean shouldIncludeFractionalPart) {
        BigInteger bigIntegerBase = BigInteger.valueOf(base);
        BigInteger integerPart = decimal.toBigInteger();
        StringBuilder number = new StringBuilder();
        while (integerPart.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divideAndRemainder = integerPart.divideAndRemainder(bigIntegerBase);
            number.append(Character.forDigit(divideAndRemainder[1].intValue(), Character.MAX_RADIX));
            integerPart = divideAndRemainder[0];
        }
        number.reverse();
        if (shouldIncludeFractionalPart) {
            number.append('.');
            BigDecimal bigDecimalBase = BigDecimal.valueOf(base);
            for (int i = 0; i < 5; i++) {
                decimal = decimal.remainder(BigDecimal.ONE).multiply(bigDecimalBase);
                number.append(Character.forDigit(decimal.intValue(), Character.MAX_RADIX));
            }
        }
        return number.toString();
    }

    public static BigDecimal convertToDecimal(String number, int base) {
        BigDecimal bigDecimalBase = BigDecimal.valueOf(base);
        BigDecimal wage = BigDecimal.ONE;
        BigDecimal decimal = BigDecimal.ZERO;
        String[] split = number.split("\\.");
        char[] digitsOfIntegerPart = split[0].toCharArray();
        for (int i = digitsOfIntegerPart.length - 1; i >= 0; i--) {
            decimal = decimal.add(wage.multiply(BigDecimal.valueOf(Character.digit(digitsOfIntegerPart[i], base))));
            wage = wage.multiply(bigDecimalBase);
        }
        if (split.length > 1) {
            wage = BigDecimal.ONE.divide(bigDecimalBase, BIG_DECIMAL_SCALE, RoundingMode.HALF_EVEN);
            char[] digitsOfFractionalPart = split[1].toCharArray();
            for (char digit : digitsOfFractionalPart) {
                decimal = decimal.add(wage.multiply(BigDecimal.valueOf(Character.digit(digit, base))));
                wage = wage.divide(bigDecimalBase, BIG_DECIMAL_SCALE, RoundingMode.HALF_EVEN);
            }
        }
        return decimal;
    }

}
