package com.example.johanmorales.loginapplication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Manager {

    public static String encode(String value) {

        StringBuilder hexString = new StringBuilder();

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(value.getBytes());

            byte messageDigest[] = md.digest();

            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hexString.toString();
    }
}
