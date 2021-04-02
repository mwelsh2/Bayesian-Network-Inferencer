package inference;

import core.Assignment;
import core.BayesianNetwork;
import core.Distribution;
import core.Inferencer;
import core.RandomVariable;
import core.Value;

/**
 * Implements the rejection sampling algorithm for approximate inference in 
 * Bayesian networks described in AIMA Figure 14.14.
 */
public class RejectionSamplingInferencer implements Inferencer {

    public RejectionSamplingInferencer(){}

    /**
     * Returns true if Assignment x is consistent with Assignment e, in the 
     * sense that every variable in e has the same value in x.
     */
    protected boolean isConsistent(Assignment x, Assignment e) {
        for (RandomVariable e_i: e.keySet()) {
            if (!x.get(e_i).equals(e.get(e_i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return an estimate of the Distribution P(X|e) from the given 
     * BayesianNetwork using the default number of samples.
     */
    @Override
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network) {
        return query(X, e, network, 5000);
    }

    /**
     * Return an estimate of the Distribution P(X|e) from the given 
     * BayesianNetwork, where X is the query RandomVariable, e is an 
     * Assignment representing the evidence variables and their values, and n 
     * is the total number of samples to be generated.
     */
    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork network, int n) {
        Distribution Xdist = new base.Distribution(X);
        for (Value v: X.getDomain()) {
            Xdist.put(v, 0.0);
        }
        int count = 0;
        
        for (int i = 0; i < n; i++) {
            // Generate Prior Sample
            PriorSampler ps = new PriorSampler(network);
            Assignment a = ps.getSample();

            // If sample is consistent with evidence
            if (isConsistent(a, e)) {
                count++;
                // Update probability distribution
                Value x = a.get(X);
                Xdist.set(x, Xdist.get(x) + 1.0);
            } 
        }
        System.out.println("\n" + count + " samples out of " + n + " are consistent with the evidence.");

        System.out.print("\nN: <");
        int i = 0;
        for (Value v: X.getDomain()) {
            i++;
            System.out.printf("%d", (int) Xdist.get(v));
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
            System.out.printf("%.4f", Xdist.get(v)/n);
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