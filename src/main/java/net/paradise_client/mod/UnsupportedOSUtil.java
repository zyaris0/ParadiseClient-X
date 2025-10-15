package net.paradise_client.mod;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import net.paradise_client.Constants;

import javax.swing.*;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class to check the operating system for compatibility. Ensures the client only runs on modern, supported
 * versions (Windows 11, macOS 12+, Linux kernel 5.10+).
 */
public class UnsupportedOSUtil {

  private static final AtomicBoolean checksPerformed = new AtomicBoolean(false);

  // Minimum version requirements
  private static final int MIN_WINDOWS_BUILD = 22000; // Windows 11 build number
  private static final int MIN_MACOS_VERSION = 12; // macOS Monterey (12.0)
  private static final int MIN_LINUX_KERNEL_MAJOR = 5;
  private static final int MIN_LINUX_KERNEL_MINOR = 10; // Kernel 5.10 or newer

  /**
   * Executes all necessary OS and architecture checks.
   */
  public static void loadUtils() {
    // Skip if checks are already done.
    if (checksPerformed.getAndSet(true)) {
      Constants.LOGGER.debug("OS checks already performed, skipping.");
      return;
    }

    try {
      String osName = getSystemProperty("os.name", "unknown");
      String osVersion = getSystemProperty("os.version", "unknown");

      Constants.LOGGER.info("Starting OS compatibility checks. Detected OS: {} {}", osName, osVersion);

      // Run checks
      checkArchitecture();
      checkWindowsCompatibility();
      checkMacOSCompatibility();
      checkLinuxCompatibility();

      Constants.LOGGER.info("All OS compatibility checks passed successfully.");

    } catch (UnsupportedOperationException e) {
      // Already handled and logged.
      throw e;
    } catch (Exception e) {
      Constants.LOGGER.error("An unexpected error occurred during compatibility check.", e);
      showErrorDialog("An unexpected error occurred while checking system compatibility.\n\n" +
        "Error: " +
        e.getMessage(), "Paradise Client – System Error");
      throw new UnsupportedOperationException("System compatibility check failed unexpectedly.", e);
    }
  }

  /**
   * Checks for 64-bit architecture for Windows, 64-bit supported only.
   */
  private static void checkArchitecture() {
    String osName = getSystemProperty("os.name", "").toLowerCase();
    if (!osName.contains("windows")) {
      return;
    }

    String osArch = getSystemProperty("os.arch", "").toLowerCase();
    boolean is64Bit = osArch.contains("amd64") || osArch.contains("x86_64");

    if (!is64Bit) {
      Constants.LOGGER.error("32-bit Windows detected. Not supported.");
      showNativeWindowsError();
      System.exit(1);
    }

    Constants.LOGGER.info("Architecture check passed: Confirmed 64-bit system.");
  }

  /**
   * Displays a native Windows error dialog for the 32-bit failure, falling back to Swing if necessary.
   */
  private static void showNativeWindowsError() {
    try {
      String path = getApplicationPath();

      try {
        // Try to use the native Windows message box via JNA.
        User32Extended.INSTANCE.MessageBoxA(null,
          path + " is not a valid Win32 application. (This client requires 64-bit Windows.)",
          "Paradise Client – System Error",
          0x10
        );
      } catch (UnsatisfiedLinkError | Exception e) {
        // Fallback to the Java dialog.
        Constants.LOGGER.warn("Failed to show native Windows dialog, using standard Java fallback.", e);
        showErrorDialog(
          "Paradise Client is only compatible with 64-bit Windows. Please use a 64-bit version of Windows to run the client.",
          "Paradise Client – Architecture Not Supported");
      }
    } catch (Exception e) {
      Constants.LOGGER.error("General error while attempting to display the 32-bit warning.", e);
    }
  }

  /**
   * Checks if the Windows version meets the minimum requirement (Windows 11+).
   */
  private static void checkWindowsCompatibility() {
    String osName = getSystemProperty("os.name", "").toLowerCase();
    if (!osName.contains("windows")) {
      return;
    }

    // Get the accurate build number via Registry query.
    int currentBuild = getWindowsCurrentBuild();

    if (currentBuild >= MIN_WINDOWS_BUILD) {
      Constants.LOGGER.info("Windows version check passed: Confirmed Windows 11 or later via build number: {}",
        currentBuild);
      return;
    }

    // Fallback: Check os.name explicitly.
    if (osName.contains("windows 11")) {
      Constants.LOGGER.info("Windows version check passed: Windows 11 detected in os.name property.");
      return;
    }

    // System is too old. Generate error message and fail.
    String message = getWindowsEOLMessage(osName);
    logAndShowError(message, "Paradise Client requires Windows 11 or higher");
  }

