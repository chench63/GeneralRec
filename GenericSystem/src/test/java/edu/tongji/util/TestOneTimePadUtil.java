/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.util;

import java.math.BigInteger;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.tongji.encryption.EncryptionContext;
import edu.tongji.log4j.LoggerDefineConstant;

/**
 * 
 * @author chenkh
 * @version $Id: TestOneTimePadUtil.java, v 0.1 2013-12-17 下午1:25:06 chenkh Exp $
 */
public class TestOneTimePadUtil {

    /** logger */
    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    @Test
    public void testSecretScheme() {
        BigInteger plaintext = new BigInteger("100");
        EncryptionContext encryptionContext = OneTimePadUtil.secretEnc(plaintext);
        BigInteger ciphertext = (BigInteger) encryptionContext.get("CIPHERTEXT");
        LoggerUtil.debug(logger,
            "Plaintext: " + plaintext.toString() + " Key: " + encryptionContext.getKeyValue()
                    + " Ciphertext: " + ciphertext.toString());

        plaintext = OneTimePadUtil.secretDec(ciphertext, encryptionContext);
        Assert.assertTrue(plaintext.equals(new BigInteger("100")));
    }

    @Test
    public void testSensitiveScheme() {
        BigInteger plaintext = new BigInteger("100");
        EncryptionContext encryptionContext = OneTimePadUtil.sensitiveEnc(plaintext);
        BigInteger ciphertext = (BigInteger) encryptionContext.get("CIPHERTEXT");
        LoggerUtil.debug(logger,
            "Plaintext: " + plaintext.toString() + " Key: " + encryptionContext.getKeyValue()
                    + " Ciphertext: " + ciphertext.toString());

        plaintext = OneTimePadUtil.sensitiveDec(ciphertext, encryptionContext);
        Assert.assertTrue(plaintext.equals(new BigInteger("100")));
    }

    @Test
    public void testMixture() {
        //Meter加密层
        BigInteger plaintext = new BigInteger("100");
        EncryptionContext providerContext = OneTimePadUtil.sensitiveEnc(plaintext);
        BigInteger cipherMeter = (BigInteger) providerContext.get("CIPHERTEXT");
        LoggerUtil.info(
            logger,
            "1.Meter Encrypted.Plaintext: " + plaintext.toString() + " Key: "
                    + providerContext.getKeyValue() + " Ciphertext: " + cipherMeter.toString());

        //Customer加密层
        EncryptionContext customerContext = OneTimePadUtil.secretEnc(cipherMeter);
        BigInteger cipherCustomer = (BigInteger) customerContext.get("CIPHERTEXT");
        BigInteger plainCustomer = OneTimePadUtil.secretDec(cipherCustomer, customerContext);
        Assert.assertTrue(cipherMeter.equals(plainCustomer));
        LoggerUtil.info(logger, "2.Customer Encrypted.Plaintext: " + cipherMeter.toString()
                                + " Key: " + customerContext.getKeyValue() + " Ciphertext: "
                                + cipherCustomer.toString());

        //Provider解密层
        plaintext = OneTimePadUtil.sensitiveDec(plainCustomer, providerContext);
        Assert.assertTrue(plaintext.equals(new BigInteger("100")));
    }

}
