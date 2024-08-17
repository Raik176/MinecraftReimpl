package org.rhm.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtils {
    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    public static byte[] digestData(String data, PublicKey pubKey, SecretKey secKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return digestData(data.getBytes("ISO_8859_1"), secKey.getEncoded(), pubKey.getEncoded());
    }

    private static byte[] digestData(byte[]... bytes) throws NoSuchAlgorithmException {
        MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");

        for (byte[] b : bytes) {
            messagedigest.update(b);
        }

        return messagedigest.digest();
    }

    public static SecretKey decryptByteToSecretKey(PrivateKey privKey, byte[] bytes) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new SecretKeySpec(decryptUsingKey(privKey, bytes), "AES");
    }

    public static byte[] decryptUsingKey(Key key, byte[] bytes) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return cipherData(2, key, bytes);
    }

    public static byte[] encryptUsingKey(Key key, byte[] bytes) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(bytes);
    }

    public static PublicKey byteToPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(bytes);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        return keyfactory.generatePublic(encodedkeyspec);
    }

    private static byte[] cipherData(int operation, Key key, byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return setupCipher(operation, key.getAlgorithm(), key).doFinal(bytes);
    }

    private static Cipher setupCipher(int operation, String p_13581_, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(p_13581_);
        cipher.init(operation, key);
        return cipher;
    }

    public static Cipher getCipher(int operation, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(operation, key, new IvParameterSpec(key.getEncoded()));
        return cipher;
    }
}
