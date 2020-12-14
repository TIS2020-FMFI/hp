package com.app.service;

public enum NotificationType {
    SUCCESS,
    ERROR,
    WARNING,
    ANNOUNCEMENT;

    public static String getClassName(NotificationType type) {
        switch (type) {
            case ERROR: return "error";
            case SUCCESS: return "success";
            case WARNING: return "warning";
            case ANNOUNCEMENT:return "announcement";
            default: return null;
        }
    }
}
