package gs.ml.recommender;

/**
 * Recommender System 
 * 
 * @author Hanke
 * @version $Id: Recommender.java, v 0.1 2015-1-19 下午2:41:07 Exp $
 */
public interface Recommender {

    /**
     * Rating prediction of given user on item index.
     * 
     * @param u     The index of user to predict
     * @param i     The index of item to predict
     * @return      The prediction of given index
     */
    public double getPrediction(int u, int i);
}
