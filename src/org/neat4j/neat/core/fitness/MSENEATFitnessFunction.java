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
	
	public double[] evaluates(Chromosome genoType, Task task, VisionBound Vision) {


		// need to create a net based on this chromo
		this.createNetFromChromo(genoType);

	   // System.out.println("fitness is " + fitness);
		//TODO return an array of fitnesses 
		if(((NEATChromosome)genoType).findActiveReg() != null)
			return ((ProgressTask)task).evaluateAll((Agent) new NeatAgent(this.net(),Vision,((NEATChromosome)genoType).findActiveReg().getHueristics()));
		else
			return ((ProgressTask)task).evaluateAll((Agent) new NeatAgent(this.net(),Vision,null));
	}
}
