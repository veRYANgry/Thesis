package org.neat4j.neat.applications.train;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.apache.log4j.Category;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATGADescriptor;
import org.neat4j.neat.core.NEATGeneticAlgorithm;
import org.neat4j.neat.core.NEATGeneticAlgorithmMario;
import org.neat4j.neat.core.NEATLoader;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.core.control.NEATNetManager;
import org.neat4j.neat.core.fitness.InvalidFitnessFunction;
import org.neat4j.neat.core.mutators.InvalidMutatorFunction;
import org.neat4j.neat.core.pselectors.InvalidParentSelectorFunction;
import org.neat4j.neat.core.xover.InvalidCrossoverFunction;
import org.neat4j.neat.data.core.NetworkDataSet;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.CrossOver;
import org.neat4j.neat.ga.core.FitnessFunction;
import org.neat4j.neat.ga.core.GADescriptor;
import org.neat4j.neat.ga.core.GeneticAlgorithm;
import org.neat4j.neat.ga.core.Mutator;
import org.neat4j.neat.ga.core.NeuralFitnessFunction;
import org.neat4j.neat.ga.core.ParentSelector;
import org.neat4j.neat.ga.core.Population;
import org.neat4j.neat.nn.core.LearningEnvironment;
import org.neat4j.neat.nn.core.NeuralNet;
import org.neat4j.neat.nn.core.NeuralNetDescriptor;
import org.neat4j.neat.core.fitness.MSENEATFitnessFunction;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.NeatAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.MarioAIOptions;

/**
 * Training control for a NEAT network based on given configuration.
 * @author MSimmerson
 *
 */
public class NEATGATrainingManagerLoad {
	private static final Category cat = Category.getInstance(NEATGATrainingManager.class);
	private GeneticAlgorithm ga;
	private AIConfig config;
	
	public GeneticAlgorithm ga() {
		return (this.ga);
	}
	/**
	 * @see org.neat4j.ailibrary.core.AIController#initialise(org.neat4j.ailibrary.AIConfig)
	 */
	public void initialise(AIConfig config) throws InitialisationFailedException {
		GADescriptor gaDescriptor = this.createDescriptor(config);
		this.assigGA(this.createGeneticAlgorithm(gaDescriptor));
		try {
			this.assignConfig(config);
			this.ga.pluginFitnessFunction(this.createFunction(config));
			this.ga.pluginCrossOver(this.createCrossOver(config));
			this.ga.pluginMutator(this.createMutator(config));
			this.ga.pluginParentSelector(this.createParentSelector(config));
			this.ga.createPopulation();
		} catch (InvalidFitnessFunction e) {
			throw new InitialisationFailedException(e.getMessage());
		} catch (InvalidCrossoverFunction e) {
			throw new InitialisationFailedException(e.getMessage());
		} catch (InvalidMutatorFunction e) {
			throw new InitialisationFailedException(e.getMessage());
		} catch (InvalidParentSelectorFunction e) {
			throw new InitialisationFailedException(e.getMessage());
		} catch (Exception e) {
			throw new InitialisationFailedException(e.getMessage());
		}
	}

	public void assigGA(GeneticAlgorithm ga) {
		this.ga = ga;
	}
	
	public void assignConfig(AIConfig config) {
		this.config = config;
	}

