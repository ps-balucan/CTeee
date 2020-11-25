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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    private static String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCutGgtBr00iIAEuH7pRpNhE3AjPkiaOvMhl5o8KeQ2nG8HiEYYUYi00vTeFACozb7rPjFM+bI6pdXt2EITeXWhse0eHS1u08G5eHlhmoyNK7rx009TlsAW55TRLtNRY2QyO6jV3VhEBv8mxl47MXMIlcr/yrRSLs9UkZWUIU0a0QIDAQAB";

    static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK60aC0GvTSIgAS4fulGk2ETcCM+SJo68yGXmjwp5DacbweIRhhRiLTS9N4UAKjNvus+MUz5sjql1e3YQhN5daGx7R4dLW7Twbl4eWGajI0ruvHTT1OWwBbnlNEu01FjZDI7qNXdWEQG/ybGXjsxcwiVyv/KtFIuz1SRlZQhTRrRAgMBAAECgYAC8RqhOfgjB6KkubTY4aFrEL5KWKLVNoWIMPbl2RR8wy33MfEwuQRA273H9M0lSogiytJ+k+SPemIHohTsQicD97PLLLyq2x35r/rSfWpnhkOew1LILokde5zjKmaxiO2qpZHhax5Nra5x6urGGEpqKcAcFLv3w+RCCbNeheaiYQJBANw70PXYpuhsbAs8BJa4nOX+hUNR/9c7JC8THFkG8eNSS2G2Wu2PqzthZLzIz+72kacKuortfLFeViljnJPcwLECQQDLE7swrQMB1J2ZbcucwOp6N06bbtWuuBvqg1pEwk+6BGaWFn9JVTT6CnHZZ3wmZn7eVV5h2fFupcUpe1ItjYQhAkEAzkHvJ/BqEQ9J+ROSsJ3hw70ljbEETM0EzFm3mOFaNS3jj3W7nvWIxqomMHBZ3VFrg+gWYPqeZ3cUdtrlz81VYQJAA68u9ToeVNkrps30JLrnRiJcvbssC0hx0TZi295+ncxYpB5yXr06AdNQr3YOLZAsexInbTUMCUreodsH6aWooQJAHUZlfcxlk+Qdx3D3oWDlgZRa5NFXhWknLxIQ5Ggb3VgSTrEIYmdaH1+TM4qnGikS/pI4rH9nJdLyAqZDgeMM+A==";
    static String encryptedTextString = "<your_received_encrypted_text_here>";
    static String encryptedSecretKeyString = "Ilt6eaVG8E6m6bpn0pl1DubDyjC42a8FypFbkZBAuHs8Iw2Lmk2aoHslzzb1mz0bjsYF7HDd32JMNokPoEBF+OwO1IgiUy412aB9lRWicCsek8FQP90FjyaJz5ipmTgweaOeShYg0UKTFilDYQrxGG0MY5oJP9fAUJlgfd9EPZE=";

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
            for (String t : tokens)
                System.out.println(t);
            String parsed = tokens[1].substring(1, tokens[1].length() - 2);

            //System.out.println("The username only: " + parsed);


            //STEP 1. GENERATE KEY
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            SecretKey secretKey = keyGenerator.generateKey();
            //secretKey = javax.crypto.spec.SecretKeySpec@178f0;

            /*
            //depracated hard coded key
            //Generate key using password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // iterationCount = 65536
            // keyLength = 256
            KeySpec spec = new PBEKeySpec(appname.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            //end of hard coded key
            */
            System.out.println("secret key: " + secretKey);

            /*RSA KEY GIVING THING
            try {

                // 1. generate public key and private key
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(1024); // key length
                KeyPair keyPair = keyPairGenerator.genKeyPair();
                String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
                String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

                // 2. print both keys
                System.out.println("rsa key pair generated\n");
                System.out.println("privateKey\n" + privateKeyString + "\n");
                System.out.println("publicKey\n" + publicKeyString + "\n\n");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            */
            //END OF RSA


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
            /*System.out.println("IV: " + strbruh);
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
            System.out.println("IV11: " + IV[11]);*/

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

            //String resultString = tokens[0] + ":\"" + str2 + "\",\"logs\":" + tokens[2];
            //System.out.println("Final Text : " + resultString);

            //Step 3.5. Encrypt the encryption key using RSA Public Key
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicSpec);

            Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
            String encryptedSecretKey = Base64.getEncoder().encodeToString(cipher2.doFinal(secretKey.getEncoded()));
            System.out.println("encrypted secret key : " + encryptedSecretKey);

            String resultString = tokens[0] + ":\"" + str2 + "\",\"logs\":" + encryptedSecretKey + "}";
            System.out.println("resultString : " + resultString);
            //STEP 4. DECRYPT
            try {

                // 1. Get private key
                PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
                KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory1.generatePrivate(privateSpec);

                // 2. Decrypt encrypted secret key using private key
                Cipher cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                cipher1.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] secretKeyBytes = cipher1.doFinal(Base64.getDecoder().decode(encryptedSecretKey));
                SecretKey secretKey1 = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");


                // 3. decrypt encrypted messaging using the just decrypted secret key
                Cipher dcipher = Cipher.getInstance("AES/GCM/NoPadding");
                SecretKeySpec dkeySpec = new SecretKeySpec(secretKey1.getEncoded(), "AES");
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

            return resultString;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
            System.err.println("I'm sorry, but MD5 is not a valid message digest algorithm");
            return "error";
        }

    }

}