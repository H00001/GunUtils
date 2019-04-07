package top.gunplan.utils;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * GunDirectoryUtil
 *
 * @author dosdrtt
 */
public final class GunDirectoryUtil {
    public static List<GunHttpMappingFileReference> scanAllFilesFromDirectory(final String folder) throws IOException {
        List<GunHttpMappingFileReference> files = new LinkedList<>();
        nextFindFile(files, folder, ".class", "");
        return files;
    }

    private static void nextFindFile(@NotNull List<GunHttpMappingFileReference> files, String folder, String pattern, String base) throws IOException {
        Path path = Paths.get(folder);
        DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
        for (Path processPath : dirStream) {
            if (Files.isDirectory(processPath)) {
                nextFindFile(files, processPath.toString(), pattern, processPath.toString().replace(folder, ""));
            } else {
                if (processPath.toString().endsWith(pattern)) {
                    files.add(new GunHttpMappingFileReference(base.replace("/", ".") + ".", folder, processPath.toFile()));
                }
            }
        }
        dirStream.close();
    }

    public static class GunHttpMappingFileReference {
        String base;
        String packname;
        File clcasfile;

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getPackname() {
            return packname;
        }

        public void setPackname(String packname) {
            this.packname = packname;
        }

        public File getClcasfile() {
            return clcasfile;
        }

        public void setClcasfile(File clcasfile) {
            this.clcasfile = clcasfile;
        }

        public GunHttpMappingFileReference(String base, String packname, File clcasfile) {

            this.base = base;
            this.packname = packname;
            this.clcasfile = clcasfile;
        }
    }
}
