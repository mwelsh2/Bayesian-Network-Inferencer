package inference;

import java.util.ArrayList;
import java.util.List;

import core.Assignment;
import core.BayesianNetwork;
import core.Distribution;
import core.Inferencer;
import core.RandomVariable;
import core.Value;

/**
 * Implements the depth-first inference by enumeration algorithm for exact 
 * inference in Bayesian networks described in AIMA Figure 14.9.
 */
public class EnumerationInferencer implements Inferencer {

    public EnumerationInferencer() {}

    /**
     * Recursive (depth-first) step in the enumeration algorithm for answering 
     * queries on BayesianNetworks.
     */
    protected double enumerateAll(List<RandomVariable> variables, Assignment e, BayesianNetwork bn) {
        // Make copy of list of variables
        List<RandomVariable> vars = new ArrayList<RandomVariable>();
        vars.addAll(variables);
        
        if (vars.isEmpty()) {
            return 1.0;
        }
        
        RandomVariable Y = vars.remove(0);
        if (e.containsKey(Y)) {
            // P(y|parents(Y) * enumeration of all other variables
            return bn.getProbability(Y, e) * enumerateAll(vars, e, bn);
        } else {
            // Sigma(y) P(y|parents(Y)) * enumeration of all other variables
            double sum = 0;
            for (Value y: Y.getDomain()) {
                Assignment e_y = e.copy();
                e_y.put(Y, y);
                sum += bn.getProbability(Y, e_y) * enumerateAll(vars, e_y, bn);
            }
            return sum;
        }
    }

    /**
     * Return the posterior Distribution P(X|e) for RandomVariable X given 
     * Assignment e (evidence variables and their values) using the joint 
     * probability distribution encoded in the given BayesianNetwork.
     */
    public Distribution enumerateAsk(RandomVariable X, Assignment e, BayesianNetwork bn) {
        Distribution Xdist = new base.Distribution(X);
        
        for (Value xi: X.getDomain()) {
            // Extend evidence with X = xi
            Assignment e_xi = e.copy();
            e_xi.put(X, xi);
            // Update probability distribution, enumerating over all variables
            Xdist.put(xi, enumerateAll(bn.getVariablesSortedTopologically(), e_xi, bn));
        }

        Xdist.normalize();
        return Xdist;
    }    

    /**
     * Compute and return the Distribution of RandomVariable X given evidence e
     * using the distribution encoded by the given BayesianNetwork.
     */
    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return enumerateAsk(X, e, network);
    }

}