/*
 * Created on 20-Jun-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.neat4j.neat.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.log4j.Category;
import org.neat4j.neat.core.fitness.MSENEATFitnessFunction;
import org.neat4j.neat.core.mutators.NEATMutator;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.ChromosomeSet;
import org.neat4j.neat.ga.core.CrossOver;
import org.neat4j.neat.ga.core.FitnessFunction;
import org.neat4j.neat.ga.core.GADescriptor;
import org.neat4j.neat.ga.core.GeneticAlgorithm;
import org.neat4j.neat.ga.core.Mutator;
import org.neat4j.neat.ga.core.ParentSelector;
import org.neat4j.neat.ga.core.Population;
import org.neat4j.neat.ga.core.Specie;
import org.neat4j.neat.ga.core.Species;

import ch.idsia.benchmark.tasks.Task;

/**
 * 
 * @author MSimmerson
 *
 */
public class NEATGeneticAlgorithmMario implements GeneticAlgorithm , Serializable{
	private static final long serialVersionUID = 1L;
	private NEATGADescriptor descriptor;
	private NEATMutator mut;
	private FitnessFunction func;
	private ParentSelector selector;
	private CrossOver xOver;
	private Population pop;
	private Chromosome discoveredBest;
	private Chromosome genBest;
	private double genBestdoub;
	private Species specieList;
	private static int specieIdIdx = 1;
	private int eleCount = 0;

	/**
	 * Creates a NEAT GA with behaviour defined by the descriptor
	 * @param descriptor
	 */
	public NEATGeneticAlgorithmMario(NEATGADescriptor descriptor) {
		this.descriptor = descriptor;
		this.specieList = new Species();		
	}
	
	public FitnessFunction gaEvaluator() {
		return (this.func);
	}
	
	public GADescriptor descriptor() {
		return (this.descriptor);
	}
	

