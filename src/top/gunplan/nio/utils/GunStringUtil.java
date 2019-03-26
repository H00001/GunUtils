package top.gunplan.nio.utils;

/**
 * about this class, we do not suggest you explore
 */
public final class GunStringUtil {
    public static String removeLastUrl(String in) {
        int fg = 0;
        for (int i = in.length() - 1; i >= 0; i--) {
            if (in.charAt(i) == '/') {
                fg = i;
                break;
            }
        }
        return in.substring(0, fg + 1);
    }
}
