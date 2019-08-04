package top.gunplan.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;


/**
 * GunLogger
 * <p>
 * this is a log interface
 *
 * @author frank albert
 * @version 0.0.0.1
 * @date 2019-07-21 08:43
 */

public interface GunLogger {

    /**
     * <p>
     * set format
     * </p>
     *
     * @param format format
     */
    void setFormat(String format);

    GunLogger setTAG(Class<?> tag);


    void setErrOutput(OutputStream erroutput);


    void init();


    void setStdOutput(OutputStream os);


    void info(String s, String... val);

    void debug(String s, String... val);


    void error(String s, String... val);

    void error(Throwable s);


    void urgency(String s, String... val);

    void outputFile(String file) throws IOException, URISyntaxException;

    void setLevel(int level);


}
