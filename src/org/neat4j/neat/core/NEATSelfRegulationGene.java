package org.neat4j.neat.core;

import java.util.ArrayList;
import java.util.Random;

public class NEATSelfRegulationGene implements NEATGene, Cloneable {
	private int innovationNumber;
	private NEATSelfRegulationGene linkForward;
	private double CompletionThreshold = 0; //used to determined if its time to move to the Gene rules
	private int ThresholdType = 0; //describes the type of rule to determine threshold
	private boolean active;

	//values that can be changed separately for each individual
	private double pAddLink;
	private double pAddNode;
	private double pToggleLink;
	private double pMutation;
	private double pMutateBias;
	private double pWeightReplaced;
	
	private double maxPerturb;
	private double maxBiasPerturb;
	
	private double disjointCoeff;
	private double excessCoeff;
	private double weightCoeff;
	private double threshold;
	
	private int maxSpecieAge;
	private int specieAgeThreshold;
	private int specieYouthThreshold;
	private double agePenalty;
	private double youthBoost;
	
	private double survivalThreshold;
	//New features added
	private double pMutatateRegulation;
	private double pMutatateRegulationHueristics;
	private double pMutatateRegulationCoeff;
	private double pMutatateRegulationMutation;
	private double pMutatateRegulationAgeing;
	
	private double maxPerturbRegulation;//max change that a value listed above can change in one mutation
	
	private  ArrayList<double[]> Hueristics;
	


	public NEATSelfRegulationGene(int innovationNumber){
		this.innovationNumber = innovationNumber;
		initialize(new Random(System.currentTimeMillis()));
		active = true;
	}
	

	public NEATSelfRegulationGene(int innovationNumber,
			NEATSelfRegulationGene linkForward, double completionThreshold,
			int thresholdType, boolean active, double pAddLink,
			double pAddNode, double pToggleLink, double pMutation,
			double pMutateBias, double pWeightReplaced, double maxPerturb,
			double maxBiasPerturb, double disjointCoeff, double excessCoeff,
			double weightCoeff, double threshold, int maxSpecieAge,
			int specieAgeThreshold, int specieYouthThreshold,
			double agePenalty, double youthBoost, double survivalThreshold,
			double pMutatateRegulation, double pMutatateRegulationHueristics,
			double pMutatateRegulationCoeff,
			double pMutatateRegulationMutation, double pMutatateRegulationAgeing,double maxPerturbRegulation,  ArrayList<double[]> Hueristics ) {
		super();
		this.innovationNumber = innovationNumber;
		this.linkForward = linkForward;
		CompletionThreshold = completionThreshold;
		ThresholdType = thresholdType;
		this.active = active;
		this.pAddLink = pAddLink;
		this.pAddNode = pAddNode;
		this.pToggleLink = pToggleLink;
		this.pMutation = pMutation;
		this.pMutateBias = pMutateBias;
		this.pWeightReplaced = pWeightReplaced;
		this.maxPerturb = maxPerturb;
		this.maxBiasPerturb = maxBiasPerturb;
		this.disjointCoeff = disjointCoeff;
		this.excessCoeff = excessCoeff;
		this.weightCoeff = weightCoeff;
		this.threshold = threshold;
		this.maxSpecieAge = maxSpecieAge;
		this.specieAgeThreshold = specieAgeThreshold;
		this.specieYouthThreshold = specieYouthThreshold;
		this.agePenalty = agePenalty;
		this.youthBoost = youthBoost;
		this.survivalThreshold = survivalThreshold;
		this.pMutatateRegulation = pMutatateRegulation;
		this.pMutatateRegulationHueristics = pMutatateRegulationHueristics;
		this.pMutatateRegulationCoeff = pMutatateRegulationCoeff;
		this.pMutatateRegulationMutation = pMutatateRegulationMutation;
		this.pMutatateRegulationAgeing = pMutatateRegulationAgeing;
		this.maxPerturbRegulation = maxPerturbRegulation;
		this.Hueristics = new  ArrayList<double[]>();
		if(Hueristics != null){
			for(int i = 0; i < Hueristics.size();i++){
				this.Hueristics.add(new double[Hueristics.get(i).length]);
				for(int j = 0; j < Hueristics.get(i).length; j++)
					this.Hueristics.get(i)[j] = Hueristics.get(i)[j];
			}
		}
	}


