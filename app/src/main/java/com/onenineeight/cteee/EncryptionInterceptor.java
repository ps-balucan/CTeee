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
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionInterceptor implements Interceptor {
    private static final String TAG = EncryptionInterceptor.class.getSimpleName();
    private static final boolean DEBUG = true;
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private static String appname = "Pr1v4Tr4c3#!";
    private static String salt = "pepper is better";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody oldBody = request.body();
        Buffer buffer = new Buffer();
        oldBody.writeTo(buffer);
        System.out.println("Original Text : " + buffer);
        String strOldBody = buffer.readUtf8();
        System.out.println("Original Text : " + strOldBody);
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        String strNewBody = pencrypt(strOldBody);
        //String strNewBody = strOldBody;
        RequestBody body = RequestBody.create(mediaType, strNewBody);
        request = request.newBuilder().header("Content-Type", body.contentType().toString()).header("Content-Length", String.valueOf(body.contentLength())).method(request.method(), body).build();

        return chain.proceed(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String pencrypt(String text) {

        try {
            //original text print
            System.out.println("Original Text : " + text);
            String[] tokens = text.split(":");

            //parsing the username only
            //for (String t : tokens)
               // System.out.println(t);
            String parsed = tokens[1].substring(1, tokens[1].length() - 2);

            //System.out.println("The username only: " + parsed);


            //STEP 1. GENERATE KEY
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            SecretKey secretKey1 = keyGenerator.generateKey();
            //secretKey = javax.crypto.spec.SecretKeySpec@178f0;

            //Generate key using password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // iterationCount = 65536
            // keyLength = 256
            KeySpec spec = new PBEKeySpec(appname.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            System.out.println("secret key: " + secretKey);


            byte[] IV = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(IV);
            String strbruh = new String(IV, StandardCharsets.UTF_8);
            IV[0] = -114;
            IV[1] = 123;
            IV[2] = 40;
            IV[3] = 30;
            IV[4] = -88;
            IV[5] = 69;
            IV[6] = 39;
            IV[7] = 118;
            IV[8] = 62;
            IV[9] = -33;
            IV[10] = -17;
            IV[11] = 51;
            System.out.println("IV: " + strbruh);
            System.out.println("IV: " + IV);
            System.out.println("IV0: " + IV[0]);
            System.out.println("IV1: " + IV[1]);
            System.out.println("IV2: " + IV[2]);
            System.out.println("IV3: " + IV[3]);
            System.out.println("IV4: " + IV[4]);
            System.out.println("IV5: " + IV[5]);
            System.out.println("IV6: " + IV[6]);
            System.out.println("IV7: " + IV[7]);
            System.out.println("IV8: " + IV[8]);
            System.out.println("IV9: " + IV[9]);
            System.out.println("IV10: " + IV[10]);
            System.out.println("IV11: " + IV[11]);

            byte[] plaintext = text.getBytes();
            //System.out.println("The username only: " + plaintext);

            //STEP 2. ENCRYPT
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            System.out.println("key: " + secretKey);
            //System.out.println("keyspec: " + keySpec);
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
            //System.out.println("gcmspec: " + gcmParameterSpec);
            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] cipherText = cipher.doFinal(plaintext);
            //return cipherText;

            //Step 3. Print encrypted text for validation
            String str2 = Base64.getEncoder().encodeToString(cipherText);
            System.out.println("Encrypted Text : " + str2);
            String resultString = tokens[0] + ":\"" + cipherText + "\"}";
           //System.out.println("Final Text : " + resultString);

            //STEP 4. DECRYPT
            try {
                Cipher dcipher = Cipher.getInstance("AES/GCM/NoPadding");
                SecretKeySpec dkeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
                GCMParameterSpec dgcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
                IvParameterSpec divSpec = new IvParameterSpec(IV);
                dcipher.init(Cipher.DECRYPT_MODE, dkeySpec, dgcmParameterSpec);
                byte[] decryptedText = dcipher.doFinal(cipherText);
                //return new String(decryptedText);
                String str3 = new String(decryptedText);
                //String str3 = Base64.getEncoder().encodeToString(decryptedText);
                System.out.println("decrypted Text : " + str3);
                //String dresultString = tokens[0] + ":\"" + decryptedText + "\"}";
                //System.out.println("Final Decrypted Text : " + dresultString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return str2;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
            return "error";
        }

    }

}