/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.homomorphicEncryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.StopWatch;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.PaillierUtil;

/**
 * 
 * @author chenkh
 * @version $Id: TestHomoEncr.java, v 0.1 2013-10-10 下午5:22:16 chenkh Exp $
 */
public class TestPaillierUtil {

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    //    @Test
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
        /* instantiating two plaintext msgs*/
        BigInteger m1 = new BigInteger("2568000");
        List<BigInteger> additions = new ArrayList<BigInteger>();

        /* encryption*/
        BigInteger em1 = PaillierUtil.encryptions(m1);
        //every 10 numbers as a column have the same complexity
        Random ran = new Random();
        ran.setSeed(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {
            int addition = Double.valueOf(ran.nextDouble() * Math.pow(2, i / 10 + 1)).intValue();
            additions.add(PaillierUtil.encryptions(BigInteger.valueOf(addition)));
        }

        int arrays = 10;
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < arrays; i++) {
            stopWatch.start();
            for (int j = 0; j < 10000; j++) {
                for (int k = 0; k < 10; k++) {
                    PaillierUtil.add(em1, additions.get( 990 + k));
                }
            }
            stopWatch.stop();
            LoggerUtil.info(logger,
                "Array: " + i + "  elapse: " + stopWatch.getLastTaskTimeMillis());
        }
        LoggerUtil.info(logger, "Avg: " + stopWatch.getTotalTimeMillis() / (arrays * 1.0));

    }
    
    
//    @Test
//    public void test3(){
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        int sum = 5;
//        for(int i = 0; i < 10000; i++){
//            sum += 5;
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.getLastTaskTimeMillis());
//    } 
    

}
