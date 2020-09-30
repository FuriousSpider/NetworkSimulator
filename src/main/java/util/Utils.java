package util;

import simulator.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class Utils {
    private final static List<String> macAddressList = new ArrayList<>();

    public static String generateMacAddress() {
        Random random = new Random();
        StringBuilder macAddress;

        do {
            macAddress = new StringBuilder();

            for (int i = 0; i < 6; i++) {
                String singlePart = Integer.toHexString(Math.abs(random.nextInt())).substring(0, 2);
                if (singlePart.contains("ff")) {
                    i--;
                } else {
                    macAddress.append(singlePart).append(":");
                }
            }
            macAddress.setLength(macAddress.length() - 1);

        } while (macAddressList.contains(macAddress.toString()));

        macAddressList.add(macAddress.toString());

        return macAddress.toString();
    }

    public static String getNetworkAddressFromIp(String ipAddress) {
        String ipAddressWithMask = ipAddress;
        if (Utils.isIpAddressWithoutMask(ipAddressWithMask)) {
            ipAddressWithMask = Manager.getInstance().getIpWithMaskByIp(ipAddressWithMask);
        }
        List<StringBuilder> ipAddressList = new ArrayList<>();
        StringBuilder ipAddressString = new StringBuilder();
        StringBuilder maskString = new StringBuilder();
        StringBuilder binaryResult = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String str : ipAddressWithMask.split("[./]")) {
            StringBuilder octet = new StringBuilder(Integer.toBinaryString(Integer.parseInt(str)));
            while (octet.length() < 8) {
                octet.insert(0, "0");
            }
            ipAddressList.add(octet);
        }
        int mask = Integer.parseInt(ipAddressWithMask.split("[/]")[1]);
        for (int i = 0; i < mask; i++) {
            maskString.append("1");
        }
        for (int i = mask; i < 32; i++) {
            maskString.append("0");
        }
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

    public static boolean isNetworkAddress(String ipAddress) {
        Pattern pattern = Pattern.compile(Values.REGEX_IP_ADDRESS_WITH_MASK);
        if (pattern.matcher(ipAddress).matches()) {
            return ipAddress.contains(getNetworkAddressFromIp(ipAddress));
        } else {
            return false;
        }
    }

    public static boolean isHostAddress(String ipAddress) {
        Pattern pattern = Pattern.compile(Values.REGEX_IP_ADDRESS_WITH_MASK);
        if (pattern.matcher(ipAddress).matches()) {
            return !ipAddress.contains(getNetworkAddressFromIp(ipAddress));
        } else {
            return false;
        }
    }

    public static boolean isIpAddressWithoutMask(String ipAddress) {
        Pattern pattern = Pattern.compile(Values.REGEX_IP_ADDRESS_WITHOUT_MASK);
        return pattern.matcher(ipAddress).matches();
    }

    public static boolean isVLanIdValid(String vLanId) {
        try {
            int vLan = Integer.parseInt(vLanId);
            return vLan >= Values.PORT_VLAN_ID_MIN_VALUE && vLan <= Values.PORT_VLAN_ID_MAX_VALUE;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getIpAddressWithoutMask(String ipAddress) {
        return ipAddress.split("/")[0];
    }
}
