package com.net.utils;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

public class TextEncrypter {

	public static final String DESEDE_ENCRYPTION_ALGORITHM = "DESEDE";
	public static final String DES_ENCRYPTION_ALGORITHM = "DES";
	public static final String DEFAULT_KEY = "abhjsdfkfh3j4jd56k3j2gmn7k42knfd6mjki5390";
	private KeySpec keySpec;
	private SecretKeyFactory keyFactory;
	private Cipher cipher;

	public TextEncrypter(String paramString) throws Exception {
		this(paramString, DEFAULT_KEY);
	}

	public TextEncrypter(String paramString1, String paramString2) throws Exception {
		try {
			byte[] arrayOfByte = paramString2.getBytes();
			if (paramString1.equals("DESEDE")) {
				this.keySpec = new DESedeKeySpec(arrayOfByte);
			} else if (paramString1.equals("DES")) {
				this.keySpec = new DESKeySpec(arrayOfByte);
			} else {
				throw new IllegalArgumentException("Encryption Algorithm not supported: " + paramString1);
			}
			this.keyFactory = SecretKeyFactory.getInstance(paramString1);
			this.cipher = Cipher.getInstance(paramString1);
		} catch (Exception localException) {
			throw localException;
		}
	}

	public String encrypt(String paramString) throws Exception {
		if ((paramString == null) || (paramString.trim().length() == 0)) {
			throw new IllegalArgumentException("unencrypted string was null or empty");
		}
		try {
			SecretKey localSecretKey = this.keyFactory.generateSecret(this.keySpec);
			this.cipher.init(Cipher.ENCRYPT_MODE, localSecretKey);
			byte[] arrayOfByte = this.cipher.doFinal(paramString.getBytes());
			Base64 localBASE64Encoder = new Base64();
			return new String(localBASE64Encoder.encode(arrayOfByte));
		} catch (Exception localException) {
			throw localException;
		}
	}

	public String decrypt(String paramString) throws Exception {
		if ((paramString == null) || (paramString.trim().length() <= 0)) {
			throw new IllegalArgumentException("encrypted string was null or empty");
		}
		try {
			SecretKey localSecretKey = this.keyFactory.generateSecret(this.keySpec);
			this.cipher.init(Cipher.DECRYPT_MODE, localSecretKey);
			Base64 localBASE64Decoder = new Base64();
			byte[] arrayOfByte1 = localBASE64Decoder.decode(paramString);
			byte[] arrayOfByte2 = this.cipher.doFinal(arrayOfByte1);
			return new String(arrayOfByte2);
		} catch (Exception localException) {
			throw localException;
		}
	}

	public static void main(String[] paramArrayOfString) {
		
		String str = "4sowwAnt2fY=";
		try {
			TextEncrypter localTextEncrypter = new TextEncrypter(DES_ENCRYPTION_ALGORITHM);
			str = localTextEncrypter.encrypt("123456");
			System.out.println(generateKey() + " Encrypted Text :: " + str + "---");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			TextEncrypter localTextEncrypter = new TextEncrypter(DES_ENCRYPTION_ALGORITHM);
			str = localTextEncrypter.decrypt(str);
			System.out.println(generateKey() + " Encrypted Text :: " + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String generateKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		SecretKey localSecretKey = (SecretKey) generateRawKey();
		byte[] arrayOfByte = localSecretKey.getEncoded();
		return new String(arrayOfByte);
	}

	private static Key generateRawKey() throws NoSuchAlgorithmException {
		KeyGenerator localKeyGenerator = KeyGenerator.getInstance(DESEDE_ENCRYPTION_ALGORITHM);
		SecretKey localSecretKey = localKeyGenerator.generateKey();
		return localSecretKey;
	}

}
