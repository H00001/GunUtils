package top.gunplan.utils;

import java.util.regex.Pattern;

public class NumberUtil {
    public static String NUM_16_STA = "0x";

    public static boolean isNumber(String str) {
        if (str.startsWith(NUM_16_STA)) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
