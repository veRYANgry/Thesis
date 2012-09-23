package org.neat4j.neat.core;

import java.util.Random;

public class NEATSelfRegulationGene implements NEATGene {
	private int innovationNumber;
	private NEATSelfRegulationGene linkForward;
	private double CompletionThreshold; //used to determined if its time to move to the Gene rules
	private int ThresholdType; //describes the type of rule to determine threshold
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
	//New features added
	private double pMutatateRegulation;
	
	public NEATSelfRegulationGene(int innovationNumber){
		this.innovationNumber = innovationNumber;
		initialize(new Random(System.currentTimeMillis()));
		active = true;
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
		
		maxSpecieAge = Rand.nextInt(1000);
		specieAgeThreshold = Rand.nextInt(1000);
		specieYouthThreshold = Rand.nextInt(1000);
		//TODO use equations to check for suitable ranges (may not even be needed)
		agePenalty = 1;
		youthBoost = Rand.nextInt(10)  + 1;
		
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