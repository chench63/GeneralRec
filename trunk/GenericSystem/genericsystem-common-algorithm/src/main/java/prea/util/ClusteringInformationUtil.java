package prea.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.tongji.data.BlockMatrix;

/**
 * 
 * @author Hanke
 * @version $Id: ClusteringInformationUtil.java, v 0.1 2015-5-7 上午10:16:38 Exp $
 */
public final class ClusteringInformationUtil {

    /**
     * forbid construction
     */
    private ClusteringInformationUtil() {

    }

    /**
     * read the mapping between the uId (iId) and uClustering(iClustering)Id 
     * 
     * @param ua            user assignment of the clustering. [userId] : [user clustering id]
     * @param ia            item assignment of the clustering. [itemId] : [item clustering id]
     * @param clusterDir    identified directory of clustering configuration files  
     * @param rootDir       root directory of of clustering configuration files  
     * @return
     */
    public static int[] readBiAssigmnt(int[] ua, int[] ia, String clusterDir, String rootDir) {
        String settingFile = rootDir + clusterDir + "SETTING";
        String rowMappingFile = rootDir + clusterDir + "RM";
        String colMappingFile = rootDir + clusterDir + "CM";

        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix blockMatrix = MatrixFileUtil.readEmptyBlock(settingFile, rowMappingFile,
            colMappingFile, rowAssig, colAssig);
        int[] biSize = new int[2];

        if (ua != null) {
            int[] userClusterBounds = blockMatrix.rowBound();
            for (Entry<Integer, Integer> uIndex : rowAssig.entrySet()) {
                for (int i = 0; i < userClusterBounds.length; i++) {
                    if (uIndex.getValue() < userClusterBounds[i]) {
                        ua[uIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[0] = userClusterBounds.length;
        }

        if (ia != null) {
            int[] itemClusterBounds = blockMatrix.structure()[0];
            for (Entry<Integer, Integer> iIndex : colAssig.entrySet()) {
                for (int i = 0; i < itemClusterBounds.length; i++) {
                    if (iIndex.getValue() < itemClusterBounds[i]) {
                        ia[iIndex.getKey()] = i;
                        break;
                    }
                }
            }
            biSize[1] = itemClusterBounds.length;
        }

        return biSize;
    }
}