	/**
	 * Creates the initial population
	 */
	public void createPopulation() {
		int popSize = this.descriptor.gaPopulationSize();
		int initialChromoSize = this.func.requiredChromosomeSize() + this.descriptor.getExtraFeatureCount();
		this.pop = new NEATPopulation(popSize, initialChromoSize, this.descriptor.getInputNodes(), this.descriptor.getOutputNodes(), this.descriptor.featureSelectionEnabled(), this.descriptor.getExtraFeatureCount());
		this.pop.createPopulation();
	}
	
	
	/**
	 * Loads the pop
	 */
	public void loadPopulationState(String fileName) {
			FileInputStream out = null;
			ObjectInputStream s = null;
			try {
				if (fileName != null) {
					System.out.println("loading Population " + fileName);
					out = new FileInputStream(fileName);
					s = new ObjectInputStream(out);
					this.pop = (NEATPopulation) s.readObject();
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
			
			System.out.println("loading Population...Done");
		
	}
	

	/**
	 * Finds the best performing chromosome from a give evaluation cycle
	 * @param genoTypes - population
	 * @return
	 */
	private Chromosome findBestChromosome(Chromosome[] genoTypes) {
		Chromosome best = genoTypes[0];
		int i;
		
		for (i = 1; i < genoTypes.length; i++) {
			if (this.descriptor.isNaturalOrder()) {
				if (genoTypes[i].fitness() < best.fitness()) {
					best = genoTypes[i];
				}
			} else {
				if (genoTypes[i].fitness() > best.fitness()) {
					best = genoTypes[i];
				}
			}
		}
		
		return (best);
	}

	private void evaluatePopulation(Chromosome[] genoTypes,Task task) {
		int i;
		double eval;
		
		for (i = 0; i < genoTypes.length; i++) {
			eval = ((MSENEATFitnessFunction) this.func).evaluates(genoTypes[i], task);
			genoTypes[i].updateFitness(eval);			
		}
	}
	
	private Chromosome cloneBest(Chromosome best) {
		Chromosome cloneBest = new NEATChromosome(best.genes());
		cloneBest.updateFitness(best.fitness());
		((NEATChromosome)cloneBest).setSpecieId(((NEATChromosome)best).getSpecieId());
		
		return (cloneBest);
	}
	
	private void speciatePopulation(Chromosome[] currentGen) {
		int i;
		int j;
		boolean memberAssigned = false;
		ArrayList currentSpecieList;
		Specie specie;
		
		this.specieList.resetSpecies(this.descriptor.getThreshold());
		
		System.out.println("Compat threshold:" + this.descriptor.getThreshold());
		for (i = 0; i < currentGen.length; i++) {
			if (!memberAssigned) {
				currentSpecieList = this.specieList.specieList();
				j = 0;
				while (!memberAssigned && j < currentSpecieList.size()) {
					specie = (Specie)currentSpecieList.get(j);
					if (specie.addSpecieMember(currentGen[i])) {
						memberAssigned = true;
						//((NEATChromosome)currentGen[i]).setSpecieId(specie.id());
						//System.out.println("Member assigned to specie " + specie.id());
					} else {
						j++;
					}
				}	
				
				if (!memberAssigned) {
					specie = this.createNewSpecie(currentGen[i]);
					this.specieList.addSpecie(specie);
					//((NEATChromosome)currentGen[i]).setSpecieId(specie.id());
					System.out.println("Created new specie, member assigned to specie " + specie.id());
				}
			}
			memberAssigned = false;
		}

	}

	/**
	 * Runs an evaluation and evolution cycle
	 */
	public void runEpoch(Task task) {
		Chromosome[] currentGen = this.pop.genoTypes();
		this.setChromosomeNO(currentGen);
		System.out.println("Evaluating pop");
		this.evaluatePopulation(currentGen, task);
		this.runEvolutionCycle(currentGen);
	}
	
	/**
	 * Runs an evolution cycle on the given population
	 * @param currentGen
	 */
	public void runEvolutionCycle(Chromosome[] currentGen) {
		NEATChromosome champ;
		ArrayList validSpecieList;

		// ELE?
		this.runEle(currentGen);
		// speciate the remaining population
		this.speciatePopulation(currentGen);
		this.genBest = this.findBestChromosome(currentGen);		
		if ((this.discoveredBest == null) || (this.genBest.fitness() >= this.discoveredBest.fitness() && !this.descriptor.isNaturalOrder()) || (this.genBest.fitness() <= this.discoveredBest.fitness() && this.descriptor.isNaturalOrder())) {
			// copy best
			this.discoveredBest = this.cloneBest(this.genBest);
		}
		System.out.println("Best Ever Raw:" + (this.discoveredBest.fitness()) + ":from specie:" + ((NEATChromosome)this.discoveredBest).getSpecieId());		
		System.out.println("Best of Generation is:" + (this.genBest.fitness()) + " specie " + ((NEATChromosome)this.genBest).getSpecieId());
		genBestdoub = this.genBest.fitness();
		// kill any extinct species
		if (this.descriptor.keepBestEver()) {
			champ = (NEATChromosome)this.discoveredBest;
		} else {
			champ = (NEATChromosome)this.genBest;
		}
		this.specieList.removeExtinctSpecies(champ);
		System.out.println("Creating New Gen");
		// spawn new pop	
		this.pop.updatePopulation(this.spawn());
		// Display specie stats
		validSpecieList = this.specieList.validSpecieList(champ.getSpecieId());
		System.out.println("Num species:" + validSpecieList.size());
		if (this.descriptor.getCompatabilityChange() > 0) {
			if (validSpecieList.size() > this.descriptor.getSpecieCount()) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() + this.descriptor.getCompatabilityChange());
			} else if (validSpecieList.size() < this.descriptor.getSpecieCount() && (this.descriptor.getThreshold() > this.descriptor.getCompatabilityChange())) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() - this.descriptor.getCompatabilityChange());
			}
		}
		this.eleCount++;
	}
	
	private void runEle(Chromosome[] currentGen) {
		if (this.descriptor.isEleEvents()) {
			if ((this.eleCount % this.descriptor.getEleEventTime()) == 0 && this.eleCount != 0) {
				System.out.println("Runnig ELE");
				this.descriptor.setThreshold(this.descriptor.getThreshold() * 5);
			} else if ((this.eleCount % this.descriptor.getEleEventTime()) == 1 && this.eleCount != 1) {
				this.descriptor.setThreshold(this.descriptor.getThreshold() / 5.0);
			}
		}
	}
	
	private void setChromosomeNO(Chromosome[] gen) {
		int i;
		
		for (i = 0; i < gen.length; i++) {
			((NEATChromosome)gen[i]).setNaturalOrder(this.descriptor.isNaturalOrder());
		}
	}
	
	private Chromosome[] spawn() {
		Chromosome[] currentGen = this.pop.genoTypes();
		Chromosome[] newGen = new Chromosome[currentGen.length];
		Specie specie = null;
		ChromosomeSet offspring = null;
		int offSpringCount;
		int newGenIdx = 0;
		int i;
		int j = 0;
		double totalAvFitness;
		//int offSp = 0;
		
		// update species by sharing fitness.
		this.specieList.shareFitness();
		totalAvFitness = this.specieList.totalAvSpeciesFitness();
		// mate within valid species to produce new population
		ArrayList species = this.specieList.validSpecieList(((NEATChromosome)this.discoveredBest).getSpecieId());
		for (i = 0; i < species.size(); i++) {
			specie = (Specie)species.get(i);
			if (specie.specieMembers().size() == 0) {
				System.out.println("spawn produced error:");
			}
			// ensure we have enough for next gen
			if (i == (species.size() - 1)) {
				offSpringCount = newGen.length - newGenIdx;
				//offSp = this.specieList.calcSpecieOffspringCount(specie, this.descriptor.gaPopulationSize(), totalAvFitness);
			} else {
				offSpringCount = this.specieList.calcSpecieOffspringCount(specie, this.descriptor.gaPopulationSize(), totalAvFitness);
			}
			
			//System.out.println("Sp[" + specie.id() + "] Age:" + ((NEATSpecie)specie).specieAge() + ":Offspring Sz:" + offSpringCount + ":AvF:" + specie.getAverageFitness() + ":FAge:" + specie.getCurrentFitnessAge() + ":BestF:" + specie.findBestMember().fitness());
			if (offSpringCount > 0) {
				offspring = specie.specieOffspring(offSpringCount, this.mut, this.selector, this.xOver);
				for (j = 0; j < offspring.size(); j++) {
					if (newGenIdx < newGen.length) {
						// if population not full
						newGen[newGenIdx++] = offspring.nextChromosome();
					}
				}
			} else {
				System.out.println("Specie " + specie.id() + ":size:" + specie.specieMembers().size() + " produced no offspring.  Average fitness was " + specie.averageFitness() + " out of a total fitness of " + this.specieList.totalAvSpeciesFitness());
				specie.setExtinct();
			}
		}
				
		return (newGen);
	}
	
	private Specie createNewSpecie(Chromosome member) {
		double excessCoeff = this.descriptor.getExcessCoeff();
		double disjointCoeff = this.descriptor.getDisjointCoeff();
		double weightCoeff = this.descriptor.getWeightCoeff();
		double threshold = this.descriptor.getThreshold();
		
		NEATSpecie specie = new NEATSpecie(threshold, excessCoeff, disjointCoeff, weightCoeff, specieIdIdx++);
		specie.setMaxFitnessAge(this.descriptor.getMaxSpecieAge());
		specie.setAgePenalty(this.descriptor.getAgePenalty());
		specie.setAgeThreshold(this.descriptor.getSpecieAgeThreshold());
		specie.setYouthBoost(this.descriptor.getYouthBoost());
		specie.setYouthThreshold(this.descriptor.getSpecieYouthThreshold());
		specie.setSurvivalThreshold(this.descriptor.getSurvivalThreshold());
		specie.addSpecieMember(member);
		
		return (specie);
	}

	public Chromosome discoverdBestMember() {
		return (this.discoveredBest);
	}
	
	public void resetdiscoverdBestMember() {
		this.discoveredBest = null;
	}
	
	public double genBest() {
		return genBestdoub;
	}

	/** 
	 * @see org.neat4j.ailibrary.ga.core.GeneticAlgorithm#pluginMutator(org.neat4j.ailibrary.ga.core.Mutator)
	 */
	public void pluginMutator(Mutator mut) {
		this.mut = (NEATMutator)mut;
		this.mut.setPAddLink(this.descriptor.getPAddLink());
		this.mut.setPAddNode(this.descriptor.getPAddNode());
		this.mut.setPPerturb(this.descriptor.getPMutation());
		this.mut.setPToggle(this.descriptor.getPToggleLink());
		this.mut.setPWeightReplaced(this.descriptor.getPWeightReplaced());
		this.mut.setFeatureSelection(this.descriptor.featureSelectionEnabled());
		this.mut.setRecurrencyAllowed(this.descriptor.isRecurrencyAllowed());
		this.mut.setPMutateBias(this.descriptor.getPMutateBias());
		this.mut.setBiasPerturb(this.descriptor.getMaxBiasPerturb());
		this.mut.setPerturb(this.descriptor.getMaxPerturb());
	}

	/**
	 * @see org.neat4j.ailibrary.ga.core.GeneticAlgorithm#pluginFitnessFunction(org.neat4j.ailibrary.ga.core.Function)
	 */
	public void pluginFitnessFunction(FitnessFunction func) {
		this.func = func;
	}

	/**
	 * @see org.neat4j.ailibrary.ga.core.GeneticAlgorithm#pluginParentSelector(org.neat4j.ailibrary.ga.core.ParentSelector)
	 */
	public void pluginParentSelector(ParentSelector selector) {
		this.selector = selector;
		this.selector.setOrderStrategy(this.descriptor.isNaturalOrder());
	}

	/**
	 * @see org.neat4j.ailibrary.ga.core.GeneticAlgorithm#pluginCrossOver(org.neat4j.ailibrary.ga.core.CrossOver)
	 */
	public void pluginCrossOver(CrossOver xOver) {
		this.xOver = xOver;
		this.xOver.setProbability(this.descriptor.getPXover());
	}

	/**
	 * Saves the entire population.  Especially useful for long running evolution processes
	 */
	public void savePopulationState(String fileName) {
		FileOutputStream out = null;
		ObjectOutputStream s = null;
		try {
			if (fileName != null) {
				System.out.println("Saving Population " + fileName);
				out = new FileOutputStream(fileName);
				s = new ObjectOutputStream(out);
				s.writeObject((NEATPopulation)this.pop);
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
		
		System.out.println("Saving Population...Done");
	}
	
	
	
	/**
	 * Loads the pop
	 */
	public void loadSpeciesState(String fileName) {
			FileInputStream out = null;
			ObjectInputStream s = null;
			try {
				if (fileName != null) {
					System.out.print("loading species " + fileName);
					out = new FileInputStream(fileName);
					s = new ObjectInputStream(out);
					this.specieList = (Species) s.readObject();
					
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
			specieIdIdx = Collections.max(this.specieList.specieList(),new Comparator<Specie>(){
				public int compare(Specie a, Specie b){
					return a.id() - b.id();
				}
			}).id() + 1;

			System.out.print("loading species...Done");
		
	}
	
	
	
	/**
	 * Saves the entire population.  Especially useful for long running evolution processes
	 */
	public void saveSpeciesState(String fileName) {
		FileOutputStream out = null;
		ObjectOutputStream s = null;
		try {
			if (fileName != null) {
				System.out.print("Saving species " + fileName);
				out = new FileOutputStream(fileName);
				s = new ObjectOutputStream(out);
				s.writeObject(this.specieList);
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
		
		System.out.print("Saving species...Done");
	}
	
	

	public Population population() {
		return (this.pop);
	}
	
	public Chromosome generationBest() {
		return (this.genBest);
	}

	@Override
	public void runEpoch() {
		// TODO Auto-generated method stub
		
	}
}
