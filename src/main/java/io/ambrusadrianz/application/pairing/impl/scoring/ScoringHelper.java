package io.ambrusadrianz.application.pairing.impl.scoring;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class ScoringHelper {
    public static Double getLevenshteinDistanceNormalized(String a, String b) {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return 0d;
        }

        int maxInputLength = Math.max(a.length(), b.length());
        return ((maxInputLength - LevenshteinDistance.getDefaultInstance().apply(a, b)) * 1.0d / maxInputLength) * 10d;
    }
}
