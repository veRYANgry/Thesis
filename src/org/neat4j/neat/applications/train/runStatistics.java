package org.neat4j.neat.applications.train;

import java.util.Vector;

import org.neat4j.core.AIConfig;
import org.neat4j.neat.ga.core.Chromosome;

public class runStatistics {



	private String RunName;
	private double bestFitness;
	private int bestFitGen;
	private int generation;
	private Vector<Double> BestFitEachGeneration;
	
	//Later viewing////////
	private Chromosome Bestchrome;
	private AIConfig aiconfig;
	private int LevelModeIndex;
	private int seed;
	private String levelName;
	private int difficulty;
	///////////////////////
	
	
	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public Chromosome getBestchrome() {
		return Bestchrome;
	}

	public int getLevelModeIndex() {
		return LevelModeIndex;
	}

	public void setLevelModeIndex(int levelModeIndex) {
		LevelModeIndex = levelModeIndex;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public void setBestchrome(Chromosome bestchrome) {
		Bestchrome = bestchrome;
	}

	public AIConfig getAiconfig() {
		return aiconfig;
	}

	public void setAiconfig(AIConfig aiconfig) {
		this.aiconfig = aiconfig;
	}

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
