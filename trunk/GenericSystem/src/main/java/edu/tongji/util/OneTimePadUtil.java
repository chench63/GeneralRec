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
 * @author chenkh
 * @version $Id: OneTimePadUtil.java, v 0.1 2013-12-17 上午10:23:10 chenkh Exp $
 */
public final class OneTimePadUtil {

    private static BigInteger BIG_PRIME = BigInteger.probablePrime(32, new Random());

    /**
     * 禁用构造函数
     */
    private OneTimePadUtil() {

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
     * @return
     */
    private static BigInteger genKey(BigInteger plaintext, boolean shouldGreatter) {
        //拼接密钥
        String keyValue = ((Integer) RandomUtil.nextInt(-10, 10)).toString();
        if (!shouldGreatter) {
            return plaintext.multiply(new BigInteger(keyValue));
        }

        return plaintext.multiply(new BigInteger(keyValue)).multiply(BIG_PRIME);
    }

}
