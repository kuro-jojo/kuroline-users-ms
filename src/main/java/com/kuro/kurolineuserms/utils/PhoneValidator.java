package com.kuro.kurolineuserms.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneValidator {

    public static boolean isValidPhoneNumber(String phoneNumber) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumberProto = phoneNumberUtil.parse(phoneNumber, null);
        return phoneNumberUtil.isValidNumber(phoneNumberProto);
    }
}
