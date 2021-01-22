package com.app.service.utils;


import com.app.service.AppMain;
import com.app.service.notification.NotificationType;
import javafx.application.Platform;

public class Utils {

    public static String[] lineSplitAndExtractNumbers(String line, String delimiter) {
        String[] values = line.split(delimiter);
        for (int i=0; i<values.length; i++) {
            values[i] = extractNumber(values[i]);
        }
        return values;
    }

    private static String extractNumber(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < input.length(); i++) {
            char sign = input.charAt(i);
            if (sign == '.'|| Character.isDigit(sign)) {
                stringBuilder.append(sign);
            }
        }
        return stringBuilder.toString();
    }

    public static void closeApp() {
        Platform.runLater(() -> {
            try {
                AppMain.communicationService.killCommunicator();
                Thread.sleep(300);
                Platform.exit();
                System.exit(0);
            } catch (InterruptedException e) {
                AppMain.notificationService.createNotification("Could not quit -> " + e.getMessage(), NotificationType.ERROR);
            }
        });
    }
}
