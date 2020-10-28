package com.onenineeight.cteee;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionInterceptor implements Interceptor {
    private static final String TAG = EncryptionInterceptor.class.getSimpleName();
    private static final boolean DEBUG = true;
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody oldBody = request.body();
        Buffer buffer = new Buffer();
        oldBody.writeTo(buffer);
        String strOldBody = buffer.readUtf8();

        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        String strNewBody = encrypt(strOldBody);
        RequestBody body = RequestBody.create(mediaType, strNewBody);
        request = request.newBuilder().header("Content-Type", body.contentType().toString()).header("Content-Length", String.valueOf(body.contentLength())).method(request.method(), body).build();

        return chain.proceed(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String encrypt(String text) {

        try {
            //original text print
            System.out.println("Original Text : " + text);
            String[] tokens = text.split(":");

            //parsing the username only
            for (String t : tokens)
                System.out.println(t);
            String parsed = tokens[1].substring(1, tokens[1].length() - 2);
            System.out.println("The username only: " + parsed);
            byte[] plaintext = parsed.getBytes();
            System.out.println("The username only: " + plaintext);


            //STEP 1. GENERATE KEY
            KeyGenerator keyGenerator;
            SecretKey secretKey;
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            secretKey = keyGenerator.generateKey();
            //STEP 1.5. Set INitialization Vector - OPTIONAL but recommended
            byte[] IV = new byte[16];
            SecureRandom random;
            random = new SecureRandom();
            random.nextBytes(IV);


            //STEP 2. ENCRYPT
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(plaintext);
            //return cipherText;

            //Step 3. Print encrypted text for validation
            String str2 = Base64.getEncoder().encodeToString(cipherText);
            System.out.println("Encrypted Text : " + str2);
            String resultString = tokens[0] + ":\"" + cipherText + "\"}";
            System.out.println("Final Text : " + resultString);


            /*
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);

            //Generate Key
            SecretKey key = keygen.generateKey();
            System.out.println("The key: " + key);

            //byte[] iv = new byte[12];
            //SecureRandom random = new SecureRandom();
            //random.nextBytes(iv);

            //Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            byte[] iv = cipher.getIV();
            SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
            //GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, iv);
            //cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //perform encryption
            byte[] ciphertext = cipher.doFinal(plaintext);

            String str=new String(ciphertext);
            String str2;
            str2 = Base64.getEncoder().encodeToString(ciphertext);

            System.out.println("Encrypted Text : " + str2);
            String resultString = tokens[0] + ":\"" + str2 + "\"}";
            System.out.println("Final Text : " + resultString);


            // Initialize Cipher for DECRYPT_MODE
            // Get Cipher Instance
            Cipher dcipher = Cipher.getInstance("AES/GCM/PKCS5PADDING");

            // Create SecretKeySpec
            //SecretKeySpec dkeySpec = new SecretKeySpec(key.getEncoded(), "AES");

            // Create GCMParameterSpec
            //GCMParameterSpec dgcmParameterSpec = new GCMParameterSpec(16 * 8, iv);

            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Perform Decryption
            byte[] decryptedText = dcipher.doFinal(ciphertext);
            String str3;
            str3 = Base64.getEncoder().encodeToString(decryptedText);
            System.out.println("DeCrypted Text : " + str3);
            */
            //STEP 4. DECRYPT
            try {
                Cipher dcipher = Cipher.getInstance("AES");
                SecretKeySpec dkeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
                IvParameterSpec divSpec = new IvParameterSpec(IV);
                dcipher.init(Cipher.DECRYPT_MODE, dkeySpec, divSpec);
                byte[] decryptedText = dcipher.doFinal(cipherText);
                //return new String(decryptedText);
                String str3 = Base64.getEncoder().encodeToString(decryptedText);
                System.out.println("decrypted Text : " + str3);
                String dresultString = tokens[0] + ":\"" + decryptedText + "\"}";
                System.out.println("Final Decrypted Text : " + dresultString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultString;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
            return "error";
        }

        /*
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);

        // Generate Key
        SecretKey key = keyGenerator.generateKey();
        byte[] IV = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);

        System.out.println("Original Text : " + text);

        byte[] cipherText = encrypt(text.getBytes(), key, IV);
        System.out.println("Encrypted Text : " + Base64.getEncoder().encodeToString(cipherText));

        String decryptedText = decrypt(cipherText, key, IV);
        System.out.println("DeCrypted Text : " + decryptedText);

        String encryptBody="";
        try {
            encryptBody = Cipher.getHmacSha256("KEY", body);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptBody;

       */
    }
    public static String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception
    {
        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        // Create GCMParameterSpec
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

        // Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

        // Perform Decryption
        byte[] decryptedText = cipher.doFinal(cipherText);

        return new String(decryptedText);
    }
    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }
}
