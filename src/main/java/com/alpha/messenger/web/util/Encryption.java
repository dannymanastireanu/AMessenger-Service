package com.alpha.messenger.web.util;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Encryption {

    public static String decryptRSA(String message, String pvKey) {
        try {
            byte[] arrBytePvt = Base64.getDecoder().decode(pvKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey copyPrivate = kf.generatePrivate(new PKCS8EncodedKeySpec(arrBytePvt));
            Cipher dec = Cipher.getInstance("RSA");
            dec.init(Cipher.DECRYPT_MODE, copyPrivate);


            byte[] bytes = Base64.getDecoder().decode(message);
            String decrypted = new String(dec.doFinal(bytes), UTF_8);
            return decrypted;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encryptRSA(String message, String pbKey) {
        try {
            byte[] arrBytePb = Base64.getDecoder().decode(pbKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey copyPublic = kf.generatePublic(new X509EncodedKeySpec(arrBytePb));

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, copyPublic);

            byte[] cipherText = encryptCipher.doFinal(message.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
