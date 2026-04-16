package org.server.scrcpy;

import org.server.scrcpy.util.Workarounds;
import org.server.scrcpy.util.SettingsException;
import org.server.scrcpy.wrappers.ContentProvider;
import org.server.scrcpy.wrappers.ServiceManager;

import java.io.IOException;

public final class Server {

    private static String ip = null;

    private Server() {
        // not instantiable
    }

    private static void scrcpy(Options options) throws IOException {
        Workarounds.apply();  // init content

        final Device device = new Device(options);
        try {
            try (DroidConnection connection = DroidConnection.open(ip)) {
                applyKeepAwake(options);
                ScreenEncoder screenEncoder = new ScreenEncoder(options);

                // asynchronous
                startEventController(device, connection, screenEncoder, options);

                try {
                    // synchronous
                    screenEncoder.streamScreen(device, connection.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    // this is expected on close
                    Ln.d("Screen streaming stopped");
                }
            }
        } finally {
            if (!device.setDisplayPower(true)) {
                Ln.w("Could not restore display power on server exit");
            }
        }
    }

    private static void startEventController(final Device device, final DroidConnection connection, ScreenEncoder screenEncoder, Options options) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new EventController(device, connection, screenEncoder, options).control();
                } catch (IOException e) {
                    // this is expected on close
                    Ln.d("Event controller stopped");
                }
            }
        }).start();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Options createOptions(String... args) {
        Options options = new Options();

        if (args.length < 1) {
            return options;
        }
        ip = normalizeArg(args[0]);


        if (args.length < 2) {
            return options;
        }
        int maxSize = Integer.parseInt(normalizeArg(args[1])) & ~7; // multiple of 8
        options.setMaxSize(maxSize);

        if (args.length < 3) {
            return options;
        }
        int bitRate = Integer.parseInt(normalizeArg(args[2]));
        options.setBitRate(bitRate);

        if (args.length < 4) {
            return options;
        }
        // use "adb forward" instead of "adb tunnel"? (so the server must listen)
        boolean tunnelForward = Boolean.parseBoolean(normalizeArg(args[3]));
        options.setTunnelForward(tunnelForward);

        if (args.length < 5) {
            return options;
        }
        options.setMaxFps(Integer.parseInt(normalizeArg(args[4])));

        if (args.length < 6) {
            return options;
        }
        options.setVideoCodec(normalizeArg(args[5]));

        if (args.length < 7) {
            return options;
        }
        options.setAudioCodec(normalizeArg(args[6]));

        if (args.length < 8) {
            return options;
        }
        options.setAudioBitRate(Integer.parseInt(normalizeArg(args[7])));

        if (args.length < 9) {
            return options;
        }
        options.setAudioForward(Boolean.parseBoolean(normalizeArg(args[8])));

        if (args.length < 10) {
            return options;
        }
        options.setTurnScreenOff(Boolean.parseBoolean(normalizeArg(args[9])));

        if (args.length < 11) {
            return options;
        }
        options.setKeepAwake(Boolean.parseBoolean(normalizeArg(args[10])));
        return options;
    }

    private static String normalizeArg(String arg) {
        if (arg == null) {
            return "";
        }
        String trimmed = arg.trim();
        if (trimmed.endsWith(";")) {
            return trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static void applyKeepAwake(Options options) {
        if (!options.isKeepAwake()) {
            return;
        }
        try (ContentProvider contentProvider = ServiceManager.getActivityManager().createSettingsProvider()) {
            if (contentProvider == null) {
                Ln.w("Could not access settings provider for keep-awake option");
                return;
            }
            contentProvider.putValue(ContentProvider.TABLE_GLOBAL, "stay_on_while_plugged_in", "3");
        } catch (SettingsException e) {
            Ln.w("Could not set keep-awake option: " + e.getMessage());
        }
    }

    public static void main(String... args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Ln.e("Exception on thread " + t, e);
            }
        });

        try {
            Process cmd = Runtime.getRuntime().exec("rm /data/local/tmp/scrcpy-server.jar");
            cmd.waitFor();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        Options options = createOptions(args);
        scrcpy(options);
    }
}
