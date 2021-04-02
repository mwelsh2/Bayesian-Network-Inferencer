package inference;

import java.util.Random;

import core.Assignment;
import core.BayesianNetwork;
import core.Distribution;
import core.RandomVariable;
import core.Value;

/**
 * Class implementing the method for sampling from a variable's Markov blanket,
 * as described in AIMA 14.5.2.
 */
public class MarkovBlanketSampler {

    /**
     * The BayesianNetwork being sampled by this MarkovBlanketSampler.
     */
    protected BayesianNetwork network;
    /**
     * The random number generator used by this MarkovBlanketSampler.
     */
    protected Random random;

    /**
     * Construct and return a new MarkovBlanketSampler for the given
     * BayesianNetwork.
     */
    public MarkovBlanketSampler(BayesianNetwork network) {
        this.network = network;
        this.random = new Random();
    }

    /**
     * Construct and return a new MarkovBlanketSampler for the given BayesianNetwork
     * using the given seed for the Random number generator.
     */

    public MarkovBlanketSampler(BayesianNetwork network, long seed) {
        this.network = network;
        this.random = new Random();
    }

    /**
     * Choose and set the value for for variable Xi in xvec by sampling from
     * P(Xi|mb(Xi)) where ``mb(Xi)'' refers to the Markov blanket of Xi and current
     * state of the network is xvec.
     */
    protected void sample(RandomVariable Xi, Assignment xvec) {

        Distribution dist = new base.Distribution(Xi);

        for (Value xi : Xi.getDomain()) {
            Assignment x_copy = xvec.copy();
            x_copy.put(Xi, xi);
            double p = network.getProbability(Xi, x_copy); // P(Xi|parents(Xi))
            // Yj in Children(Xi)
            for (RandomVariable child : network.getChildren(Xi)) {
                p *= network.getProbability(child, x_copy); // P(Yj|parents(Yj))
            }
            dist.put(xi, p);
        }
        dist.normalize();
        
        // Use random number to select a value for Xi, given the new distribution
        double rand = random.nextDouble();
        double least = 0.0;
        double most = 0.0;
        for (Value xi : dist.keySet()) {
            most = least + dist.get(xi);
            if (rand >= least && rand <= most) {
                // Update the assignment
                xvec.put(Xi, xi);
                return;
            }
            least = most;
        }
    }

}