package edu.tongji.util;

import java.math.BigInteger;
import java.util.Random;

/**
 * 同态加密工具类,非线程安全。
 * 
 * @author unknown
 * @version $Id: PaillierUtil.java, v 0.1 2013-10-12 下午2:19:43 chenkh Exp $
 */
public final class PaillierUtil {
    /**
     * p and q are two large primes.
     * lambda = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1).
     */
    private BigInteger          p, q, lambda;
    /**
     * n = p*q, where p and q are two large primes.
     */
    public BigInteger           n;
    /**
     * nsquare = n*n
     */
    public BigInteger           nsquare;
    /**
     * a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1.
     */
    private BigInteger          g;
    /**
     * number of bits of modulus
     */
    private int                 bitLength;

    /**
     * singleton instance
     */
    private static PaillierUtil singleton = PaillierUtil.newInstance();

    /**
     * 获取工具实例
     * 
     * @return
     */
    public static PaillierUtil newInstance() {
        return new PaillierUtil();
    }

    /**
     * Constructs an instance of the Paillier cryptosystem.
     * @param bitLengthVal number of bits of modulus
     * @param certainty The probability that the new BigInteger represents a prime number will exceed (1 - 2^(-certainty)). The execution time of this constructor is proportional to the value of this parameter.
     */
    public PaillierUtil(int bitLengthVal, int certainty) {
        KeyGeneration(bitLengthVal, certainty);
    }

    /**
     * Constructs an instance of the Paillier cryptosystem with 512 bits of modulus and at least 1-2^(-64) certainty of primes generation.
     */
    public PaillierUtil() {
        KeyGeneration(512, 64);
    }

    /**
     * Sets up the public key and private key.
     * @param bitLengthVal number of bits of modulus.
     * @param certainty The probability that the new BigInteger represents a prime number will exceed (1 - 2^(-certainty)). The execution time of this constructor is proportional to the value of this parameter.
     */
    private void KeyGeneration(int bitLengthVal, int certainty) {
        bitLength = bitLengthVal;
        /*Constructs two randomly generated positive BigIntegers that are probably prime, with the specified bitLength and certainty.*/
        p = new BigInteger(bitLength / 2, certainty, new Random());
        q = new BigInteger(bitLength / 2, certainty, new Random());

        n = p.multiply(q);
        nsquare = n.multiply(n);

        g = new BigInteger("2");
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
            .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));
        /* check whether g is good.*/
        if (g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
            System.out.println("g is not good. Choose g again.");
            System.exit(1);
        }
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function explicitly requires random input r to help with encryption.
     * @param m plaintext as a BigInteger
     * @param r random plaintext to help with encryption
     * @return ciphertext as a BigInteger
     */
    public static BigInteger Encryptions(BigInteger m, BigInteger r) {
        return singleton.Encryption(m, r);
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function automatically generates random input r (to help with encryption).
     * @param m plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public static BigInteger encryptions(BigInteger m) {
        return singleton.Encryption(m);
    }

    /**
     * Decrypts ciphertext c. plaintext m = L(c^lambda mod n^2) * u mod n, where u = (L(g^lambda mod n^2))^(-1) mod n.
     * @param c ciphertext as a BigInteger
     * @return plaintext as a BigInteger
     */
    public static BigInteger decryptions(BigInteger c) {
        return singleton.Decryption(c);
    }

    /**
     * Addition Operator
     * 
     * @param orig          密文形式.cipherText
     * @param addtion       密文形式.cipherText
     * @return
     */
    public static BigInteger add(BigInteger orig, BigInteger addition) {
        return orig.multiply(addition).mod(singleton.nsquare);
    }

    /**
     * Multiply Operator
     * 
     * @param orig         密文形式.cipherText 
     * @param addtion      明文形式.plaintext
     * @return
     */
    public static BigInteger multiply(BigInteger orig, BigInteger muitiplier) {
        return orig.modPow(muitiplier, singleton.nsquare);
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function explicitly requires random input r to help with encryption.
     * @param m plaintext as a BigInteger
     * @param r random plaintext to help with encryption
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m, BigInteger r) {
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }

    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function automatically generates random input r (to help with encryption).
     * @param m plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);

    }

    /**
     * Decrypts ciphertext c. plaintext m = L(c^lambda mod n^2) * u mod n, where u = (L(g^lambda mod n^2))^(-1) mod n.
     * @param c ciphertext as a BigInteger
     * @return plaintext as a BigInteger
     */
    public BigInteger Decryption(BigInteger c) {
        BigInteger u = g.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }

    //	/**
    //	 * main function
    //	 * @param str intput string
    //	 */
    //	public static void main(String[] str) {
    //		/* instantiating an object of Paillier cryptosystem*/
    //		PaillierUtil paillier = new PaillierUtil();
    //		/* instantiating two plaintext msgs*/
    //		BigInteger m1 = new BigInteger("1");
    //		BigInteger m2 = new BigInteger("1000");
    //		/* encryption*/
    //		BigInteger em1 = paillier.Encryption(m1);
    //		BigInteger em2 = paillier.Encryption(m2);
    //		/* printout encrypted text*/
    //		System.out.println(em1);
    //		System.out.println(em2);
    //		/* printout decrypted text */
    //		System.out.println(paillier.Decryption(em1).toString());
    //		System.out.println(paillier.Decryption(em2).toString());
    //
    //		/* test homomorphic properties -> D(E(m1)*E(m2) mod n^2) = (m1 + m2) mod n */
    //		BigInteger product_em1em2 = em1.multiply(em2).mod(paillier.nsquare);
    //		BigInteger sum_m1m2 = m1.add(m2).mod(paillier.n);
    //		System.out.println("original sum: " + sum_m1m2.toString());
    //		System.out.println("decrypted sum: " + paillier.Decryption(product_em1em2).toString());
    //
    //		/* test homomorphic properties -> D(E(m1)^m2 mod n^2) = (m1*m2) mod n */
    //		BigInteger expo_em1m2 = em1.modPow(m2, paillier.nsquare);
    //		BigInteger prod_m1m2 = m1.multiply(m2).mod(paillier.n);
    //		System.out.println("original product: " + prod_m1m2.toString());
    //		System.out.println("decrypted product: " + paillier.Decryption(expo_em1m2).toString());
    //
    //	}

}
