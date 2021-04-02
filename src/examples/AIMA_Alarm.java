package examples;

import java.util.Set;

import core.Assignment;
import core.BayesianNetwork;
import core.CPT;
import core.Distribution;
import core.Inferencer;
import core.RandomVariable;
import inference.EnumerationInferencer;
import inference.GibbsSamplingInferencer;
import inference.LikelihoodWeightingInferencer;
import inference.RejectionSamplingInferencer;
import base.BooleanDomain;
import base.BooleanValue;
import base.NamedVariable;
import util.ArraySet;

/**
 * The AIMA Burglar Alarm example of a BayesianNetwork (AIMA Fig 14.2).
 * <p>
 * P(B|j,m) = \alpha &lt;0.00059224,0.0014919&gt; ~= &lt;0.284,0.716&gt; (p. 524)
 */
public class AIMA_Alarm {

	public static void main(String[] args) {
		RandomVariable B = new NamedVariable("B", new BooleanDomain());
		RandomVariable E = new NamedVariable("E", new BooleanDomain());
		RandomVariable A = new NamedVariable("A", new BooleanDomain());
		RandomVariable J = new NamedVariable("J", new BooleanDomain());
		RandomVariable M = new NamedVariable("M", new BooleanDomain());
		BayesianNetwork bn = new base.BayesianNetwork();
		bn.add(B);
		bn.add(E);
		bn.add(A);
		bn.add(J);
		bn.add(M);
		// Shorthands
		BooleanValue TRUE = BooleanValue.TRUE;
		BooleanValue FALSE = BooleanValue.FALSE;
		Assignment a;

		// B (no parents)
		CPT Bprior = new base.CPT(B);
		a = new base.Assignment();
		Bprior.set(TRUE, a, 0.001);
		Bprior.set(FALSE, a, 0.999);
		bn.connect(B, new ArraySet<RandomVariable>() , Bprior);

		// E (no parents)
		CPT Eprior = new base.CPT(E);
		a = new base.Assignment();
		Eprior.set(TRUE, a, 0.002);
		Eprior.set(FALSE, a, 0.998);
		bn.connect(E, new ArraySet<RandomVariable>() , Eprior);

		// B,E -> A
		Set<RandomVariable> BE = new ArraySet<RandomVariable>();
		BE.add(B);
		BE.add(E);
		CPT AgivenBE = new base.CPT(A);
		a = new base.Assignment();
		a.put(B, TRUE);
		a.put(E, TRUE);
		AgivenBE.set(TRUE, a, 0.95);
		AgivenBE.set(FALSE, a, 0.05);
		a = new base.Assignment();
		a.put(B, TRUE);
		a.put(E, FALSE);
		AgivenBE.set(TRUE, a, 0.94);
		AgivenBE.set(FALSE, a, 0.06);
		a = new base.Assignment();
		a.put(B, FALSE);
		a.put(E, TRUE);
		AgivenBE.set(TRUE, a, 0.29);
		AgivenBE.set(FALSE, a, 0.71);
		a = new base.Assignment();
		a.put(B, FALSE);
		a.put(E, FALSE);
		AgivenBE.set(TRUE, a, 0.001);
		AgivenBE.set(FALSE, a, 0.999);
		bn.connect(A, BE, AgivenBE);

		// A -> J
		Set<RandomVariable> justA = new ArraySet<RandomVariable>();
		justA.add(A);
		CPT JgivenA = new base.CPT(J);
		a = new base.Assignment();
		a.put(A, TRUE);
		JgivenA.set(TRUE, a, 0.9);
		JgivenA.set(FALSE, a, 0.1);
		a = new base.Assignment();
		a.put(A, FALSE);
		JgivenA.set(TRUE, a, 0.05);
		JgivenA.set(FALSE, a, 0.95);
		bn.connect(J, justA, JgivenA);

		// A -> M
		CPT MgivenA = new base.CPT(M);
		a = new base.Assignment();
		a.put(A, TRUE);
		MgivenA.set(TRUE, a, 0.7);
		MgivenA.set(FALSE, a, 0.3);
		a = new base.Assignment();
		a.put(A, FALSE);
		MgivenA.set(TRUE, a, 0.01);
		MgivenA.set(FALSE, a, 0.99);
		bn.connect(M, justA, MgivenA);
		
		System.out.println(bn);

		System.out.println("Enumeration Inference");
		System.out.println("P(B|j,m) = \\alpha <0.00059224,0.0014919> ~= <0.284,0.716>");
		Inferencer exact = new EnumerationInferencer();
		a = new base.Assignment();
		a.put(J, TRUE);
		a.put(M, TRUE);
		Distribution e_dist = exact.query(B, a, bn);
		System.out.println(e_dist);

		System.out.println("\nRejection Sampling");
		System.out.println("P(B|j,m) ~= <0.284,0.716>");
		Inferencer reject = new RejectionSamplingInferencer();
		Distribution r_dist = reject.query(B, a, bn);
		System.out.println(r_dist);

		System.out.println("\nLikelihood Sampling");
		System.out.println("P(B|j,m) ~= <0.284,0.716>");
		Inferencer weight = new LikelihoodWeightingInferencer();
		Distribution w_dist = weight.query(B, a, bn);
		System.out.println(w_dist);

		System.out.println("\nGibbs Sampling");
		System.out.println("P(B|j,m) ~= <0.284,0.716>");
		Inferencer gibbs = new GibbsSamplingInferencer();
		Distribution g_dist = gibbs.query(B, a, bn);
		System.out.println(g_dist);

	}

}
