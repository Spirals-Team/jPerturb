package perturbation.log;

import perturbation.location.PerturbationLocation;

/**
 * Created by spirals on 28/03/16.
 */
public interface Logger {

    void logOn(PerturbationLocation location);

    void logCall(PerturbationLocation location);

    void logEnaction(PerturbationLocation location);

    int getCalls();

    int getEnactions();

    void reset();

}
