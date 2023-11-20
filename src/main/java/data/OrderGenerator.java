package data;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static OrderData getRandomOrder() {
        String firstName = RandomStringUtils.randomAlphabetic(10);
        String lastName = RandomStringUtils.randomAlphabetic(10);
        String address = RandomStringUtils.randomAlphabetic(20);
        String metroStation = String.valueOf(new Random().nextInt(215));
        String phone = "+7 " + RandomStringUtils.randomNumeric(10);
        int rentTime = new Random().nextInt(10) + 1; // Длительность аренды от 1 до 10 дней

        LocalDate deliveryDate = LocalDate.now().plusDays(new Random().nextInt(30)); // Доставка в течение 30 дней от текущей даты
        String deliveryDateStr = deliveryDate.format(DATE_FORMATTER);

        String comment = RandomStringUtils.randomAlphabetic(30);

        return new OrderData(firstName, lastName, address, metroStation, phone, rentTime, deliveryDateStr, comment, new ArrayList<>());
    }

    public static OrderData getRandomOrderWithColor(String color) {
        OrderData order = getRandomOrder();
        List<String> colors = new ArrayList<>();
        colors.add(color);
        order.setColor(colors);
        return order;
    }

    public static OrderData getRandomOrderWithColors(String color1, String color2) {
        OrderData order = getRandomOrder();
        List<String> colors = new ArrayList<>();
        colors.add(color1);
        colors.add(color2);
        order.setColor(colors);
        return order;
    }
}
