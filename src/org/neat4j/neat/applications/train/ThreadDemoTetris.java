package org.neat4j.neat.applications.train;

import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.nn.core.NeuralNet;

import tetris.BrainScorer;
import tetris.NetBrain;

public class ThreadDemoTetris extends Thread {
	private NEATGATrainingManager gam;
	private AIConfig config;
	private Chromosome tempChrome;

	
	
	public ThreadDemoTetris(NEATGATrainingManager gam, AIConfig config,
			Chromosome tempChrome) {
		super();
		this.gam = gam;
		this.config = config;
		this.tempChrome = tempChrome;
	}



	public void run() {		
        NeuralNet nets = null;
		try {
			nets = gam.createNet(config);
		} catch (InitialisationFailedException a) {
			// TODO Auto-generated catch block
			a.printStackTrace();
		}
		
		((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(tempChrome);
		((NEATNeuralNet)nets).updateNetStructure();
		
		NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
		frame.setTitle("Demo");
		frame.showNet();
		BrainScorer scorer = new BrainScorer();
		scorer.demo(new NetBrain(nets));
		return;
}

}
