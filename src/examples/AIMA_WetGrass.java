package examples;

import java.util.Set;

import base.BooleanDomain;
import base.BooleanValue;
import base.NamedVariable;
import core.Assignment;
import core.BayesianNetwork;
import core.CPT;
import core.Distribution;
import core.Inferencer;
import core.RandomVariable;
import inference.EnumerationInferencer;
import inference.GibbsSamplingInferencer;
import inference.RejectionSamplingInferencer;
import inference.LikelihoodWeightingInferencer;
import util.ArraySet;

/**
 * The AIMA WetGrass example of a BayesianNetwork (AIMA Fig. 14.12).
 * <p>
 * P(Rain|Sprinkler=true) = &lt;0.3,0.7&gt; (p. 532)
 */
public class AIMA_WetGrass {

	public static void main(String[] args) {
		RandomVariable C = new NamedVariable("C", new BooleanDomain());
		RandomVariable S = new NamedVariable("S", new BooleanDomain());
		RandomVariable R = new NamedVariable("R", new BooleanDomain());
		RandomVariable W = new NamedVariable("W", new BooleanDomain());
		BayesianNetwork bn = new base.BayesianNetwork();
		bn.add(C);
		bn.add(S);
		bn.add(R);
		bn.add(W);
		// Shorthands
		BooleanValue TRUE = BooleanValue.TRUE;
		BooleanValue FALSE = BooleanValue.FALSE;
		Assignment a;

		// C (no parents)
		CPT Bprior = new base.CPT(C);
		a = new base.Assignment();
		Bprior.set(TRUE, a, 0.5);
		Bprior.set(FALSE, a, 1-0.5);
		bn.connect(C, new ArraySet<RandomVariable>() , Bprior);

		// C -> S
		Set<RandomVariable> justC = new ArraySet<RandomVariable>();
		justC.add(C);
		CPT SgivenC = new base.CPT(S);
		a = new base.Assignment();
		a.put(C, TRUE);
		SgivenC.set(TRUE, a, 0.1);
		SgivenC.set(FALSE, a, 1-0.1);
		a = new base.Assignment();
		a.put(C, FALSE);
		SgivenC.set(TRUE, a, 0.5);
		SgivenC.set(FALSE, a, 1-0.5);
		bn.connect(S, justC, SgivenC);

		// C -> R
		justC.add(C);
		CPT RgivenC = new base.CPT(R);
		a = new base.Assignment();
		a.put(C, TRUE);
		RgivenC.set(TRUE, a, 0.8);
		RgivenC.set(FALSE, a, 1-0.8);
		a = new base.Assignment();
		a.put(C, FALSE);
		RgivenC.set(TRUE, a, 0.2);
		RgivenC.set(FALSE, a, 1-0.2);
		bn.connect(R, justC, RgivenC);

		// S,R -> W
		Set<RandomVariable> SR = new ArraySet<RandomVariable>();
		SR.add(S);
		SR.add(R);
		CPT WgivenSR = new base.CPT(W);
		a = new base.Assignment();
		a.put(S, TRUE);
		a.put(R, TRUE);
		WgivenSR.set(TRUE, a, 0.99);
		WgivenSR.set(FALSE, a, 1-0.99);
		a = new base.Assignment();
		a.put(S, TRUE);
		a.put(R, FALSE);
		WgivenSR.set(TRUE, a, 0.90);
		WgivenSR.set(FALSE, a, 1-0.90);
		a = new base.Assignment();
		a.put(S, FALSE);
		a.put(R, TRUE);
		WgivenSR.set(TRUE, a, 0.90);
		WgivenSR.set(FALSE, a, 1-0.90);
		a = new base.Assignment();
		a.put(S, FALSE);
		a.put(R, FALSE);
		WgivenSR.set(TRUE, a, 0.0);
		WgivenSR.set(FALSE, a, 1-0.0);
		bn.connect(W, SR, WgivenSR);
		
		System.out.println(bn);
		
		System.out.println("Enumeration Inference");
		System.out.println("P(Rain|Sprinkler=true) ~= <0.3,0.7>");
		Inferencer exact = new EnumerationInferencer();
		a = new base.Assignment();
		a.put(S, TRUE);
		Distribution e_dist = exact.query(R, a, bn);
		System.out.println(e_dist);

		System.out.println("\nRejection Sampling");
		System.out.println("P(Rain|Sprinkler=true) ~= <0.3,0.7>");
		Inferencer reject = new RejectionSamplingInferencer();
		Distribution r_dist = reject.query(R, a, bn);
		System.out.println(r_dist);

		System.out.println("\nLikelihood Sampling");
		System.out.println("P(Rain|Sprinkler=true) ~= <0.3,0.7>");
		Inferencer weight = new LikelihoodWeightingInferencer();
		Distribution w_dist = weight.query(R, a, bn);
		System.out.println(w_dist);

		System.out.println("\nGibbs Sampling");
		System.out.println("P(Rain|Sprinkler=true) ~= <0.3,0.7>");
		Inferencer gibbs = new GibbsSamplingInferencer();
		Distribution g_dist = gibbs.query(R, a, bn);
		System.out.println(g_dist);
	}

}