	public void initialize(Random Rand){
		pAddLink = Rand.nextDouble();
		pAddNode = Rand.nextDouble();
		pToggleLink = Rand.nextDouble();
		pMutation = Rand.nextDouble();
		pMutateBias = Rand.nextDouble();
		pWeightReplaced = Rand.nextDouble();
		
		maxPerturb = Rand.nextDouble();
		maxBiasPerturb = Rand.nextDouble();
		
		disjointCoeff = Rand.nextDouble();
		excessCoeff = Rand.nextDouble();
		weightCoeff = Rand.nextDouble();
		threshold = Rand.nextDouble();
		survivalThreshold = Rand.nextDouble();
		/////////////////
		//TODO age needs to be worked on!!!!!!!
		////////////////////
		maxSpecieAge = Rand.nextInt(1000);
		specieAgeThreshold = Rand.nextInt(1000);
		specieYouthThreshold = 1;
		//TODO use equations to check for suitable ranges (may not even be needed)
		agePenalty = 1;
		youthBoost =  1;
		
		pMutatateRegulation  = Rand.nextDouble();
		pMutatateRegulationHueristics  = Rand.nextDouble();
		pMutatateRegulationCoeff  = Rand.nextDouble();
		pMutatateRegulationMutation  = Rand.nextDouble();
		pMutatateRegulationAgeing  = Rand.nextDouble();
		maxPerturbRegulation = Rand.nextDouble();
		
		//TODO create a way to vary the number of h values
		//TODO normalize h values from the runs to be constant for max value or approach a constant
		Hueristics = new  ArrayList<double[]>();
		Hueristics.add(new double[8]);
		for(int i = 0; i < Hueristics.get(0).length;i++){
			Hueristics.get(0)[i] = (Rand.nextDouble() - .5) * 2;
		}
		
		
	}
	@Override
	public NEATSelfRegulationGene clone(){
		return new NEATSelfRegulationGene( innovationNumber,
				 linkForward,  CompletionThreshold,
				 ThresholdType,  active,  pAddLink,
				 pAddNode,  pToggleLink,  pMutation,
				 pMutateBias,  pWeightReplaced,  maxPerturb,
				 maxBiasPerturb,  disjointCoeff,  excessCoeff,
				 weightCoeff,  threshold,  maxSpecieAge,
				 specieAgeThreshold,  specieYouthThreshold,
				 agePenalty,  youthBoost,  survivalThreshold,
				 pMutatateRegulation,  pMutatateRegulationHueristics,
					 pMutatateRegulationCoeff,
					 pMutatateRegulationMutation, pMutatateRegulationAgeing, maxPerturbRegulation, Hueristics);
	}
	
	public double Difference(NEATSelfRegulationGene compareTo){
		double totalDiff = 0;
		totalDiff += Math.abs(this.pAddLink - compareTo.pAddLink);
		totalDiff += Math.abs(this.pAddNode - compareTo.pAddNode);
		totalDiff += Math.abs(this.pToggleLink - compareTo.pToggleLink);
		totalDiff += Math.abs(this.pMutation - compareTo.pMutation);
		totalDiff += Math.abs(this.pMutateBias - compareTo.pMutateBias);
		totalDiff += Math.abs(this.pWeightReplaced - compareTo.pWeightReplaced);
		totalDiff += Math.abs(this.maxPerturb - compareTo.maxPerturb);
		totalDiff += Math.abs(this.maxBiasPerturb - compareTo.maxBiasPerturb);
		
		totalDiff += Math.abs(this.disjointCoeff - compareTo.disjointCoeff);
		totalDiff += Math.abs(this.excessCoeff - compareTo.excessCoeff);
		totalDiff += Math.abs(this.weightCoeff - compareTo.weightCoeff);
		
		//totalDiff += Math.abs(this.maxSpecieAge - compareTo.maxSpecieAge) / 10;
		
		totalDiff += Math.abs(this.survivalThreshold - compareTo.survivalThreshold);
		totalDiff += Math.abs(this.pMutatateRegulation - compareTo.pMutatateRegulation);
		totalDiff += Math.abs(this.pMutatateRegulationHueristics - compareTo.pMutatateRegulationHueristics);
		totalDiff += Math.abs(this.pMutatateRegulationCoeff - compareTo.pMutatateRegulationCoeff);
		totalDiff += Math.abs(this.pMutatateRegulationMutation - compareTo.pMutatateRegulationMutation);
		totalDiff += Math.abs(this.pMutatateRegulationAgeing - compareTo.pMutatateRegulationAgeing);
		totalDiff += Math.abs(this.maxPerturbRegulation - compareTo.maxPerturbRegulation);
		
		
		return totalDiff / ((this.maxPerturbRegulation + compareTo.maxPerturbRegulation) / 2 * 18);
	}
	
	public ArrayList<double[]> getHueristics() {
		return Hueristics;
	}

	public double getpWeightReplaced() {
		return pWeightReplaced;
	}

	public void setpWeightReplaced(double pWeightReplaced) {
		this.pWeightReplaced = pWeightReplaced;
	}

	public boolean isActive() {
		return active;
	}

