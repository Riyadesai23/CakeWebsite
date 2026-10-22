package com.example.ecommerce.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public final class OrderNumberGenerator {

    private static final Random RANDOM = new Random();

    private OrderNumberGenerator() {
    }

    public static String generateOrderNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        int randomNum = 1000 + RANDOM.nextInt(9000);
        return "ORD-" + dateStr + "-" + randomNum;
    }
}
