package org.neat4j.neat.ga.core;

import java.io.Serializable;

/**
 * @author MSimmerson
 *
 */
public interface FitnessFunction extends Operator , Serializable{
	public double evaluate(Chromosome genoType);
	public int requiredChromosomeSize();
}
