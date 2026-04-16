package org.client.scrcpy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.client.scrcpy.utils.Progress;
import org.client.scrcpy.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LogActivity extends Activity {

    private static final String[] LOGCAT_COMMAND = new String[]{"logcat", "-d", "-v", "time", "-s", "Scrcpy:*", "ADB:*", "*:S"};

    private TextView logsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        logsTextView = findViewById(R.id.text_logs);
        Button refreshButton = findViewById(R.id.button_refresh_logs);
        Button closeButton = findViewById(R.id.button_close_logs);
        refreshButton.setOnClickListener(v -> loadLogs());
        closeButton.setOnClickListener(v -> finish());
        loadLogs();
    }

    private void loadLogs() {
        Progress.showDialog(this, getString(R.string.please_wait));
        ThreadUtils.workPost(() -> {
            final String logs = getLogs();
            ThreadUtils.post(() -> {
                Progress.closeDialog();
                logsTextView.setText(logs);
            });
        });
    }

    private String getLogs() {
        Process process = null;
        StringBuilder builder = new StringBuilder();
        try {
            process = new ProcessBuilder(LOGCAT_COMMAND).redirectErrorStream(true).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            }
            process.waitFor();
        } catch (Exception e) {
            return getString(R.string.logs_load_failed) + ": " + e.getMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        if (builder.length() == 0) {
            return getString(R.string.logs_empty);
        }
        return builder.toString();
    }
}
