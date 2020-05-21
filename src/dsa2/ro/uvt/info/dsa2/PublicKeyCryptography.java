package ro.uvt.info.dsa2;

import java.security.*;
import java.security.cert.*;
import java.util.Scanner;
import javax.crypto.*;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Crypto Lab
 *
 * 1. Encrypt the data using a Symmetric Key 2. Encrypt the Symmetric key using
 * the Receivers public key 3. Create a Message Digest of the data to be
 * transmitted 4. Sign the message to be transmitted 5. Send the data over to an
 * unsecured channel 6. Validate the Signature 7. Decrypt the message using
 * receiver's private Key to get the Symmetric Key 8. Decrypt the data using the
 * Symmetric Key 9. Compute MessageDigest of data + Signed message 10.Validate
 * if the Message Digest of the Decrypted Text matches the Message Digest of the
 * Original Message
 *
 *
 */
public class PublicKeyCryptography {

    // utility method to convert a byte array into a hexadecimal String
    final protected static char[] hexArray = "0123456789ABCDEF ".toCharArray();

    public static String bytesToHex(byte[] bytes, int maxLen) {
        int bytesLen = bytes.length;
        if (bytesLen > maxLen) {
            bytesLen = maxLen;
        }

        char[] hexChars = new char[bytesLen * 3];
        for (int j = 0; j < bytesLen; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = hexArray[16];
        }
        return new String(hexChars);
    }

    public String run(byte[] byteClearText, String signatureCombo, boolean show) {
        return mainDSS(byteClearText, signatureCombo, show);
    }

