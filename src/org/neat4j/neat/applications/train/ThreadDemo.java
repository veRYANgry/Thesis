package org.neat4j.neat.applications.train;

import java.util.Random;
import java.util.Vector;

import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.NeatAgent;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.tools.MarioAIOptions;

public class ThreadDemo extends Thread {

	private Chromosome tempChrome;
	private int difficulty;

	private MarioAIOptions WorkerOptions;
	private VisionBound Vision;
	private NEATGATrainingManager gam;
	private AIConfig config;

	private int LevelModeIndex;
	private int seed;
	private String levelName;
	private ProgressTask task;

	public ThreadDemo(Chromosome tempChrome, int difficulty,
			MarioAIOptions workerOptions, VisionBound vision,
			NEATGATrainingManager gam, AIConfig config, int levelModeIndex,
			int seed, String levelName) {
		super();
		this.tempChrome = tempChrome;
		this.difficulty = difficulty;
		WorkerOptions = workerOptions;
		Vision = vision;
		this.gam = gam;
		this.config = config;
		LevelModeIndex = levelModeIndex;
		this.seed = seed;
		this.levelName = levelName;
	}

	public ThreadDemo(Chromosome tempChrome,
			VisionBound vision, NEATGATrainingManager gam, AIConfig config,
			ProgressTask task) {
		super();
		this.tempChrome = tempChrome;

		Vision = vision;
		this.gam = gam;
		this.config = config;
		this.task = task.clone();
	}

	@Override
	public void run() {

		ProgressTask WorkerTask;
		if (WorkerOptions != null) {
			WorkerOptions.setLevelDifficulty(difficulty);
			WorkerOptions.setFPS(32);
			WorkerOptions.setVisualization(true);
			setOptions(WorkerOptions);
			WorkerTask = new ProgressTask(WorkerOptions);
		} else {
			WorkerTask = task;
			task.showDemo = true;
		}

		NeuralNet nets = null;
		try {
			nets = gam.createNet(config);
		} catch (InitialisationFailedException a) {

			a.printStackTrace();
		}

		((NEATNetDescriptor) (nets.netDescriptor()))
				.updateStructure(tempChrome);
		((NEATNeuralNet) nets).updateNetStructure();

		NEATFrame frame = new NEATFrame((NEATNeuralNet) nets);
		frame.setTitle("Demo");
		frame.showNet();

		WorkerTask.evaluateAll((Agent) new NeatAgent(nets, Vision, null));

		return;
	}

	private void setOptions(MarioAIOptions options) {

		switch (LevelModeIndex) {
		case 0:
			options.setLevelDifficulty(difficulty);
			options.setArgs("-ls " + seed);
			break;
		case 1:
			options.setArgs("-ls " + levelName);
			break;
		case 2:
			Random rand = new Random(System.currentTimeMillis());
			seed = rand.nextInt();
			options.setLevelDifficulty(difficulty);
			options.setArgs("-ls " + seed);
			break;
		case 3:
			options.setLevelDifficulty(difficulty);
			options.setArgs("-ls " + seed);
			break;
		default:
			options.setLevelDifficulty(difficulty);
			options.setArgs("-ls " + seed);
			break;

		}

	}

}