	public double getMaxPerturbRegulation() {
		return maxPerturbRegulation;
	}


	public void setMaxPerturbRegulation(double maxPerturbRegulation) {
		this.maxPerturbRegulation = maxPerturbRegulation;
	}


	public double getpMutatateRegulationHueristics() {
		return pMutatateRegulationHueristics;
	}


	public void setpMutatateRegulationHueristics(
			double pMutatateRegulationHueristics) {
		this.pMutatateRegulationHueristics = pMutatateRegulationHueristics;
	}


	public double getpMutatateRegulationCoeff() {
		return pMutatateRegulationCoeff;
	}


	public void setpMutatateRegulationCoeff(double pMutatateRegulationCoeff) {
		this.pMutatateRegulationCoeff = pMutatateRegulationCoeff;
	}


	public double getpMutatateRegulationMutation() {
		return pMutatateRegulationMutation;
	}


	public void setpMutatateRegulationMutation(double pMutatateRegulationMutation) {
		this.pMutatateRegulationMutation = pMutatateRegulationMutation;
	}


	public double getpMutatateRegulationAgeing() {
		return pMutatateRegulationAgeing;
	}


	public void setpMutatateRegulationAgeing(double pMutatateRegulationAgeing) {
		this.pMutatateRegulationAgeing = pMutatateRegulationAgeing;
	}


	public double getSurvivalThreshold() {
		return survivalThreshold;
	}

	public void setSurvivalThreshold(double survivalThreshold) {
		this.survivalThreshold = survivalThreshold;
	}

	public double getMaxPerturb() {
		return maxPerturb;
	}

	public void setMaxPerturb(double maxPerturb) {
		this.maxPerturb = maxPerturb;
	}

	public double getMaxBiasPerturb() {
		return maxBiasPerturb;
	}

	public void setMaxBiasPerturb(double maxBiasPerturb) {
		this.maxBiasPerturb = maxBiasPerturb;
	}

	public double getDisjointCoeff() {
		return disjointCoeff;
	}

	public void setDisjointCoeff(double disjointCoeff) {
		this.disjointCoeff = disjointCoeff;
	}

	public double getExcessCoeff() {
		return excessCoeff;
	}

	public void setExcessCoeff(double excessCoeff) {
		this.excessCoeff = excessCoeff;
	}

	public double getWeightCoeff() {
		return weightCoeff;
	}

	public void setWeightCoeff(double weightCoeff) {
		this.weightCoeff = weightCoeff;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getMaxSpecieAge() {
		return maxSpecieAge;
	}

	public void setMaxSpecieAge(int maxSpecieAge) {
		this.maxSpecieAge = maxSpecieAge;
	}

	public int getSpecieAgeThreshold() {
		return specieAgeThreshold;
	}

	public void setSpecieAgeThreshold(int specieAgeThreshold) {
		this.specieAgeThreshold = specieAgeThreshold;
	}

	public int getSpecieYouthThreshold() {
		return specieYouthThreshold;
	}

	public void setSpecieYouthThreshold(int specieYouthThreshold) {
		this.specieYouthThreshold = specieYouthThreshold;
	}

	public double getAgePenalty() {
		return agePenalty;
	}

	public void setAgePenalty(double agePenalty) {
		this.agePenalty = agePenalty;
	}

	public double getYouthBoost() {
		return youthBoost;
	}

	public void setYouthBoost(double youthBoost) {
		this.youthBoost = youthBoost;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public double getpAddLink() {
		return pAddLink;
	}

	public void setpAddLink(double pAddLink) {
		this.pAddLink = pAddLink;
	}

	public double getpAddNode() {
		return pAddNode;
	}

	public void setpAddNode(double pAddNode) {
		this.pAddNode = pAddNode;
	}

	public double getpToggleLink() {
		return pToggleLink;
	}

	public void setpToggleLink(double pToggleLink) {
		this.pToggleLink = pToggleLink;
	}

	public double getpMutation() {
		return pMutation;
	}

	public void setpMutation(double pMutation) {
		this.pMutation = pMutation;
	}

	public double getpMutateBias() {
		return pMutateBias;
	}

	public void setpMutateBias(double pMutateBias) {
		this.pMutateBias = pMutateBias;
	}

	public double getpMutatateRegulation() {
		return pMutatateRegulation;
	}

	public void setpMutatateRegulation(double pMutatateRegulation) {
		this.pMutatateRegulation = pMutatateRegulation;
	}
	
	@Override
	public int getInnovationNumber() {
		return (this.innovationNumber);
	}

	@Override
	public Number geneAsNumber() {
		// TODO Auto-generated method stub
		return (this.innovationNumber);
	}

	@Override
	public String geneAsString() {
		// TODO Auto-generated method stub
		return "Nothing here yet";
	}

}