  /**
   * Retrieves the reliable Windows build number by querying the Registry.
   */
  private static int getWindowsCurrentBuild() {
    try {
      // Query the registry for "CurrentBuild"
      String command =
        "cmd /c reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\" /v CurrentBuild";
      Process process = Runtime.getRuntime().exec(command);

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.contains("CurrentBuild")) {
            // Parse the build number from the end of the line.
            String[] parts = line.trim().split("\\s+");
            if (parts.length > 2) {
              int build = Integer.parseInt(parts[parts.length - 1]);
              Constants.LOGGER.debug("Windows build detected via Registry query: {}", build);
              return build;
            }
          }
        }
      }
    } catch (Exception e) {
      Constants.LOGGER.warn("Failed to reliably query Windows Registry for build number.", e);
    }

    // Secondary fallback using os.version property.
    try {
      String version = getSystemProperty("os.version", "");
      if (!version.isEmpty()) {
        String[] parts = version.split("\\.");
        if (parts.length > 2) {
          return Integer.parseInt(parts[2]);
        }
      }
    } catch (Exception e) {
      Constants.LOGGER.warn("Failed to determine Windows build number via os.version property.", e);
    }

    return 0;
  }

  /**
   * Checks if the macOS version meets the minimum requirement (Monterey/12+).
   */
  private static void checkMacOSCompatibility() {
    String osName = getSystemProperty("os.name", "").toLowerCase();
    if (!osName.contains("mac")) {
      return;
    }

    String osVersion = getSystemProperty("os.version", "");
    if (osVersion.isEmpty()) {
      Constants.LOGGER.warn("Unable to determine macOS version.");
      return;
    }

    try {
      int majorVersion = parseMacOSMajorVersion(osVersion);

      if (majorVersion < MIN_MACOS_VERSION) {
        String macOSName = getMacOSName(majorVersion, osVersion);
        String message = String.format("<html>You're currently running %s.<br><br>" +
          "Paradise Client requires <b>macOS Monterey (12.0) or newer</b> to function correctly.<br><br>" +
          "To use the client, please update your Mac's operating system.</html>", macOSName);

        logAndShowError(message, "Paradise Client requires macOS Monterey or higher");
      }

      Constants.LOGGER.info("macOS version check passed: {}", osVersion);

    } catch (NumberFormatException e) {
      Constants.LOGGER.error("Failed to parse macOS version: {}", osVersion, e);
    }
  }

  /**
   * Extracts the major version number from the macOS version string.
   */
  private static int parseMacOSMajorVersion(String osVersion) {
    String[] parts = osVersion.split("\\.");
    if (parts.length > 0) {
      return Integer.parseInt(parts[0]);
    }
    throw new NumberFormatException("Invalid macOS version format: " + osVersion);
  }

  /**
   * Checks if the Linux kernel is recent enough (5.10+).
   */
  private static void checkLinuxCompatibility() {
    String osName = getSystemProperty("os.name", "").toLowerCase();
    if (!osName.contains("linux")) {
      return;
    }

    String osVersion = getSystemProperty("os.version", "");
    if (osVersion.isEmpty()) {
      Constants.LOGGER.warn("Unable to determine Linux kernel version.");
      return;
    }

    try {
      KernelVersion kernel = parseLinuxKernelVersion(osVersion);

      if (kernel.major < MIN_LINUX_KERNEL_MAJOR ||
        (kernel.major == MIN_LINUX_KERNEL_MAJOR && kernel.minor < MIN_LINUX_KERNEL_MINOR)) {

        String message = String.format("<html>Your Linux kernel version is %d.%d, which is too old.<br><br>" +
            "Paradise Client requires <b>Linux kernel %d.%d or later</b>.<br><br>" +
            "Please update your distribution to a modern version (e.g., Ubuntu 22.04 LTS, Debian 11, Fedora 38+) to continue.</html>",
          kernel.major,
          kernel.minor,
          MIN_LINUX_KERNEL_MAJOR,
          MIN_LINUX_KERNEL_MINOR);

        logAndShowError(message, "Paradise Client – Outdated Linux Kernel");
      }

      Constants.LOGGER.info("Linux kernel version check passed: {}.{}", kernel.major, kernel.minor);

    } catch (NumberFormatException e) {
      Constants.LOGGER.error("Failed to parse Linux kernel version: {}", osVersion, e);
    }
  }

  /**
   * Parses the major and minor kernel version components.
   */
  private static KernelVersion parseLinuxKernelVersion(String osVersion) {
    String[] parts = osVersion.split("[.\\-]");
    if (parts.length >= 2) {
      int major = Integer.parseInt(parts[0]);
      int minor = Integer.parseInt(parts[1]);
      return new KernelVersion(major, minor);
    }
    throw new NumberFormatException("Invalid kernel version format: " + osVersion);
  }

  /**
   * Gets the macOS name for error messages.
   */
  private static String getMacOSName(int majorVersion, String fullVersion) {
    if (majorVersion == 11) {
      return "macOS Big Sur (11.x)";
    }

    if (majorVersion == 10) {
      if (fullVersion.startsWith("10.15")) {
        return "macOS Catalina (10.15)";
      }
      if (fullVersion.startsWith("10.14")) {
        return "macOS Mojave (10.14)";
      }
      if (fullVersion.startsWith("10.13")) {
        return "macOS High Sierra (10.13)";
      }
      return "an older version of Mac OS X / macOS 10.x";
    }

    return "a very old version of macOS (version " + majorVersion + ")";
  }

  /**
   * Determines the specific Windows EOL version for tailored error messages.
   */
  private static String getWindowsEOLMessage(String osName) {
    if (osName.contains("xp")) {
      return buildWindowsEOLMessage("Windows XP");
    }
    if (osName.contains("vista")) {
      return buildWindowsEOLMessage("Windows Vista");
    }
    if (osName.contains("7")) {
      return buildWindowsEOLMessage("Windows 7");
    }
    if (osName.contains("8.1")) {
      return buildWindowsEOLMessage("Windows 8.1");
    }
    if (osName.contains("8")) {
      return buildWindowsEOLMessage("Windows 8");
    }
    if (osName.contains("windows 10")) {
      return buildWindowsEOLMessage("Windows 10");
    }

    // Catch-all message
    return String.format("<html>Your current Windows version is too old or could not be identified.<br><br>" +
      "Paradise Client requires <b>Windows 11 (Build %d+)</b> or a newer version.<br><br>" +
      "Please upgrade your operating system to continue.</html>", MIN_WINDOWS_BUILD);
  }

  /**
   * Generates the standard EOL error message for Windows.
   */
  private static String buildWindowsEOLMessage(String windowsVersion) {
    return String.format("<html>You are currently running %s.<br><br>" +
      "This version of Windows is no longer supported by Microsoft, and therefore cannot run Paradise Client.<br><br>" +
      "You must upgrade to <b>Windows 11</b> to use the client.</html>", windowsVersion);
  }

  /**
   * Logs the fatal error prominently, shows the dialog, and throws the exception.
   */
  private static void logAndShowError(String displayMessage, String exceptionMessage) {
    // Clean log message by removing HTML tags.
    String logMessage = displayMessage.replaceAll("<[^>]*>", "").replace("\n", " ");

    Constants.LOGGER.error("========================================");
    Constants.LOGGER.error("FATAL ERROR: UNSUPPORTED OPERATING SYSTEM DETECTED");
    Constants.LOGGER.error("========================================");
    Constants.LOGGER.error(logMessage);
    Constants.LOGGER.error("========================================");

    showErrorDialog(displayMessage, "Paradise Client – System Compatibility Failure");

    throw new UnsupportedOperationException(exceptionMessage);
  }

  /**
   * Displays a standard Java error dialog.
   */
  private static void showErrorDialog(String message, String title) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Safely retrieves a system property, returning a default if access fails.
   */
  private static String getSystemProperty(String key, String defaultValue) {
    try {
      String value = System.getProperty(key);
      return value != null ? value : defaultValue;
    } catch (SecurityException e) {
      Constants.LOGGER.warn("Security manager prevented access to system property: {}", key, e);
      return defaultValue;
    }
  }

  /**
   * Attempts to find the full path of the application's executable file for error messages.
   */
  private static String getApplicationPath() {
    try {
      String rawPath = UnsupportedOSUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      String decodedPath = URLDecoder.decode(rawPath, StandardCharsets.UTF_8);
      File file = new File(decodedPath);
      return file.getAbsolutePath();
    } catch (Exception e) {
      Constants.LOGGER.warn("Failed to retrieve the application's path.", e);
      return "Paradise Client";
    }
  }

  /**
   * Data structure for major/minor kernel version.
   */
  private static class KernelVersion {
    final int major;
    final int minor;

    KernelVersion(int major, int minor) {
      this.major = major;
      this.minor = minor;
    }
  }

  /**
   * JNA interface to expose the native Windows function `MessageBoxA` (user32.dll).
   */
  private interface User32Extended extends StdCallLibrary {
    User32Extended INSTANCE = Native.loadLibrary("user32", User32Extended.class);

    int MessageBoxA(WinDef.HWND hWnd, String lpText, String lpCaption, int uType);
  }
}