	public void evolve() throws InitialisationFailedException {

		double lastBestGen = 0, bestsofar = 599;
		int i = 0,diffGen = 0;
		int[] passlevel = {1000,2000,2000,2000,2000,2000,6000,6000,6000,6000,6000};
		this.loadGA("ga");

	    MarioAIOptions options = new MarioAIOptions("nothing");
        options.setArgs("-ls " + "resources/test.lvl" );
	    for (int difficulty = 0; difficulty < 11; difficulty++)
	    {
	        System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");
	        options.setLevelDifficulty(difficulty);
	        options.setFPS(options.globalOptions.MaxFPS);
	        options.setVisualization(false);
	        Task task = new ProgressTask(options);

		
		while (true) {
	        System.out.println("Running Epoch[" + i + "] with diff:" + difficulty);
			//((NEATGeneticAlgorithmMario)this.ga).runEpoch(task);
			
					
			

			options.setVisualization(false);
			
			if ((((NEATGeneticAlgorithmMario) this.ga).genBest() > bestsofar)){
				//this.saveGA("out?!.test");
		        options.setLevelDifficulty(difficulty);
		        options.setFPS(32);
		        options.setVisualization(true);
		        
		        NeuralNet nets = this.createNet(config);
				((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(this.ga.discoverdBestMember());
				((NEATNeuralNet)nets).updateNetStructure();
				
				
				NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
				frame.setTitle("Generation: " + i + " Score :" + ((NEATGeneticAlgorithmMario) this.ga).genBest());
				frame.showNet();
		        
		        //((MSENEATFitnessFunction) ((NEATGeneticAlgorithmMario) ga).gaEvaluator()).evaluates(this.ga.discoverdBestMember(), task);
				
				if(((NEATGeneticAlgorithmMario) this.ga).genBest() > bestsofar) {
					bestsofar = (((NEATGeneticAlgorithmMario) this.ga).genBest());
			        options.setArgs("-ls " + (new Random(System.currentTimeMillis())).nextInt() );
					diffGen = 0;
					//((NEATGeneticAlgorithmMario) this.ga).resetdiscoverdBestMember();
				}
				
				
		       
				
		        options.setFPS(options.globalOptions.MaxFPS);
		        options.setVisualization(false);

				if(((NEATGeneticAlgorithmMario) this.ga).genBest() >= passlevel[difficulty] && lastBestGen  >= passlevel[difficulty] - 1000 ){
					bestsofar = 2000;
					diffGen = 0;
				//((NEATGeneticAlgorithmMario) this.ga).resetdiscoverdBestMember();
				break;
				}
			}
			
			lastBestGen = ((NEATGeneticAlgorithmMario) this.ga).genBest();
			if(diffGen > 60 && difficulty > 1 ){
				difficulty--;
				passlevel[difficulty] += 500;
				//((NEATGeneticAlgorithmMario) this.ga).resetdiscoverdBestMember();
				bestsofar = 0;
			}
			
			diffGen++;
			i++;
		}
	    }
		//cat.debug("Innovation Database Stats - Hits:" + InnovationDatabase.hits + " - misses:" + InnovationDatabase.misses);
	}
	
	
	public NeuralNet createNet(AIConfig config) throws InitialisationFailedException {
		String nnConfigFile;
		AIConfig nnConfig;
		NEATNetManager netManager;
		
		nnConfigFile = config.configElement("NN.CONFIG");
		nnConfig  = new NEATLoader().loadConfig(nnConfigFile);
		nnConfig.updateConfig("INPUT_SIZE", config.configElement("INPUT.NODES"));
		nnConfig.updateConfig("OUTPUT_SIZE", config.configElement("OUTPUT.NODES"));
		netManager = new NEATNetManager();
		netManager.initialise(nnConfig);
		
		return ((NEATNeuralNet)netManager.managedNet());
	}
	
	/**
	 * Saves the best candidate of the generation
	 *
	 */
	public void saveBest() {
		this.save(config.configElement("SAVE.LOCATION"), this.ga.discoverdBestMember());
	}
	
	
	/**
	 * save ga
	 */
	public void loadGA(String fileName) {
			FileInputStream out = null;
			ObjectInputStream s = null;
			try {
				if (fileName != null) {
					cat.info("loading Ga " + fileName);
					out = new FileInputStream(fileName);
					s = new ObjectInputStream(out);
					this.ga = (NEATGeneticAlgorithmMario) s.readObject();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					s.close();
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			cat.info("Loading Ga...Done");
		
	}
	
	
	
	/**
	 * Saves the entire population.  Especially useful for long running evolution processes
	 */
	public void saveGA(String fileName) {
		FileOutputStream out = null;
		ObjectOutputStream s = null;
		try {
			if (fileName != null) {
				cat.debug("Saving GA " + fileName);
				out = new FileOutputStream(fileName);
				s = new ObjectOutputStream(out);
				s.writeObject(this.ga);
				s.flush();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				s.close();
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		cat.debug("Saving GA...Done");
	}

	
	/**
	 * @see org.neat4j.ailibrary.ga.control.GATrainingManager#createDescriptor(org.neat4j.ailibrary.AIConfig)
	 */
	public GADescriptor createDescriptor(AIConfig config) {
		int popSize = Integer.parseInt(config.configElement("POP.SIZE"));
		double pXover = Double.parseDouble(config.configElement("PROBABILITY.CROSSOVER"));
		double pAddLink = Double.parseDouble(config.configElement("PROBABILITY.ADDLINK"));
		double pAddNode = Double.parseDouble(config.configElement("PROBABILITY.ADDNODE"));
		double pToggleLink = Double.parseDouble(config.configElement("PROBABILITY.TOGGLELINK"));
		double pMutation = Double.parseDouble(config.configElement("PROBABILITY.MUTATION"));
		double pMutateBias = Double.parseDouble(config.configElement("PROBABILITY.MUTATEBIAS"));
		double pWeightReplaced = Double.parseDouble(config.configElement("PROBABILITY.WEIGHT.REPLACED"));
		double excessCoeff = Double.parseDouble(config.configElement("EXCESS.COEFFICIENT"));
		double disjointCoeff = Double.parseDouble(config.configElement("DISJOINT.COEFFICIENT"));
		double weightCoeff = Double.parseDouble(config.configElement("WEIGHT.COEFFICIENT"));
		double threshold = Double.parseDouble(config.configElement("COMPATABILITY.THRESHOLD"));
		double thresholdChange = Double.parseDouble(config.configElement("COMPATABILITY.CHANGE"));
		int inputNodes = Integer.parseInt(config.configElement("INPUT.NODES"));
		int outputNodes =Integer.parseInt(config.configElement("OUTPUT.NODES"));
		boolean naturalOrder = Boolean.valueOf((config.configElement("NATURAL.ORDER.STRATEGY"))).booleanValue();
		int maxSpecieAge = Integer.parseInt(config.configElement("SPECIE.FITNESS.MAX"));
		int specieAgeThreshold = Integer.parseInt(config.configElement("SPECIE.AGE.THRESHOLD"));
		int specieYouthThreshold = Integer.parseInt(config.configElement("SPECIE.YOUTH.THRESHOLD"));
		double agePenalty = Double.parseDouble(config.configElement("SPECIE.OLD.PENALTY"));
		double youthBoost = Double.parseDouble(config.configElement("SPECIE.YOUTH.BOOST"));
		int specieCount = Integer.parseInt(config.configElement("SPECIE.COUNT"));
		double survialThreshold = Double.parseDouble(config.configElement("SURVIVAL.THRESHOLD"));
		boolean featureSelection = Boolean.valueOf(config.configElement("FEATURE.SELECTION")).booleanValue();
		int extraAlleles = Integer.parseInt(config.configElement("EXTRA.FEATURE.COUNT"));
		boolean eleEvents = Boolean.valueOf(config.configElement("ELE.EVENTS")).booleanValue();
		double eleSurvivalCount = Double.parseDouble(config.configElement("ELE.SURVIVAL.COUNT"));
		int eleEventTime = Integer.parseInt(config.configElement("ELE.EVENT.TIME"));
		boolean recurrencyAllowed = Boolean.valueOf(config.configElement("RECURRENCY.ALLOWED")).booleanValue();
		boolean keepBestEver = Boolean.valueOf(config.configElement("KEEP.BEST.EVER")).booleanValue();
		double terminationValue = Double.parseDouble(config.configElement("TERMINATION.VALUE"));
		double maxPerturb = Double.parseDouble(config.configElement("MAX.PERTURB"));
		double maxBiasPerturb = Double.parseDouble(config.configElement("MAX.BIAS.PERTURB"));

		
		NEATGADescriptor descriptor = new NEATGADescriptor();
		descriptor.setPAddLink(pAddLink);
		descriptor.setPAddNode(pAddNode);
		descriptor.setPToggleLink(pToggleLink);
		descriptor.setPMutateBias(pMutateBias);
		descriptor.setPXover(pXover);
		descriptor.setPMutation(pMutation);
		descriptor.setInputNodes(inputNodes);
		descriptor.setOutputNodes(outputNodes);
		descriptor.setNaturalOrder(naturalOrder);
		descriptor.setPopulationSize(popSize);
		descriptor.setDisjointCoeff(disjointCoeff);
		descriptor.setExcessCoeff(excessCoeff);
		descriptor.setWeightCoeff(weightCoeff);
		descriptor.setThreshold(threshold);
		descriptor.setCompatabilityChange(thresholdChange);
		descriptor.setMaxSpecieAge(maxSpecieAge);
		descriptor.setSpecieAgeThreshold(specieAgeThreshold);
		descriptor.setSpecieYouthThreshold(specieYouthThreshold);
		descriptor.setAgePenalty(agePenalty);
		descriptor.setYouthBoost(youthBoost);
		descriptor.setSpecieCount(specieCount);
		descriptor.setPWeightReplaced(pWeightReplaced);
		descriptor.setSurvivalThreshold(survialThreshold);
		descriptor.setFeatureSelection(featureSelection);
		descriptor.setExtraFeatureCount(extraAlleles);
		descriptor.setEleEvents(eleEvents);
		descriptor.setEleSurvivalCount(eleSurvivalCount);
		descriptor.setEleEventTime(eleEventTime);
		descriptor.setRecurrencyAllowed(recurrencyAllowed);
		descriptor.setKeepBestEver(keepBestEver);
		descriptor.setTerminationValue(terminationValue);
		descriptor.setMaxPerturb(maxPerturb);
		descriptor.setMaxBiasPerturb(maxBiasPerturb);
		
		return (descriptor);
	}


	/**
	 * Creates a GA for NEAT evolution based on the descriptor 
	 * @param gaDescriptor
	 * @return created GA
	 */
	public GeneticAlgorithm createGeneticAlgorithm(GADescriptor gaDescriptor) {
		GeneticAlgorithm ga = new NEATGeneticAlgorithmMario((NEATGADescriptor)gaDescriptor);		
		return (ga);
	}

	/**
	 * @see org.neat4j.ailibrary.ga.control.GATrainingManager#createFunction(java.lang.String)
	 */
	public FitnessFunction createFunction(AIConfig config) throws InvalidFitnessFunction {
		String functionClass = config.configElement("OPERATOR.FUNCTION");
		String nnConfigFile;
		FitnessFunction function = null;
		AIConfig nnConfig;
		Class funcClass;
		NEATNetManager netManager;
		NeuralNet net = null;
		NetworkDataSet dataSet = null;
		LearningEnvironment env;
		Constructor fConstructor;
		
		if (functionClass != null) {
			try {
				funcClass = Class.forName(functionClass);
				if (NeuralFitnessFunction.class.isAssignableFrom(funcClass)) {
					nnConfigFile = config.configElement("NN.CONFIG");
					nnConfig  = new NEATLoader().loadConfig(nnConfigFile);
					nnConfig.updateConfig("INPUT_SIZE", config.configElement("INPUT.NODES"));
					nnConfig.updateConfig("OUTPUT_SIZE", config.configElement("OUTPUT.NODES"));
					netManager = new NEATNetManager();
					netManager.initialise(nnConfig);
					net = netManager.managedNet();
					env = net.netDescriptor().learnable().learningEnvironment();
					dataSet = (NetworkDataSet)env.learningParameter("TRAINING.SET");
					fConstructor = funcClass.getConstructor(new Class[]{NeuralNet.class, NetworkDataSet.class});
					function = (FitnessFunction) fConstructor.newInstance(new Object[]{net, dataSet});
				} else {
					throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName());
				}
			} catch (ClassNotFoundException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (IllegalArgumentException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (InstantiationException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (InvocationTargetException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (SecurityException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (NoSuchMethodException e) {
				throw new InvalidFitnessFunction("Invalid function class, " + functionClass + " must extend " + NeuralFitnessFunction.class.getName() + ":" + e.getMessage());
			} catch (InitialisationFailedException e) {
				e.printStackTrace();
				throw new InvalidFitnessFunction("Could not create Firness function, configuration was invalid:" + e.getMessage());
			} 
		} else {
			throw new InvalidFitnessFunction("Function class was null");
		}
		
		return (function);
	}

	/**
	 * @see org.neat4j.ailibrary.ga.control.GATrainingManager#createParentSelector(org.neat4j.ailibrary.AIConfig)
	 */
	public ParentSelector createParentSelector(AIConfig config) throws InvalidParentSelectorFunction {
		String pSelectorClass = config.configElement("OPERATOR.PSELECTOR");
		ParentSelector pSelector;
		
		if (pSelectorClass != null) {
			try {
				pSelector = (ParentSelector)Class.forName(pSelectorClass).newInstance();
			} catch (InstantiationException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			} catch (Exception e) {
				throw new InvalidParentSelectorFunction("Invalid Parent Selector class, " + pSelectorClass + ":" + e.getMessage());
			}
		} else {
			throw new InvalidParentSelectorFunction("Parent Selector class was null");
		}
		
		return (pSelector);
	}

	/**
	 * @see org.neat4j.ailibrary.ga.control.GATrainingManager#createMutator(org.neat4j.ailibrary.AIConfig)
	 */
	public Mutator createMutator(AIConfig config) throws InvalidMutatorFunction {
		String mutatorClass = config.configElement("OPERATOR.MUTATOR");
		Mutator mutator;
		
		if (mutatorClass != null) {
			try {
				mutator = (Mutator)Class.forName(mutatorClass).newInstance();
			} catch (InstantiationException e) {
				throw new InvalidMutatorFunction("Invalid Mutator class, " + mutatorClass + ":" + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidMutatorFunction("Invalid Mutator class, " + mutatorClass + ":" + e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new InvalidMutatorFunction("Invalid Mutator class, " + mutatorClass + ":" + e.getMessage());
			} catch (Exception e) {
				throw new InvalidMutatorFunction("Invalid Mutator class, " + mutatorClass + ":" + e.getMessage());
			}
		} else {
			throw new InvalidMutatorFunction("Mutator class was null");
		}
		
		return (mutator);
	}

	/**
	 * @see org.neat4j.ailibrary.ga.control.GATrainingManager#createCrossOver(org.neat4j.ailibrary.AIConfig)
	 */
	public CrossOver createCrossOver(AIConfig config) throws InvalidCrossoverFunction {
		String xOverClass = config.configElement("OPERATOR.XOVER");
		CrossOver xOver;
		
		if (xOverClass != null) {
			try {
				xOver = (CrossOver)Class.forName(xOverClass).newInstance();
			} catch (InstantiationException e) {
				throw new InvalidCrossoverFunction("Cross Over class, " + xOverClass + ":" + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidCrossoverFunction("Cross Over class, " + xOverClass + ":" + e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new InvalidCrossoverFunction("Cross Over class, " + xOverClass + ":" + e.getMessage());
			} catch (Exception e) {
				throw new InvalidCrossoverFunction("Cross Over class, " + xOverClass + ":" + e.getMessage());
			}
		} else {
			throw new InvalidCrossoverFunction("Cross Over class was null");
		}
		
		return (xOver);
	}

	/**
	 * @see org.neat4j.ailibrary.nn.core.NeuralNet#save(java.lang.String)
	 */
	public boolean save(String fileName, Chromosome genoType) {
		boolean saveOk = false;
		ObjectOutputStream s = null;
		FileOutputStream out = null;
		try {
			if (fileName != null)
			//System.out.println("Saving Best Chromosome to " + fileName);
			out = new FileOutputStream(fileName);
			s = new ObjectOutputStream(out);
			s.writeObject(genoType);
			s.flush();
			saveOk = true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (s != null) {
					s.close();
				}
				
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		//System.out.println("Saving Best Chromosome...Done");
		return (saveOk);
	}

	public static void main(String[] args) {
		NEATGATrainingManagerLoad gam = new NEATGATrainingManagerLoad();
		try {
			if (args.length != 1) {
				System.out.println("Usage: NEATGAManager <ga ga config file");
			} else {
				AIConfig config = new NEATLoader().loadConfig(args[0]);
				gam.initialise(config);
				gam.evolve();
			}
		} catch (InitialisationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
