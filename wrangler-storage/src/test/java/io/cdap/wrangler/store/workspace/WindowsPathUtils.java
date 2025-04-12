package io.cdap.wrangler.store.workspace;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class to handle Windows path issues.
 */
public class WindowsPathUtils {
    /**
     * Converts a path that may contain colons to a Windows-compatible path.
     * @param path The path to convert
     * @return A Windows-compatible path
     */
    public static String toWindowsPath(String path) {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // Replace colons with underscores for Windows compatibility
            return path.replace(":", "_");
        }
        return path;
    }

    /**
     * Creates a directory with Windows-compatible path.
     * @param path The path to create
     * @return The created directory
     */
    public static File createDirectory(String path) {
        String windowsPath = toWindowsPath(path);
        File dir = new File(windowsPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
} 