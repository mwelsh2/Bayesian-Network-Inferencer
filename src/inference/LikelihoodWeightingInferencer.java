package inference;

import core.Assignment;
import core.BayesianNetwork;
import core.Distribution;
import core.Inferencer;
import core.RandomVariable;
import core.Value;
import inference.WeightedSampler.Sample;

/**
 * Implements the likelihood weighting algorithm for approximate inference in 
 * Bayesian networks described in AIMA Figure 14.15.
 */
public class LikelihoodWeightingInferencer implements Inferencer {

    public LikelihoodWeightingInferencer(){}

    /**
     * Return an estimate of the Distribution P(X|e) from the given 
     * BayesianNetwork, using the default number of samples.
     */
    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return query(X, e, network, 5000);
    }

    /**
     * Return an estimate of the Distribution P(X|e) from the given 
     * BayesianNetwork, where X is the query RandomVariable, e is an Assignment
     * representing the evidence variables and their values, and n is the total
     * number of samples to be generated.
     */
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network, int n) {
        Distribution Xdist = new base.Distribution(X);
        for (Value v: X.getDomain()) {
            Xdist.put(v, 0.0);
        }

        for (int i = 0; i < n; i++) {
            
            // Generate weighted sample
            WeightedSampler ws = new WeightedSampler(network);
            Sample s = ws.getSample(e);
            
            // Update probability distribution accordingly
            Value x = s.event.get(X);
            Xdist.set(x, Xdist.get(x) + s.weight);
        }
        
        int i = 0; 
        System.out.print("\nW: <");
        for (Value v: X.getDomain()) {
            i++;
            System.out.printf("%.2f", Xdist.get(v));
            if (i == X.getDomain().size()) {
                System.out.println(">");
            } else {
                System.out.print(", ");
            }
        }

        i = 0;
        System.out.print("P(" + X + "|" + e.toString().replace("{","").replace("}","") + ") = \u03B1<");
        for (Value v: X.getDomain()) {
            i++;
            System.out.printf("%.2f", Xdist.get(v));
            if (i == X.getDomain().size()) {
                System.out.println(">\n");
            } else {
                System.out.print(", ");
            }
        }

        Xdist.normalize();
        return Xdist;
    }

}