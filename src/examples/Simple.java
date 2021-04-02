package examples;

import core.Assignment;
import core.BayesianNetwork;
import core.CPT;
import core.Distribution;
import core.Domain;
import core.Inferencer;
import core.RandomVariable;
import core.Value;
import inference.EnumerationInferencer;
import inference.GibbsSamplingInferencer;
import inference.LikelihoodWeightingInferencer;
import inference.RejectionSamplingInferencer;
import base.BooleanDomain;
import base.BooleanValue;
import base.NamedVariable;
import base.StringValue;
import util.ArraySet;

/**
 * The simple Bayesian network example given in the project description.
 * <p>
 * P(B|j,m) = \alpha &lt;0.00059224,0.0014919&gt; ~= &lt;0.284,0.716&gt; (p. 524)
 */
public class Simple {

	public static void main(String args[]) {
		RandomVariable A = new NamedVariable("A", new BooleanDomain());
		RandomVariable B = new NamedVariable("B", new BooleanDomain());
		Value high = new StringValue("high");
		Value med = new StringValue("med");
		Value low = new StringValue("low");
		Domain cdomain = new base.Domain(high, med, low);
		RandomVariable C = new NamedVariable("C", cdomain);
			
		BayesianNetwork bn = new base.BayesianNetwork();
		bn.add(A);
		bn.add(B);
		bn.add(C);

		//Shorthands
		BooleanValue TRUE = BooleanValue.TRUE;
		BooleanValue FALSE = BooleanValue.FALSE;
		Assignment assign;

		//A (no parents)
		CPT Aprior = new base.CPT(A);
		assign = new base.Assignment();
		Aprior.set(TRUE, assign, 0.2);
		Aprior.set(FALSE, assign, 0.8);
		bn.connect(A, new ArraySet<RandomVariable>(), Aprior);

		// A -> B
		ArraySet<RandomVariable> justA = new ArraySet<RandomVariable>();
		justA.add(A);
		CPT BgivenA = new base.CPT(B);
		assign = new base.Assignment();
		assign.put(A, TRUE);
		BgivenA.set(TRUE, assign, 0.03);
		BgivenA.set(FALSE, assign, 1-0.03);
		assign = new base.Assignment();
		assign.put(A, FALSE);
		BgivenA.set(TRUE, assign, 1-0.13);
		BgivenA.set(FALSE, assign, 0.13);
		bn.connect(B, justA, BgivenA);
		
		// A -> C
		CPT CgivenA = new base.CPT(C);
		assign = new base.Assignment();
		assign.put(A, TRUE);
		CgivenA.set(high, assign, 0.08);
		CgivenA.set(med, assign, 0.73);
		CgivenA.set(low, assign, 0.19);
		assign = new base.Assignment();
		assign.put(A, FALSE);
		CgivenA.set(high, assign, 0.62);
		CgivenA.set(med, assign, 0.21);
		CgivenA.set(low, assign, 0.17);
		bn.connect(C, justA, CgivenA);

		System.out.println(bn);
		
		System.out.println("Enumeration Inference");
		System.out.println("P(C|B=true) ~= <0.615,0.214,0.170>");
		Inferencer exact = new EnumerationInferencer();
		assign = new base.Assignment();
		assign.put(B, TRUE);
		Distribution dist = exact.query(C, assign, bn);
		System.out.println(dist);

		System.out.println("\nRejection Sampling");
		System.out.println("P(C|B=true) ~= <0.615,0.214,0.170>");
		Inferencer reject = new RejectionSamplingInferencer();
		Distribution r_dist = reject.query(C, assign, bn);
		System.out.println(r_dist);

		System.out.println("\nLikelihood Sampling");
		System.out.println("P(C|B=true) ~= <0.615,0.214,0.170>");
		Inferencer weight = new LikelihoodWeightingInferencer();
		Distribution w_dist = weight.query(C, assign, bn);
		System.out.println(w_dist);

		System.out.println("\nGibbs Sampling");
		System.out.println("P(C|B=true) ~= <0.615,0.214,0.170>");
		Inferencer gibbs = new GibbsSamplingInferencer();
		Distribution g_dist = gibbs.query(C, assign, bn);
		System.out.println(g_dist);
	
	}
}