    public String mainDSS(byte[] clearMessage, String signatureCombo, boolean show) {

        StringBuilder info = new StringBuilder();

        // SymmetricEncrypt is a programmer defined java class
        SymmetricEncrypt encryptUtil = new SymmetricEncrypt();

        // Generating a SecretKey for Symmetric Encryption
        SecretKey senderSecretKey = SymmetricEncrypt.getSecret();

        // 1. Encrypt the data using a Symmetric Key and the AES algorithm
        byte[] byteCipherText = encryptUtil.encryptData(clearMessage, senderSecretKey, "AES");

        // BASE64 Character encoder as specified in RFC1521 - not used
        String strCipherText = new BASE64Encoder().encode(byteCipherText);

        // 2. Encrypt the Symmetric key using the Receivers public key
        try {
            // 2.1 Specify the Keystore where the Receivers certificate has been imported
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // this is the keystore level password
            char[] password = "dsa2011".toCharArray();
            java.io.FileInputStream fis = new java.io.FileInputStream(System.getProperty("user.dir") + "\\src\\dsa2\\ro\\uvt\\info\\dsa2\\resources" + "\\dsa2.jks");
            ks.load(fis, password);
            fis.close();

            // 2.2 Creating an X509 Certificate of the Receiver
            X509Certificate recvcert;
            recvcert = (X509Certificate) ks.getCertificate("testrecv");

            // 2.3 Getting the Receivers public Key from the Certificate
            PublicKey pubKeyReceiver = recvcert.getPublicKey();

            // 2.4 Encrypting the SecretKey with the Receivers public Key
            byte[] byteEncryptWithPublicKey = encryptUtil.encryptData(senderSecretKey.getEncoded(), pubKeyReceiver, "RSA/ECB/PKCS1Padding");
            String strSenbyteEncryptWithPublicKey = new BASE64Encoder().encode(byteEncryptWithPublicKey);

            // 3. Create a Message Digest of the Data to be transmitted
            MessageDigest md = MessageDigest.getInstance("SHA1");

            // it is the digest of the clear text
            md.update(clearMessage);
            byte byteMDofDataToTransmit[] = md.digest();
            String strMDofDataToTransmit = new String();

            for (int i = 0; i < byteMDofDataToTransmit.length; i++) {
                strMDofDataToTransmit = strMDofDataToTransmit + Integer.toHexString((int) byteMDofDataToTransmit[i] & 0xFF);
            }

            // 3.1 Message to be Signed = Encrypted Secret Key + digest of the data to be transmitted
            String strMsgToSign = strSenbyteEncryptWithPublicKey + "|" + strMDofDataToTransmit;

            // 4. Sign the message
            // 4.1 Get the private key of the Sender from the keystore by providing
            // the password set for the private key while creating the keys using keytool
            char[] keypassword = "smihalas".toCharArray();
            Key myKey = ks.getKey("smihalas", keypassword);
            PrivateKey myPrivateKey = (PrivateKey) myKey;

            // 4.2 Sign the message
            Signature mySign = Signature.getInstance(signatureCombo);

            mySign.initSign(myPrivateKey);
            mySign.update(strMsgToSign.getBytes());
            byte[] byteSignedData = mySign.sign();

            // display the signature in hexa
            info.append(" Digital signature of length: ").append(byteSignedData.length).append("\n");
            info.append(" Digital signature in HEXA: ").append(bytesToHex(byteSignedData, 40)).append("\n");

            // 5. The Values byteSignedData (the signature) and strMsgToSign
            // (the data which was signed) can be sent across to the receiver
            // 6.Validate the Signature
            // 6.1 Extracting the Senders public Key from his certificate
            X509Certificate sendercert;
            sendercert = (X509Certificate) ks.getCertificate("smihalas");
            PublicKey pubKeySender = sendercert.getPublicKey();

            // 6.2 Verifying the Signature
            Signature myVerifySign = Signature.getInstance(signatureCombo);
            myVerifySign.initVerify(pubKeySender);
            myVerifySign.update(strMsgToSign.getBytes());
            boolean verifySign = myVerifySign.verify(byteSignedData);

            if (verifySign == false) {
                info.append(" Error in validating Signature: ").append(signatureCombo).append("\n");
            } else {
                info.append(" Successfully validated Signature: ").append(signatureCombo).append("\n");
            }

            // 7. Decrypt the message using Recv private Key to get the Symmetric Key
            char[] recvpassword = "testrecv".toCharArray();
            Key recvKey = ks.getKey("testrecv", recvpassword);
            PrivateKey recvPrivateKey = (PrivateKey) recvKey;

            // Parsing the MessageDigest and the encrypted value
            String strRecvSignedData = new String(byteSignedData);
            String[] strRecvSignedDataArray = new String[10];
            strRecvSignedDataArray = strMsgToSign.split("|");
            int intindexofsep = strMsgToSign.indexOf("|");
            String strEncryptWithPublicKey = strMsgToSign.substring(0, intindexofsep);
            String strHashOfData = strMsgToSign.substring(intindexofsep + 1);

            // Decrypting to get the symmetric key
            byte[] bytestrEncryptWithPublicKey = new BASE64Decoder().decodeBuffer(strEncryptWithPublicKey);
            byte[] byteDecryptWithPrivateKey = encryptUtil.decryptData(byteEncryptWithPublicKey, recvPrivateKey, "RSA/ECB/PKCS1Padding");

            // 8. Decrypt the data using the Symmetric Key
            javax.crypto.spec.SecretKeySpec secretKeySpecDecrypted = new javax.crypto.spec.SecretKeySpec(byteDecryptWithPrivateKey, "AES");
            byte[] byteDecryptText = encryptUtil.decryptData(byteCipherText, secretKeySpecDecrypted, "AES");
            String strDecryptedText = new String(byteDecryptText);
            info.append(" Decrypted data in HEXA: ").append(bytesToHex(byteDecryptText, 40)).append(" \n");
            if (show) {
                info.append(" Decrypted data is ").append(strDecryptedText).append(" \n");
            }
            // 9. Compute MessageDigest of data + Signed message
            MessageDigest recvmd = MessageDigest.getInstance("SHA1");
            recvmd.update(byteDecryptText);
            byte byteHashOfRecvSignedData[] = recvmd.digest();

            String strHashOfRecvSignedData = new String();

            for (int i = 0; i < byteHashOfRecvSignedData.length; i++) {
                strHashOfRecvSignedData = strHashOfRecvSignedData + Integer.toHexString((int) byteHashOfRecvSignedData[i] & 0xFF);
            }
            // 10. Validate if the Message Digest of the Decrypted Text matches the Message Digest of the Original Message
            if (!strHashOfRecvSignedData.equals(strHashOfData)) {
                info.append(" Message has been tampered \n");
            }

        } catch (Exception exp) {
            info.append(" Exception caught ").append(exp).append("\n");
        }

        return info.toString();
    }
}
