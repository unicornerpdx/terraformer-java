package com.esri.terraformer.core;

import java.util.Arrays;
import java.util.Collection;

public final class FeatureCollection extends BaseGeometry<Feature> {
    public static final String ERROR_PREFIX = "Error while parsing FeatureCollection: ";

    /**
     * A valid FeatureCollection contains 0 or more non-null {@link Feature}'s.
     *
     * @param features
     */
    public FeatureCollection(Feature... features) {
        addAll(Arrays.asList(features));
    }

    public FeatureCollection(int initialCapacity) {
        super(initialCapacity);
    }

    public FeatureCollection(Collection<Feature> c) {
        super(c);
    }

    @Override
    public GeometryType getType() {
        return GeometryType.FEATURECOLLECTION;
    }

    @Override
    public boolean isValid() {
        for (Feature feat : this) {
            if (feat == null || !feat.isValid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        FeatureCollection other;
        try {
            other = (FeatureCollection) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return featureCollectionContainsOther(this, other) && featureCollectionContainsOther(other, this);
    }

    static boolean featureCollectionContainsOther(FeatureCollection fc1, FeatureCollection fc2) {
        for (Feature feat : fc1) {
            if (feat == null) {
                continue;
            }

            boolean success = false;

            for (Feature otherFeat : fc2) {
                if (feat.isEquivalentTo(otherFeat)) {
                    success = true;
                    break;
                }
            }

            if (!success) {
                return false;
            }
        }

        return true;
    }
}
