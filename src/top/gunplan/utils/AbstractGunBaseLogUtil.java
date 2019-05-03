package top.gunplan.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * @author dosdrtt
 */
public abstract class AbstractGunBaseLogUtil {

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
    private static volatile int level = 0;
    private static volatile OutputStream stdoutput;
    private static volatile OutputStream erroutput;

    private static void realPrint(final OutputStream os, final String TAG, String content, String... val) {
        String op = TAG;
        if (val != null && val.length != 0) {
            StringBuilder contentBuilder = new StringBuilder();
            for (String aVal : val) {
                contentBuilder.append(aVal);
            }
            op = op + " " + contentBuilder.toString() + " " + content;
        } else {
            op = op + " " + content;
        }
        try {
            os.write((op + "\n").getBytes());
            stdoutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStdoutput(OutputStream os) {
        AbstractGunBaseLogUtil.stdoutput = os;
    }


    public static void info(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASELEVEL)) {
            realPrint(stdoutput, "[INFO] ", s, val);
        }
    }

    public static void debug(String s, String... val) {

        if (AbstractGunBaseLogUtil.level <= (BASELEVEL << 1)) {
            realPrint(stdoutput, "[DEBUG] ", s, val);
        }
    }

    public static void error(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASELEVEL << 2)) {
            realPrint(erroutput, "[ERROR] ", s, val);
        }
    }

    public static void error(Exception s) {
        if (AbstractGunBaseLogUtil.level <= (BASELEVEL << 2)) {
            realPrint(erroutput, "[ERROR] ", s.getLocalizedMessage());
        }
    }

    public static void urgency(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASELEVEL << 3)) {
            realPrint(erroutput, "[URGENCY] ", s, val);
        }
    }

    public static void outputFile(String file) {
        try {
            byte[] property = Files.readAllBytes(Paths.get(Objects.requireNonNull(AbstractGunBaseLogUtil.class.getClassLoader().getResource(file)).toURI()));
            realPrint(stdoutput, "", new String(property));
        } catch (Exception e) {
        }
    }

    public static void setLevel(int level) {
        AbstractGunBaseLogUtil.level = level;
    }
}
