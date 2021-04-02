package inference;

import java.util.Random;
import java.util.Set;

import core.Assignment;
import core.BayesianNetwork;
import core.Distribution;
import core.Domain;
import core.Inferencer;
import core.RandomVariable;
import core.Value;

/**
 * Implements the Gibbs sampling algorithm for approximate inference in 
 * Bayesian networks described in AIMA Figure 14.16.
 */
public class GibbsSamplingInferencer implements Inferencer {

    public GibbsSamplingInferencer() {}

    /**
     * Compute and return the Distribution of RandomVariable X given evidence e
     * using the distribution encoded by the given BayesianNetwork.
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
        // Local Variables
        Random rand = new Random();
        Distribution Xdist = new base.Distribution(X);
        for (Value v: X.getDomain()) {
            Xdist.put(v, 0.0);
        }

        Set<RandomVariable> Z = network.getVariables(); // nonevidence variables
        for (RandomVariable e_i: e.keySet()) { Z.remove(e_i); }
        Assignment a = e.copy();
        
        // Intialize Assignment with random values for the variables in Z
        for (RandomVariable Zi: Z) {
            Domain Zdomain = Zi.getDomain();
            int index = rand.nextInt(Zdomain.size());
            int i = 0;
            Value Zval = null;
            for (Value v: Zdomain) {
                if (i == index) {
                    Zval = v;
                }
                i++;
            }
            a.put(Zi, Zval);
        }

        for (int i = 0; i < n; i++) {
            for (RandomVariable Zi: Z) {

                // Sample given Markov Blanket
                MarkovBlanketSampler mb = new MarkovBlanketSampler(network);
                mb.sample(Zi, a);

                // Update probability distribution accordingly
                Value x = a.get(X); 
                Xdist.put(x, Xdist.get(x) + 1);
            }

        }

        int i = 0;
        System.out.print("\nN: <");
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
            System.out.printf("%d", (int) Xdist.get(v));
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