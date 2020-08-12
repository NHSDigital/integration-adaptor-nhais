package uk.nhs.digital.nhsconnect.nhais.pcrm;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class NhsNumberGenerator {

    @SneakyThrows
    public static String generateUniqueNhsNumber() {
        String nhsNumber;
        do {
            nhsNumber = doGenerateNhsNumber();
            if (nhsNumber == null) {
                Thread.sleep(100L); // don't retry within 100ms you'll get the same exact number
            }
        } while (nhsNumber == null);
        return nhsNumber;
    }

    // See: https://en.wikipedia.org/wiki/NHS_number
    private static String doGenerateNhsNumber() {
        long number = System.currentTimeMillis();
        number = number / 100L; // discard two least significant digits to repeat every ~1157 days
        int[] digits = new int[10];
        int checksum = 0;
        for (int i = 0; i < 9; i++) {
            digits[i] = (int) (number % 10L);
            checksum += (10 - i) * digits[i];
            number = number / 10;
        }
        checksum = 11 - (checksum % 11);
        if (checksum == 10) {
            LOGGER.debug("Checksum of generated NHS Number is 10. This random NHS Number is not valid.");
            return null;
        }
        digits[9] = checksum;
        for (int i = 0; i < 10; i++) {
            if (digits[i] < 0 || digits[i] > 9) {
                LOGGER.warn("Generated NHS Number is invalid - digit is < 0 or > 9: digit[" + i + "] = " + digits[i]);
                return null;
            }
        }
        String nhsNumber = Arrays.stream(digits).mapToObj(Integer::toString).collect(Collectors.joining());
        if (nhsNumber.length() != 10) {
            LOGGER.warn("Generated NHS Number is invalid - not 10 digits long: " + nhsNumber);
            return null;
        }
        LOGGER.debug("Generated NHS Number: {}", nhsNumber);
        return nhsNumber;
    }

    public static void main(String[] args) {
        System.out.println(generateUniqueNhsNumber());
    }

}
