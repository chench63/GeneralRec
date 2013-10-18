/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.homoEncr;

import java.math.BigInteger;

import org.junit.Test;
import org.springframework.util.StopWatch;

import edu.tongji.util.PaillierUtil;

/**
 * 
 * @author chenkh
 * @version $Id: TestHomoEncr.java, v 0.1 2013-10-10 下午5:22:16 chenkh Exp $
 */
public class TestHomoEncr {

    @Test
    public void test() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        /* instantiating an object of Paillier cryptosystem*/
        PaillierUtil paillier = new PaillierUtil();
        /* instantiating two plaintext msgs*/
        BigInteger m1 = new BigInteger("1");
        BigInteger m2 = new BigInteger("10000000000");
        /* encryption*/
        BigInteger em1 = paillier.Encryption(m1);
        BigInteger em2 = paillier.Encryption(m2);
        /* printout encrypted text*/
        System.out.println(em1);
        System.out.println(em2);
        /* printout decrypted text */
        System.out.println(paillier.Decryption(em1).toString());
        System.out.println(paillier.Decryption(em2).toString());

        /* test homomorphic properties -> D(E(m1)*E(m2) mod n^2) = (m1 + m2) mod n */
        BigInteger product_em1em2 = em1.multiply(em2).mod(paillier.nsquare);
        BigInteger sum_m1m2 = m1.add(m2).mod(paillier.n);
        System.out.println("original sum: " + sum_m1m2.toString());
        System.out.println("decrypted sum: " + paillier.Decryption(product_em1em2).toString());

        /* test homomorphic properties -> D(E(m1)^m2 mod n^2) = (m1*m2) mod n */
        BigInteger expo_em1m2 = em1.modPow(m2, paillier.nsquare);
        BigInteger prod_m1m2 = m1.multiply(m2).mod(paillier.n);
        System.out.println("original product: " + prod_m1m2.toString());
        System.out.println("decrypted product: " + paillier.Decryption(expo_em1m2).toString());

        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());

    }

    @Test
    public void test2() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        /* instantiating two plaintext msgs*/
        BigInteger m1 = new BigInteger("2");
        BigInteger m2 = new BigInteger("10");
        /* encryption*/
        BigInteger em1 = PaillierUtil.encryptions(m1);
        BigInteger em2 = PaillierUtil.encryptions(m2);
        /* printout decrypted text */
        BigInteger sum = PaillierUtil.add(em1, em2);
        BigInteger multiply = PaillierUtil.multiply(em1, BigInteger.valueOf(2));
        System.out.println(PaillierUtil.decryptions(sum).toString());
        System.out.println(PaillierUtil.decryptions(multiply).toString());

        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());

    }

}
