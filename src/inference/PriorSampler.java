package inference;

import java.util.List;
import java.util.Random;

import core.Assignment;
import core.BayesianNetwork;
import core.RandomVariable;
import core.Value;

/**
 * Class implementing the Prior-Sample algorithm from AIMA Section 14.5.1.
 */
public class PriorSampler {

    /**
     * The BayesianNetwork being sampled by this PriorSampler.
     */
    protected BayesianNetwork network;
    /**
     * The random number generator used by this PriorSampler.
     */
    protected Random random;
    /**
     * The topologically-sorted list of RandomVariables from the 
     * BayesianNetwork being sampled by this PriorSampler.
     */
    protected List<RandomVariable> variables;

    /**
     * Construct and return a new PriorSampler for the given BayesianNetwork.
     */
    PriorSampler(BayesianNetwork network) {
        this.network = network;
        this.random = new Random();
        this.variables = network.getVariablesSortedTopologically();
    }

    /**
     * Construct and return a new PriorSampler for the given BayesianNetwork 
     * using the given seed for the Random number generator.
     */
    PriorSampler(BayesianNetwork network, long seed) {
        this.network = network;
        this.random = new Random(seed);
        this.variables = network.getVariablesSortedTopologically();
    }

    /**
     * Returns an Assignment (event) sampled from the prior specified by the 
     * given BayesianNetwork.
     */
    public Assignment getSample() {
        Assignment x = new base.Assignment();
        // Generate random value for each RandomVariable
        for (RandomVariable Xi: variables) {
            x.put(Xi, randomSampleForVariable(Xi, x));
        }
        return x;
    }

    /**
     * Return a random sample from P(Xi|Parents(Xi)), where the value is 
     * sampled according to the conditional distribution given the values 
     * already sampled for the variable's parents (in the given Assignment).
     */
    protected Value randomSampleForVariable(RandomVariable Xi, Assignment x) {
        double rand = random.nextDouble();
        double least = 0.0;
        double most = 0.0;
        // Use random number to select value for Xi, given the Assignment
        for (Value xi: Xi.getDomain()) {
            Assignment x_copy = x.copy();
            x_copy.put(Xi, xi);
            most = least + network.getProbability(Xi, x_copy);
            if (rand >= least && rand <= most) {
                return xi;
            }
            least = most;
        }
        return null;

    }


}