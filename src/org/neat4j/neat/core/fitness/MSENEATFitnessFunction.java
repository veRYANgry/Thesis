package org.neat4j.neat.core.fitness;

import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.NEATFitnessFunction;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.data.core.ExpectedOutputSet;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.ForwardAgent;
import ch.idsia.agents.controllers.NeatAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.MarioAIOptions;

public class MSENEATFitnessFunction extends NEATFitnessFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int evaluationRepetitions = 1;
	//private Task task;

	public MSENEATFitnessFunction(NeuralNet net, NetworkDataSet dataSet) {
		super(net, dataSet);
	}
	

	
	public double evaluate(Chromosome genoType) {return 2;}
	
	public double evaluates(Chromosome genoType, Task task) {

		int j;
		NetworkOutputSet opSet;
		NetworkInput ip;
		double[] op;
		double[] eOp;
		double error = 0;

		// need to create a net based on this chromo
		this.createNetFromChromo(genoType);
		
		
	    double fitness = 0;
	    for (int i = 0; i < evaluationRepetitions; i++)
	    {
	        //population[which].reset();
	        fitness += task.evaluate((Agent) new NeatAgent(this.net()));

//	            System.out.println("which " + which + " fitness " + fitness[which]);
	    }
	    fitness = fitness / evaluationRepetitions;
	    
	    
	   // System.out.println("fitness is " + fitness);
		return fitness;
	}
}
