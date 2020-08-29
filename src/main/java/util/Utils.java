package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    private final static List<String> macAddressList = new ArrayList<>();

    public static String generateMacAddress() {
        Random random = new Random();
        StringBuilder macAddress;

        do {
            macAddress = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                macAddress.append(Integer.toHexString(Math.abs(random.nextInt())), 0, 2).append(":");
            }
            macAddress.setLength(macAddress.length() - 1);

        } while (macAddressList.contains(macAddress.toString()));

        macAddressList.add(macAddress.toString());

        return macAddress.toString();
    }

    public static String getNetworkAddressFromIp(String ipAddress) {
        List<StringBuilder> ipAddressList = new ArrayList<>();
        StringBuilder ipAddressString = new StringBuilder();
        StringBuilder maskString = new StringBuilder();
        StringBuilder binaryResult = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String str : ipAddress.split("[./]")) {
            StringBuilder octet = new StringBuilder(Integer.toBinaryString(Integer.parseInt(str)));
            while (octet.length() < 8) {
                octet.insert(0, "0");
            }
            ipAddressList.add(octet);
        }
        int mask = Integer.parseInt(ipAddress.split("[/]")[1]);
        maskString.append("1".repeat(Math.max(0, mask)));
        maskString.append("0".repeat(Math.max(0, 32 - mask)));
        for (int i = 0; i < 4; i++) {
            ipAddressString.append(ipAddressList.get(i));
        }

        for (int i = 0; i < 32; i++) {
            if (ipAddressString.charAt(i) == maskString.charAt(i)) {
                binaryResult.append(ipAddressString.charAt(i));
            } else {
                binaryResult.append("0");
            }
        }

        for (int i = 0; i < 4; i++) {
            result.append(Integer.parseInt(binaryResult.substring(i * 8, (i + 1) * 8), 2)).append(".");
        }
        result.setLength(result.length() - 1);

        return result.toString();
    }

    public static boolean belongToTheSameNetwork(String ipAddress1, String ipAddress2) {
        return getNetworkAddressFromIp(ipAddress1).equals(getNetworkAddressFromIp(ipAddress2));
    }
}
