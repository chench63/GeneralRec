package paper.sigir15.experiment;

public class EnsembleEffectExperiment {
    /** file to store the original data and cocluster directory. 10M100K 1m*/
    public static String[]     rootDirs  = { "E:/MovieLens/ml-1m/1/" };
    /** The number of users. 943 6040 69878  480189*/
    public final static int    userCount = 6040;
    /** The number of items. 1682 3706 10677 17770*/
    public final static int    itemCount = 3706;
    public final static double maxValue  = 5.0d;
    public final static double minValue  = 1.0d;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //        double[] beta1s = new double[11];
        //        double[] beta2s = beta1s;
        //        for (int i = 0; i <= 10; i++) {
        //            beta1s[i] = i * 0.1;
        //        }
    }

    public static void cmp() {

    }

}
