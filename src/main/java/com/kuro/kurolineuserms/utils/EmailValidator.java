package com.kuro.kurolineuserms.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static boolean isEmailInvalid(String email) {
        if (email == null) {
            return true;
        }
        Matcher matcher = pattern.matcher(email);
        return !matcher.matches();
    }
}
