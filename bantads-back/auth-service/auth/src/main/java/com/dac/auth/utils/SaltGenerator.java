package com.dac.auth.utils;

import java.security.SecureRandom;

public class SaltGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateSalt() {
        byte[] salt = new byte[16]; // 128 bit salt
        secureRandom.nextBytes(salt);
        return javax.xml.bind.DatatypeConverter.printHexBinary(salt);
    }

}
