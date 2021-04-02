package inference;

import core.Assignment;
import core.BayesianNetwork;
import core.RandomVariable;

/**
 * Class implementing the Weighted-Sample algorithm from AIMA Section 14.5.1.
 */
public class WeightedSampler extends PriorSampler {

    /**
     * Inner class used to return a sample and its weight from a 
     * WeightedSampler.
     */
    class Sample {
        Assignment event;
        double weight;

        public Sample() {
            this.event = new base.Assignment();
            this.weight = 1.0;
        }
    }

    /**
     * Construct and return a new WeightedSampler for the given 
     * BayesianNetwork.
     */
    WeightedSampler(BayesianNetwork network) {
        super(network);
    }

    /**
     * Construct and return a new WeightedSampler for the given BayesianNetwork
     * using the given seed for the Random number generator.
     */
    WeightedSampler(BayesianNetwork network, long seed) {
        super(network, seed);
    }

    /**
     * Returns a Sample (event and weight) sampled from the distribution 
     * specified by the given BayesianNetwork, weighted by the likelihood of 
     * the values of the given evidence variables.
     */
    public Sample getSample(Assignment e) {
        Sample ws = new Sample();
        ws.event = e.copy();
        for (RandomVariable Xi: variables) {
            // if the evidence contains Xi
            if (e.containsKey(Xi)) {
                // Update weight accordingly
                ws.weight *= network.getProbability(Xi, ws.event);
            } else {
                // Add a random value for Xi to the assignment
                ws.event.put(Xi, randomSampleForVariable(Xi, ws.event));
            }
        }
        return ws;
    }

}