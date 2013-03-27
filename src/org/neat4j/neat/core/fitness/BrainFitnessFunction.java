package org.neat4j.neat.core.fitness;

import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.applications.train.VisionBound;
import org.neat4j.neat.core.NEATChromosome;
import org.neat4j.neat.core.NEATFitnessFunction;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.data.core.ExpectedOutputSet;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.nn.core.NeuralNet;

import tetris.BrainScorer;
import tetris.NetBrain;


public class BrainFitnessFunction extends NEATFitnessFunction {

	private static final long serialVersionUID = 2L;
	private final int evaluationRepetitions = 1;
	//private Task task;

	public BrainFitnessFunction(NeuralNet net, NetworkDataSet dataSet) {
		super(net, dataSet);
	}
	

	
	public double evaluate(Chromosome genoType) {return 2;}
	
	public double[] evaluates(Chromosome genoType) {


		// need to create a net based on this chromo
		this.createNetFromChromo(genoType);
		BrainScorer scorer = new BrainScorer();
	   // System.out.println("fitness is " + fitness);
		//TODO return an array of fitnesses 
		double[] temp = new double[2];
		
		if(((NEATChromosome)genoType).findActiveReg() != null){
			temp = scorer.rate(new NetBrain(this.net()),((NEATChromosome)genoType).findActiveReg().getHueristics());
		} else{
			temp = scorer.rate(new NetBrain(this.net()), null);
		}
		
		return temp;
	}
	
	
}
