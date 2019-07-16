package top.gunplan.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;



/**
 * @author dosdrtt
 */
public abstract class AbstractGunBaseLogUtil {

    /**
     * this is s static block
     */


    private static final int BASE_LEVEL;
    private static volatile int level = 0;

    private static volatile SimpleDateFormat ffm = null;

    public static void setFormat(String format) {
        ffm = new SimpleDateFormat(format);
    }

    static {
        stdoutput = System.out;
        erroutput = System.err;
        BASE_LEVEL = 1;
    }

    public static synchronized void setErroutput(OutputStream erroutput) {
        AbstractGunBaseLogUtil.erroutput = erroutput;
    }

    public static void init() {

    }

    private static volatile OutputStream stdoutput;
    private static volatile OutputStream erroutput;

    private static void realPrint(final OutputStream os, final String tag, String content, String... val) {
        String op = tag;
        if (val != null && val.length != 0) {
            StringBuilder contentBuilder = new StringBuilder();
            for (String aVal : val) {
                contentBuilder.append(aVal);
            }
            op = op + contentBuilder.toString() + " " + content;
        } else {
            op = op + content;
        }
        try {
            os.write(("[" + (ffm == null ? System.currentTimeMillis() : ffm.format(new Date())) + "] " + op + "\n").getBytes());
            stdoutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void setStdoutput(OutputStream os) {
        AbstractGunBaseLogUtil.stdoutput = os;
    }


    public static void info(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASE_LEVEL)) {
            realPrint(stdoutput, "[INFO] ", s, val);
        }
    }

    public static void debug(String s, String... val) {

        if (AbstractGunBaseLogUtil.level <= (BASE_LEVEL << 1)) {
            realPrint(stdoutput, "[DEBUG] ", s, val);
        }
    }

    public static void error(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASE_LEVEL << 2)) {
            realPrint(erroutput, "[ERROR] ", s, val);
        }
    }

    public static void error(Exception s) {
        if (AbstractGunBaseLogUtil.level <= (BASE_LEVEL << 2)) {
            realPrint(erroutput, "[ERROR] ", s.getMessage());
        }
    }

    public static void urgency(String s, String... val) {
        if (AbstractGunBaseLogUtil.level <= (BASE_LEVEL << 3)) {
            realPrint(erroutput, "[URGENCY] ", s, val);
        }
    }

    public static void outputFile(String file) throws IOException, URISyntaxException {

        byte[] property = Files.readAllBytes(Paths.get(Objects.requireNonNull(AbstractGunBaseLogUtil.class
                .getClassLoader().getResource(file)).toURI()));
        realPrint(stdoutput, "", new String(property));

    }

    public static void setLevel(int level) {
        AbstractGunBaseLogUtil.level = level;
    }
}
