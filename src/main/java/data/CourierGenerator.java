package data;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierGenerator {
    public static CourierData getRandomCourier() {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);

        return new CourierData(login, password, firstName);
    }
    public static CourierData getRandomCourierWithoutLogin() {
        String password = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);

        return new CourierData(null, password, firstName);
    }

    public static CourierData getRandomCourierWithoutPassword() {
        String login = RandomStringUtils.randomAlphabetic(10);
        String firstName = RandomStringUtils.randomAlphabetic(10);

        return new CourierData(login, null, firstName);
    }

    public static CourierData getRandomCourierWithoutFirstName() {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        return new CourierData(login, password, null);
    }
}