package com.mehbub.lib;

public class StringUtil {

    public boolean isNullOrEmptyOrBlank(String str) {
        if (str == null) {
            return true;
        } else if (str.isEmpty()) {
            return true;
        } else if (str.trim().isEmpty()) {
            return true;
        } else if (str.isBlank()) {
            return true;
        }
        return false;
    }
}
