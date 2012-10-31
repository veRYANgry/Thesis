package org.neat4j.neat.applications.train;

import java.util.Vector;

public class runStatistics {

	private String RunName;
	private int bestFitness;
	private int generation;
	private Vector<Integer> BestFitEachGeneration;
	
	
	public runStatistics(String runName, int bestFitness, int generation) {
		super();
		RunName = runName;
		this.bestFitness = bestFitness;
		this.generation = generation;
	}
}
