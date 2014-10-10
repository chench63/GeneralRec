/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.math.BigInteger;
import java.util.Random;

import edu.tongji.encryption.EncryptionContext;

/**
 * 类One-time pad的加密协议，加密域仅为实数。
 *  一次一密,非线程安全。
 * 
 * @author Hanke Chen
 * @version $Id: OneTimePadUtil.java, v 0.1 2013-12-17 上午10:23:10 chench Exp $
 */
public final class OneTimePadUtil {

    /** 大质数长度*/
    public static int        PRIME_LENGTH = 32;

    /** 总体密钥长度*/
    public static int        KEY_LENGTH   = 64;

    /** sensitiveEnc 加密密钥基数*/
    public static BigInteger BIG_PRIME    = BigInteger.probablePrime(PRIME_LENGTH, new Random());

    /** 模底数, 2^64*/
    public static BigInteger MODULUS_BASE = new BigInteger(
                                              "10000000000000000000000000000000000000000000000000000000000000000",
                                              2);

    /**
     * 禁用构造函数
     */
    private OneTimePadUtil() {

    }

    /**
     * 重新生成一个质数
     */
    public static void newPrime() {
        newPrime(PRIME_LENGTH);
    }

    /**
     * 重新生成一个质数
     * 
     * @param bitLens
     */
    public static void newPrime(int primeLens) {
        PRIME_LENGTH = primeLens;
        KEY_LENGTH = PRIME_LENGTH + 32;
        BIG_PRIME = BigInteger.probablePrime(PRIME_LENGTH, new Random());

        StringBuilder strBuilder = new StringBuilder("1");
        for (int i = KEY_LENGTH; i > 0; i--) {
            strBuilder.append(0);
        }
        MODULUS_BASE = new BigInteger(strBuilder.toString(), 2);
    }

    /**
     * 初始化质数和密钥.
     * 
     * @param primeLens
     * @param keyLens
     */
    public static void newPrime(int primeLens, int keyLens) {
        PRIME_LENGTH = primeLens;
        KEY_LENGTH = keyLens;
        BIG_PRIME = BigInteger.probablePrime(PRIME_LENGTH, new Random());

        StringBuilder strBuilder = new StringBuilder("1");
        for (int i = KEY_LENGTH; i > 0; i--) {
            strBuilder.append(0);
        }
        MODULUS_BASE = new BigInteger(strBuilder.toString(), 2);
    }

    /**
     * 纯加密，保护用户隐私
     * 
     * @param plaintext     明文信息
     * @return key          加密密钥
     */
    public static EncryptionContext secretEnc(BigInteger plaintext) {
        //拼接密钥
        BigInteger keyValue = genKey(plaintext, false);

        //执行加密
        plaintext = plaintext.add(keyValue);

        //装载结果
        EncryptionContext encryptionContext = new EncryptionContext(keyValue);
        encryptionContext.put("CIPHERTEXT", plaintext);
        return encryptionContext;
    }

    /**
     * 纯解密，保护用户隐私
     * 
     * @param ciphertext
     * @param encryptionContext
     * @return
     */
    public static BigInteger secretDec(BigInteger ciphertext, EncryptionContext encryptionContext) {
        return ciphertext.subtract(encryptionContext.getKeyValue());
    }

    /**
     * 对修改敏感的加密
     * 
     * @param plaintext
     * @return
     */
    public static EncryptionContext sensitiveEnc(BigInteger plaintext) {
        //拼接密钥
        BigInteger keyValue = genKey(plaintext, true);

        //执行加密
        BigInteger ciphertext = plaintext.multiply(BIG_PRIME);
        ciphertext = ciphertext.add(keyValue);

        //装载结果
        EncryptionContext encryptionContext = new EncryptionContext(keyValue);
        encryptionContext.put("CIPHERTEXT", ciphertext);
        return encryptionContext;
    }

    /**
     * 对修改敏感的解密，解密失败则返回ZERO
     * 
     * @param ciphertext
     * @param encryptionContext
     * @return
     */
    public static BigInteger sensitiveDec(BigInteger ciphertext, EncryptionContext encryptionContext) {
        //执行解密
        ciphertext = ciphertext.subtract(encryptionContext.getKeyValue());
        BigInteger[] plaintext = ciphertext.divideAndRemainder(BIG_PRIME);

        return plaintext[1].equals(BigInteger.ZERO) ? plaintext[0] : BigInteger.ZERO;

    }

    /**
     * 密鈅生成算法
     * 
     * @param plaintext
     * @param shouldGreatter
     * @return
     */
    private static BigInteger genKey(BigInteger plaintext, boolean shouldGreatter) {
        //默认质数32位，扰动的密钥为 32 + 32
        return RandomUtil.nextBigInteger(KEY_LENGTH);
    }

}
