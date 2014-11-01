/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.util;

/**
 * Bregman Divergence
 * 
 * @author Hanke Chen
 * @version $Id: BregmanDivergence.java, v 0.1 2014-10-27 下午10:19:58 chench Exp $
 */
public final class BregmanDivergence {

    /** I-Divergence*/
    public final static int I_DIVERGENCE         = 501;

    /** Euclidean-Divergence*/
    public final static int EUCLIDEAN_DIVERGENCE = 502;

    /**
     * forbid construction
     */
    private BregmanDivergence() {

    }

    /**
     * compute the divergence
     * 
     * @param z1            the parameter to compute
     * @param z2            the parameter to compute
     * @param divergence    the divergence to compute
     * @return
     */
    public static double divergence(double z1, double z2, final int divergence) {
        switch (divergence) {
            case I_DIVERGENCE:
                return z1 * Math.log(z1 / z2) - (z1 - z2);
            case EUCLIDEAN_DIVERGENCE:
                return Math.pow(z1 - z2, 2.0);
            default:
                return 0.0;
        }
    }

}
