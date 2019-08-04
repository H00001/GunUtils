package top.gunplan.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


/**
 * GunNxLogger a logger
 *
 * @author dosdrtt
 * @version 2.0.0.2
 */

public final class GunNxLogger implements GunLogger {
    /**
     * this is s static block
     */
    private static final int BASE_LEVEL = 1;
    private volatile int level = 0;
    private AtomicReference<Class<?>> clazz = new AtomicReference<>(null);

    private volatile SimpleDateFormat ffm = null;
    private volatile OutputStream stdoutput;
    private volatile OutputStream erroutput;

    {
        stdoutput = System.out;
        erroutput = System.err;
    }

    @Override
    public void setFormat(String format) {
        ffm = new SimpleDateFormat(format);
    }

    @Override
    public GunLogger setTAG(Class<?> tag) {
        clazz.setRelease(tag);
        return this;
    }

    @Override
    public synchronized void setErrOutput(OutputStream errOutput) {
        this.erroutput = errOutput;
    }

    @Override
    public void init() {

    }

    private void realPrint(final OutputStream os, final String tag, String content, String... val) {
        String op = tag;
        String className = "[" + (clazz.get() == null ? "null" : clazz.get().getSimpleName()) + "] ";
        if (val != null && val.length != 0) {
            StringBuilder contentBuilder = new StringBuilder();
            for (String aVal : val) {
                contentBuilder.append(aVal);
            }
            op = op + contentBuilder.toString() + className + " " + content;
        } else {
            op = op + className + content;
        }
        try {
            os.write(("[" + (ffm == null ? System.currentTimeMillis() : ffm.format(new Date())) + "] " + op + "\n").getBytes());
            stdoutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void setStdOutput(OutputStream os) {
        this.stdoutput = os;
    }


    @Override
    public void info(String s, String... val) {
        if (this.level <= (BASE_LEVEL)) {
            realPrint(stdoutput, "[   INFO] ", s, val);
        }
    }

    @Override
    public void debug(String s, String... val) {

        if (this.level <= (BASE_LEVEL << 1)) {
            realPrint(stdoutput, "[  DEBUG] ", s, val);
        }
    }

    @Override
    public void error(String s, String... val) {
        if (this.level <= (BASE_LEVEL << 2)) {
            realPrint(erroutput, "[  ERROR] ", s, val);
        }
    }

    @Override
    public void error(Throwable s) {
        if (this.level <= (BASE_LEVEL << 2)) {
            realPrint(erroutput, "[  ERROR] ", s.getMessage());
        }
    }

    @Override
    public void urgency(String s, String... val) {
        if (this.level <= (BASE_LEVEL << 3)) {
            realPrint(erroutput, "[URGENCY] ", s, val);
        }
    }

    @Override
    public void outputFile(String file) throws IOException, URISyntaxException {
        String property = Files.readString(Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource(file)).toURI()));
        realPrint(stdoutput, "[ STDOUT] ", property);
    }


    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}

