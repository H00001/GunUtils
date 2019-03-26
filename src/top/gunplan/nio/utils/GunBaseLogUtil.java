package top.gunplan.nio.utils;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author dosdrtt
 */
public abstract class GunBaseLogUtil {

    /**
     * this is s static block
     *
     * */

    static {
        stdoutput = System.out;
        erroutput = System.err;
        BASELEVEL = 1;
    }

    private static final int BASELEVEL;
    private static volatile int level = BASELEVEL << 1;
    private static volatile OutputStream stdoutput;
    private static volatile OutputStream erroutput;


    public static void setStdoutput(OutputStream os) {
        GunBaseLogUtil.stdoutput = os;
    }


    public static void info(String s) {
        if (GunBaseLogUtil.level <= (BASELEVEL)) {
            try {
                GunBaseLogUtil.stdoutput.write(("[LOG][INFOR] " + s + "\n").getBytes());
                stdoutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void debug(String s) {
        if (GunBaseLogUtil.level <= (BASELEVEL << 1)) {
            try {
                GunBaseLogUtil.stdoutput.write(("[LOG][DEBUG] " + s + "\n").getBytes());
                stdoutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void error(String s) {
        if (GunBaseLogUtil.level <= (BASELEVEL << 2)) {
            try {
                GunBaseLogUtil.erroutput.write(("[LOG][ERROR] " + s + "\n").getBytes());
                erroutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void urgency(String s) {
        if (GunBaseLogUtil.level <= (BASELEVEL << 3)) {
            try {
                GunBaseLogUtil.erroutput.write(("[LOG][URGEN] " + s + "\n").getBytes());
                erroutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setLevel(int level) {
        GunBaseLogUtil.level = level;
    }
}
