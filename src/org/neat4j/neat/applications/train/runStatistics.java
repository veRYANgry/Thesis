package org.neat4j.neat.applications.train;

import java.util.Vector;

public class runStatistics {



	private String RunName;
	private double bestFitness;
	private int bestFitGen;
	private int generation;
	private Vector<Double> BestFitEachGeneration;
	
	
	public runStatistics(String runName, double bestFitness, int generation) {
		super();
		RunName = runName;
		this.bestFitness = bestFitness;
		this.generation = generation;
	}
	
	public int getBestFitGen() {
		return bestFitGen;
	}

	public void setBestFitGen(int bestFitGen) {
		this.bestFitGen = bestFitGen;
	}

	public String getRunName() {
		return RunName;
	}


	public void setRunName(String runName) {
		RunName = runName;
	}


	public double getBestFitness() {
		return bestFitness;
	}


	public void setBestFitness(double bestFitness) {
		this.bestFitness = bestFitness;
	}


	public int getGeneration() {
		return generation;
	}


	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
