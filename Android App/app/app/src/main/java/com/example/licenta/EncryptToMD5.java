package com.example.licenta;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptToMD5 {
    public EncryptToMD5(String password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public String getMd5() {
        return md5;
    }

    public String generateMd5Hash(){
        String method = "MD5";

        try{
            MessageDigest digest = MessageDigest.getInstance(method);
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();

            for (byte aMsgDigest : messageDigest) {
                String h = Integer.toHexString( 0xFF & aMsgDigest);

                while(h.length() < 2){
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String md5;
}
