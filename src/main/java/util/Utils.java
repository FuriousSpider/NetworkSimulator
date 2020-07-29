package util;

import java.util.Random;

public class Utils {
    public static String generateMacAddress() {
        Random random = new Random();
        StringBuilder macAddress = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            macAddress.append(Integer.toHexString(Math.abs(random.nextInt())), 0, 2).append(":");
        }
        macAddress.setLength(macAddress.length() - 1);

        return macAddress.toString();
    }
}
