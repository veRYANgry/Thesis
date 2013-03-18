package org.neat4j.neat.applications.train;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JCheckBox;



import junit.framework.Test;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATChromosome;
import org.neat4j.neat.core.NEATFeatureGene;
import org.neat4j.neat.core.NEATGeneticAlgorithmMario;
import org.neat4j.neat.core.NEATLinkGene;
import org.neat4j.neat.core.NEATLoader;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.core.NEATNodeGene;
import org.neat4j.neat.core.NEATSelfRegulationGene;
import org.neat4j.neat.ga.core.Chromosome;
import org.neat4j.neat.ga.core.Gene;
import org.neat4j.neat.ga.core.GeneticAlgorithm;
import org.neat4j.neat.ga.core.Specie;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.NeatAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.engine.mapedit.LevelEditor;
import ch.idsia.benchmark.tasks.ProgressTask;
import ch.idsia.benchmark.tasks.Task;
import ch.idsia.tools.MarioAIOptions;

public class realtimeInterface extends JFrame implements ActionListener {
	
	private static boolean IsPaused;
	private static int runNumber = 0, GenNumber = 0;
	
	static SwingWorker<Void, Void> worker;

	private static String[][] StatData;
	static String[] StatDataHeading = {"Run name", "Best Fit", "Best Fit Gen" , "Generations (Age)"};
	static JTable StatDataTable;
	
	private static String[][] queueData;
	static String[] queueDataHeading = {"Levels"};
	static JTable queueDataTable;

	private static String[][] specData;
	static String[] SpecDataHeading = {"Species" , "Species members"};
	static JTable SpecDataTable;
	static int currentSpecies;
	
	private static String[][] specMemberData;
	static String[] SpecMemberDataHeading = {"Species member" , "Adjusted Fitness","self fitness", "Genes"};
	static JTable SpecMemberDataTable;
	
	private static String[][] ChromosomeData;
	private static  JTable ChromosomeDataTable;
	static String[] ChromosomeDataHeading = {"Type" , "Innovation #", "ID #","Wieght / Bias", "From","To"};
	
	private static String[][] GeneData;
	private static  JTable GeneDataTable;
	static String[] GeneDataHeading = {"Type" , "Innovation #", "Max specie age","pAddLink", "pAddNode", "pToggleLink","pMutation","pMutateBias","pWeightReplaced","maxPerturb","maxBiasPerturb",
		"disjointCoeff", "excessCoeff","weightCoeff","pMutatateRegulation","pMutatateRegulationHueristics","pMutatateRegulationCoeff","pMutatateRegulationMutation","pMutatateRegulationAgeing","maxPerturbRegulation"
		,"DistanceHeuristic", "MushroomHeuristic","FlowerHeuristic","CoinsHeuristic", "ShellKillHeuristic", "StompKillHeuristic","ConnectionHeuristic", "NeuronHeuristic", "Survival threshold" };
	
	static int LevelModeIndex = 0;
	
	ArrayList<JTextField> OptionsBoxes = new ArrayList<JTextField>();
	ArrayList<String> OptionsBoxesHash = new ArrayList<String>();
	
	ArrayList<JTextField> HeuristicBoxes = new ArrayList<JTextField>();
	
	JLabel RunningStatus;
	
	JLabel GenerationLabel;
	
	String RunningPausedNotification = "Paused";
	JTextField SeedText;
	///////////////////////////////
	//NEAT stuff
	///////////////////////////////
	static NEATGATrainingManager gam;
	private static NEATGeneticAlgorithmMario ga;
	private static AIConfig configMario;
	private static AIConfig configTetris;
	
	private static Chromosome DemoMember;
	private static AIConfig configs;
	private static VisionBound Vision = new VisionBound(-3,3,-5,3);
	private static int extraFeatures = 4;
	///////////////////////////////
	//Mario testbed stuff
	///////////////////////////////
	static ProgressTask task;
	static MarioAIOptions options = new MarioAIOptions("");
	static int difficulty = 0;
	static int seed = 0;
	static Random rand = new Random(System.currentTimeMillis());
	static String levelName = "resources/test.lvl";
	
	private double DistanceHeuristic = 1, MushroomHeuristic = 0, FlowerHeuristic = 0,  CoinsHeuristic = 0, StompKillsHeuristic = 200,ShellKillHeuristic = 500;
	private double ConnectionHeuristic = 0, NeuronHeuristic = 0;

	
	////////////
	//Demo stuff
	////////////
	SwingWorker<Void, Void> demoWorker;
	
	
	////////////
	//Level stuff
	////////////
	Vector<MarioAIOptions> levelQueue = new Vector<MarioAIOptions>(); //should be a vector of game options or parts of data set
	
	////////////
	//Statistics stuff
	////////////
	Vector<runStatistics> levelStat = new Vector<runStatistics>();
	static int selectedRun;
	static XYSeriesCollection dataset;
	static XYSeriesCollection Bestdataset;
	static XYSeriesCollection Speciesdataset;
	
	////////////
	//AutoRun stuff
	////////////
	static boolean Autorun = true;
	static int AutoRunMode;
	JTextField GenText;
	JTextField ScoreText;
	//mario
	static int GenerationLimit = 100;
	static int ScoreLimit = 6000;
	
	////////////
	//Run type stuff
	////////////
	static int runType = 0;
	
	/**
	 * @param args
	 */

	public static void main(final String[] args) {

		configMario = configs = new NEATLoader().loadConfig("xor_neat.ga");
		configTetris = new NEATLoader().loadConfig("xor_tetris.ga");
		options = new MarioAIOptions("");
        options.setFPS(options.globalOptions.MaxFPS);
        options.setVisualization(false);
        task = new ProgressTask(options);
    	seed = rand.nextInt();
    	dataset = new XYSeriesCollection();
    	Bestdataset = new XYSeriesCollection();
        Speciesdataset = new XYSeriesCollection();
    	
		configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd) * (Vision.YVisionStart - Vision.YVisionEnd) +  extraFeatures));

		new realtimeInterface();
	}
	
	public class loopWorker extends SwingWorker<Void, Void>{
		private mainWorker workerThread;
		public boolean isDone = true;
		@Override
		protected Void doInBackground() throws Exception {
			while(this.isDone){
				if(runNumber > 1){
					break;
				}
				
				if(workerThread == null || workerThread.isDone()){
					workerThread = new mainWorker();
				InnovationDatabase.databaseReset();

				//lets shove the config from the old main class into this one

				gam = new NEATGATrainingManager();
				try {
					gam.initialise(configs);
				} catch (InitialisationFailedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ga = (NEATGeneticAlgorithmMario)gam.ga();
				configs = gam.GetConfig();

				workerThread.execute();
				}
				
	               try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

					return null;
				} 
	               
	            
			}
			return null;
		}
		
	}
	
	public class mainWorker extends SwingWorker<Void, Void> {
		private int gen = 0;
		@Override
		public Void doInBackground() {
			runNumber++;
			levelStat.add(new runStatistics("" + runNumber,0,0));
			
			while (true) {
		        GenNumber = gen;
				setOptions(options);
		    //    System.out.println("Running Epoch[" + gen + "] with diff:" + difficulty);
		        
				switch(runType){
				case 0:
					((NEATGeneticAlgorithmMario)ga).runEpoch(task,Vision);
					break;
				case 1:
					((NEATGeneticAlgorithmMario)ga).runEpochTetris();
					break;
				default:
					break;
				} 

				
				/////////////////////////////////////////
				//Gui Process stuff
				/////////////
				//pause to read results
				if(IsPaused){
					RunningPausedNotification = "Paused";
				}
				//Get results
				publish();		
				
				while(IsPaused){

		               try {
						Thread.sleep(500);
					} catch (InterruptedException e) {

						return null;
					}
				}
				RunningPausedNotification = "Running";
				//////////////////////////////////////////
				if(Autorun){
					switch(AutoRunMode){
					case 0:
						if(gen >= GenerationLimit){
							return null;
						}
						break;
					case 1:
						if(ga.discoverdBestMember().fitness() >= ScoreLimit){
							return null;
						}
						break;
					default:
						break;

					} 
				}
					
				//TODO this will not work for autorun
				if(isCancelled())
					return null;

				gen++;
			}
			
			//return null;
		}
		
		
		@Override
		protected void process(List<Void> t) {
			RunningStatus.setText(RunningPausedNotification);
			GenerationLabel.setText("Current Generation: " + GenNumber);
			
			//specData[0][0] = Integer.toString(ga.population().genoTypes().length);
			specData = new String[ga.GetSpecies().specieList().size()][2];
			for(int i = 0; i < ga.GetSpecies().specieList().size() ; i++){
				specData[i][0] = Integer.toString(((Specie)ga.GetSpecies().specieList().get(i)).id());
				specData[i][1] = Integer.toString(((Specie)ga.GetSpecies().specieList().get(i)).specieMembers().size());
			}
			                       
			SpecDataTable.setModel(new DefaultTableModel(specData,SpecDataHeading));
			SpecDataTable.updateUI();
			
			////////////////Stats gathering /////////////
			
			if(dataset.getSeries().size() < runNumber){
				dataset.addSeries(new XYSeries(runNumber));
				Bestdataset.addSeries(new XYSeries(runNumber));
		        Speciesdataset.addSeries(new XYSeries(runNumber));
			}
				
			XYSeries tempset = (XYSeries) dataset.getSeries().get(runNumber - 1);
			tempset.add(GenNumber , ga.GetSpecies().totalAvSpeciesFitness() / ga.GetSpecies().specieList().size());

			
			tempset = (XYSeries) Bestdataset.getSeries().get(runNumber - 1);
			tempset.add(GenNumber , ga.genBest());
	
			
			tempset = (XYSeries) Speciesdataset.getSeries().get(runNumber - 1);
			tempset.add(GenNumber , ga.GetSpecies().specieList().size());
			
			
			runStatistics run = levelStat.get(runNumber - 1);
			if(ga.discoverdBestMember() != null)
				if(run.getBestFitness() != ga.discoverdBestMember().fitness()){
					run.setBestFitness(ga.discoverdBestMember().fitness());
					run.setBestchrome(ga.discoverdBestMember());
					run.setBestFitGen(GenNumber);
					run.setAiconfig(configs);
					run.setSeed(seed);
					run.setLevelModeIndex(LevelModeIndex);
					run.setLevelName(levelName);
					run.setDifficulty(difficulty);
					
				}
			
			run.setGeneration(GenNumber);
			/////////////////////////////////////////////
			StatisticsTableUp();
			
			
			
		}

		/*
		 * @Override public void done() { ; }
		 */
	}

	
	public void actionPerformed(ActionEvent e) {
		if(!Autorun){
		  callRunWith(this.worker);
		}
		else {
	      callRunWithLoop(this.worker);
		}

	}
	
	public void callRunWith(SwingWorker work){
		if(work != null && !work.isDone()){

			work.cancel(true);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(work == null || work.isDone()){
			work = new mainWorker();
		InnovationDatabase.databaseReset();

		//lets shove the config from the old main class into this one

		gam = new NEATGATrainingManager();
		try {
			gam.initialise(configs);
		} catch (InitialisationFailedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ga = (NEATGeneticAlgorithmMario)gam.ga();
		configs = gam.GetConfig();

		work.execute();
		}
	}
	
	public void callRunWithLoop(SwingWorker work){
		if(work != null && !work.isDone()){

			work.cancel(true);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(work == null || work.isDone()){
			work = new loopWorker();

		work.execute();
		}
	}

	public realtimeInterface() {
		IsPaused = false;
		
		this.setSize(800, 800);
		setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("NEAT Mario Nets Alpha2");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel runPanel = new JPanel(false);
        runPanel.setLayout(new FlowLayout());
        JPanel optionsPanel = new JPanel(false);
        optionsPanel.setLayout(new FlowLayout());
        JPanel graphPanel = new JPanel(false);
        graphPanel.setLayout(new FlowLayout());
        
        tabbedPane.add("Run Info" , runPanel);
        tabbedPane.add("Options" , optionsPanel);
        tabbedPane.add("Graph" , graphPanel);
        
		Container content = this.getContentPane();
	    content.setLayout(new FlowLayout());
	    
	    
	    content.add(tabbedPane);
	    
	    JPanel GridPanel = new JPanel(new FlowLayout());
	
		

		Gamemode(GridPanel);
		
		
		JPanel StackPanel = new JPanel(new GridLayout(4, 2));
		
		StackPanel.add(GridPanel);
		SpeciesBoxes(StackPanel);
		
		runPanel.add(StackPanel);
		
		GeneInfo(StackPanel);
		
	    Graphs(graphPanel);
		
		
		JPanel SidePanel = new JPanel(new GridLayout(0, 1));
		StatisticBox(SidePanel);
		levelQueue(SidePanel);
		levelOptions(SidePanel);
		NextRunOptions(SidePanel);
		content.add(SidePanel);
		
		JPanel StackPanel2 = new JPanel(new GridLayout(0, 1));
		
		NEATConfig(StackPanel2);
		FitnessFunction(StackPanel2);
		optionsPanel.add(StackPanel2);
		
		VisionRange(optionsPanel);
		CheckBoxRunOptions(optionsPanel);
		
		this.pack();

	}
	
	public void Graphs(final Container content){
		
        JTabbedPane tabbedPane = new JTabbedPane();
        
        
        JPanel BestPanel = new JPanel(false);
        BestPanel.setLayout(new FlowLayout());
        
        JPanel SpeciesPanel = new JPanel(false);
        SpeciesPanel.setLayout(new FlowLayout());
        
        JPanel FitPanel = new JPanel(false);
        BestPanel.setLayout(new FlowLayout());
        
        tabbedPane.add("Best Fitness" , BestPanel);
        tabbedPane.add("Number of species" , SpeciesPanel);
        tabbedPane.add("Total Fitness" , FitPanel);
        
        content.add(tabbedPane);
        
		chartFitness fitnessChart = new chartFitness(dataset, "Average Fitness", "Generation", "Ave. Fitness");
		FitPanel.add(fitnessChart.chartPanel);
		
		chartFitness bestFitnessChart = new chartFitness(Bestdataset, "Best fitness", "Generation", "Fitness");
		BestPanel.add(bestFitnessChart.chartPanel);
		
		chartFitness speciesChart = new chartFitness(Speciesdataset, "Number of species", "Generation", "Species");
		SpeciesPanel.add(speciesChart.chartPanel);
		
		JButton b1 = new JButton("Save graph");
		b1.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < dataset.getSeries().size();i++){
					
					XYSeries  temp = (XYSeries) dataset.getSeries().get(i);
					FileWriter fstream;
					try {
						fstream = new FileWriter(System.currentTimeMillis() + "" + i +  "totalfitdataset.txt");
						BufferedWriter out = new BufferedWriter(fstream);
					
					for(int j = 0; j < temp.getItems().size();j++){
						out.append(temp.getX(j).toString());
						out.append("  ");
						out.append(temp.getY(j).toString());
						out.append("\n");
					}
					out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		FitPanel.add(b1);
		
		
		
		JButton b2 = new JButton("Save graph");
		b2.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < Bestdataset.getSeries().size();i++){
					
					XYSeries  temp = (XYSeries) Bestdataset.getSeries().get(i);
					FileWriter fstream;
					try {
						fstream = new FileWriter(System.currentTimeMillis() + "" + i +  "Bestdataset.txt");
						BufferedWriter out = new BufferedWriter(fstream);
					
					for(int j = 0; j < temp.getItems().size();j++){
						out.append(temp.getX(j).toString());
						out.append("  ");
						out.append(temp.getY(j).toString());
						out.append("\n");
					}
					out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		BestPanel.add(b2);
		
		JButton b3 = new JButton("Save graph");
		b3.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < Speciesdataset.getSeries().size();i++){
					
					XYSeries  temp = (XYSeries) Speciesdataset.getSeries().get(i);
					FileWriter fstream;
					try {
						fstream = new FileWriter(System.currentTimeMillis() + "" + i +  "Speciesdataset.txt");
						BufferedWriter out = new BufferedWriter(fstream);
					
					for(int j = 0; j < temp.getItems().size();j++){
						out.append(temp.getX(j).toString());
						out.append("  ");
						out.append(temp.getY(j).toString());
						out.append("\n");
					}
					out.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		SpeciesPanel.add(b3);
	}
	
	
	public void Gamemode(final Container content){
		//chromosome data
	    RunningStatus = new JLabel(RunningPausedNotification);
	    content.add(RunningStatus);
	    
		JButton b1 = new JButton("Start New Run");
		b1.addActionListener(this);
		content.add(b1);
		
		JButton b2 = new JButton("Pause on next break");
		b2.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				IsPaused = !IsPaused;
			}
		});
		content.add(b2);
		
		JRadioButton setMario = new JRadioButton("Mario mode");
		setMario.setSelected(false);
		
		setMario.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				runType = 0;
				configs = configMario;
			}
		});
		
		
		JRadioButton setTetris = new JRadioButton("Tetris mode");
		setMario.setSelected(true);
		
		setTetris.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				runType = 1;
				configs = configTetris;
			}
		});
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(setTetris);
		group.add(setMario);
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(setTetris);
		radioPanel.add(setMario);
		
		content.add(radioPanel);
		
	}
	
	public void GeneInfo(final Container content){
		//chromosome data
		GeneData = new String[1][GeneDataHeading.length];
		 GeneDataTable = new JTable(GeneData,GeneDataHeading);
		JScrollPane GenePane = new JScrollPane(GeneDataTable, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		GenePane.setPreferredSize(new Dimension(400, 50));
		((JTable) GeneDataTable).setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		content.add(GenePane);
	}
	
	public void CheckBoxRunOptions(final Container content){
		JCheckBox enableSelfRegulation = new JCheckBox("Enable Self Regulation Gene");
		enableSelfRegulation.setSelected(false);
		enableSelfRegulation.addItemListener(new  ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {

		        if (e.getStateChange() == ItemEvent.DESELECTED) {
		        	configs.updateConfig("SELF.REG",Boolean.toString(false));
		        }else {
		        	configs.updateConfig("SELF.REG",Boolean.toString(true));
		        }
			}
		});
		
		JCheckBox enableAutoSpecie = new JCheckBox("Enable Auto Dynamic Speciation");
		enableAutoSpecie.setSelected(false);
		enableAutoSpecie.addItemListener(new  ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {

		        if (e.getStateChange() == ItemEvent.DESELECTED) {
		        	configs.updateConfig("DYN.SPCIE",Boolean.toString(false));
		        }else {
		        	configs.updateConfig("DYN.SPCIE",Boolean.toString(true));
		        }
			}
		});
		
		
		
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(enableSelfRegulation);
		radioPanel.add(enableAutoSpecie);
		content.add(radioPanel);
	}

	
	public void SpeciesBoxes(final Container content){
		//species data table
		specData = new String[10][2];
	    SpecDataTable = new JTable(specData,SpecDataHeading);
		JScrollPane SpecieInfo = new JScrollPane(SpecDataTable);
		SpecieInfo.setPreferredSize(new Dimension(300, 200));

		
		content.add(SpecieInfo);
		
		//species member data
		
		specMemberData = new String[10][4];
		SpecMemberDataTable = new JTable(specMemberData,SpecMemberDataHeading);
		JScrollPane SpecieMemberInfo = new JScrollPane(SpecMemberDataTable);
		SpecieMemberInfo.setPreferredSize(new Dimension(300, 200));
		
		//chromosome data

		ChromosomeDataTable = new JTable(new String[10][6],ChromosomeDataHeading);
		JScrollPane ChromosomePane = new JScrollPane(ChromosomeDataTable);
		ChromosomePane.setPreferredSize(new Dimension(300, 200));
		
		content.add(ChromosomePane);
		
		
		//update on species click
		SpecDataTable.addMouseListener( new MouseAdapter() {
	          public void mouseClicked(MouseEvent e) {
	        	  
	              int RecentClicked;
	              try
	              {
	            	  RecentClicked = SpecDataTable.getSelectedRow();
	            	  if(RecentClicked < ga.GetSpecies().specieList().size()){
	            		  currentSpecies = RecentClicked;
	            		  specMemberData = new String[((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size()][4];
	            		  ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size();
	            		  
	      				for(int i = 0; i < ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size() ; i++){
	      					specMemberData[i][0] = Integer.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).getSpecieId() );
	      					specMemberData[i][1] = Double.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).fitness());
	      					specMemberData[i][2] = Double.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).getSelfFitness());
	      					specMemberData[i][3] = Integer.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).genes().length);
	    				}
	    				                       
	      				SpecMemberDataTable.setModel(new DefaultTableModel(specMemberData,SpecMemberDataHeading));
	      				SpecMemberDataTable.updateUI();
	            		  
	            	  }
	              }
	              catch(Exception x)
	              {}
	              
	          }
		});
		
		content.add(SpecieMemberInfo);
		
		//run clicked species member for a demo
		JButton b3 = new JButton("View demo of selected member");
		b3.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {

				if(DemoMember != null)
				{
					//while(true){
						Thread threadWorker = new Thread() {
							Chromosome tempChrome = DemoMember;
							@Override
							public void run() {

								MarioAIOptions WorkerOptions = new MarioAIOptions("");
								
								WorkerOptions.setLevelDifficulty(difficulty);
								WorkerOptions.setFPS(32);
								WorkerOptions.setVisualization(true);
								setOptions(WorkerOptions);
								ProgressTask WorkerTask = new ProgressTask(WorkerOptions);
								
						        NeuralNet nets = null;
								try {
									nets = gam.createNet(configs);
								} catch (InitialisationFailedException a) {
									// TODO Auto-generated catch block
									a.printStackTrace();
								}
								
								((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(tempChrome);
								((NEATNeuralNet)nets).updateNetStructure();
								
								NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
								frame.setTitle("Demo");
								frame.showNet();
						        
								WorkerTask.evaluateAll((Agent) new NeatAgent(nets, Vision, null));
								
								 
								
								return;
							}
						};

							threadWorker.start();


				}
				else{
					System.out.println("Chromosome is null something is wrong");
				}

			}
		});
		 JPanel FlowPanel1 = new JPanel(new FlowLayout());
		 JPanel FlowPanel2 = new JPanel(new FlowLayout());
		 FlowPanel1.add(b3);
		 
		JButton DemoGenBestButton = new JButton("View demo of best member this gen");
		DemoGenBestButton.addActionListener(new  ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Chromosome tempChrome = ga.generationBest();
					switch(runType){
					case 0:
						if(levelQueue != null){
							Thread threadworker = new ThreadDemo(tempChrome, Vision,
                                    gam, configs, task);
							threadworker.start();
						}else{
							Thread threadworker = new ThreadDemo(tempChrome, difficulty,
									new MarioAIOptions(), Vision,
									gam, configs,LevelModeIndex,
									seed, levelName);
							threadworker.start();
						}
						break;
					case 1:
						Thread threadworker2 = new ThreadDemoTetris(gam, configs,tempChrome);
						threadworker2.start();
						break;
					default:
						break;
					} 
				}
				
			});
		
		FlowPanel2.add(DemoGenBestButton);
		 
		content.add(FlowPanel1);
		content.add(FlowPanel2);
		
		
		SpecMemberDataTable.addMouseListener( new MouseAdapter() {
	          public void mouseClicked(MouseEvent e) {
	        	  
	              int RecentClicked;
	              try
	              {
	            	  RecentClicked = SpecMemberDataTable.getSelectedRow();
	            	  if(RecentClicked < ((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().size()){
	            		  DemoMember = (Chromosome) ((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().get(RecentClicked);
	    
	            		  ChromosomeData = new String[((NEATChromosome)((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().get(RecentClicked)).genes().length][6];
	            		  if( ((NEATChromosome)DemoMember).findActiveReg() == null)
	            				 System.out.println("nothing here!!!");

		      				for(int i = 0; i < ((NEATChromosome)((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().get(RecentClicked)).genes().length ; i++){
		      					Gene chrome =((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().get(RecentClicked)).genes()[i];
		      					if(chrome.getClass() == NEATNodeGene.class){
		      						ChromosomeData[i][0] = "NEATNodeGene";
		      						ChromosomeData[i][1] = Integer.toString(((NEATNodeGene)chrome).getInnovationNumber());
		      						ChromosomeData[i][2] = Integer.toString(((NEATNodeGene)chrome).id());
		      						
		      						ChromosomeData[i][3] = Double.toString(((NEATNodeGene)chrome).bias());
		      					}
		      					else if(chrome.getClass() == NEATLinkGene.class){
		      						ChromosomeData[i][0] = "NEATLinkGene";
		      						ChromosomeData[i][1] = Integer.toString(((NEATLinkGene)chrome).getInnovationNumber());
		      						
		      						ChromosomeData[i][3] = Double.toString(((NEATLinkGene)chrome).getWeight());
		      						ChromosomeData[i][4] = Integer.toString(((NEATLinkGene)chrome).getFromId());
		      						ChromosomeData[i][5] = Integer.toString(((NEATLinkGene)chrome).getToId());
		      					} 
		      					else if (chrome.getClass() == NEATFeatureGene.class){
		      						ChromosomeData[i][0] = "NEATFeatureGene";
		      						ChromosomeData[i][1] = Integer.toString(((NEATFeatureGene)chrome).getInnovationNumber());
		      					} else if(chrome.getClass() == NEATSelfRegulationGene.class){
		      						GeneData[0][0] =ChromosomeData[i][0] = "NEATSelfRegulationGene";
		      						GeneData[0][1] =ChromosomeData[i][1] = Integer.toString(((NEATSelfRegulationGene)chrome).getInnovationNumber());
		      						GeneData[0][2] =ChromosomeData[i][3] =  Integer.toString(((NEATSelfRegulationGene)chrome).getMaxSpecieAge());
		      						GeneData[0][3] =ChromosomeData[i][4] =  Double.toString(((NEATSelfRegulationGene)chrome).getpAddLink());
		      						
		      					    GeneData[0][4] = Double.toString(((NEATSelfRegulationGene)chrome).getpAddNode());
		      					    GeneData[0][5] = Double.toString(((NEATSelfRegulationGene)chrome).getpToggleLink());
		      					    GeneData[0][6] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutation());
			      					GeneData[0][7] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutateBias());
			      					GeneData[0][8] = Double.toString(((NEATSelfRegulationGene)chrome).getpWeightReplaced());
			      					GeneData[0][9] = Double.toString(((NEATSelfRegulationGene)chrome).getMaxPerturb());
			      					GeneData[0][10] = Double.toString(((NEATSelfRegulationGene)chrome).getMaxBiasPerturb());
			      					GeneData[0][11] = Double.toString(((NEATSelfRegulationGene)chrome).getDisjointCoeff());
			      					GeneData[0][12] = Double.toString(((NEATSelfRegulationGene)chrome).getExcessCoeff());
			      					GeneData[0][13] = Double.toString(((NEATSelfRegulationGene)chrome).getWeightCoeff());
			      					GeneData[0][14] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutatateRegulation());
			      					GeneData[0][15] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutatateRegulationHueristics());
			      					GeneData[0][16] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutatateRegulationCoeff());
			      					GeneData[0][17] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutatateRegulationMutation());
			      					GeneData[0][18] = Double.toString(((NEATSelfRegulationGene)chrome).getpMutatateRegulationAgeing());
			      					GeneData[0][19] = Double.toString(((NEATSelfRegulationGene)chrome).getMaxPerturbRegulation());
			      					
			      					GeneData[0][20] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[0]);
			      					GeneData[0][21] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[1]);
			      					GeneData[0][22] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[2]);
			      					GeneData[0][23] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[3]);
			      					GeneData[0][24] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[4]);
			      					GeneData[0][25] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[5]);
			      					GeneData[0][26] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[6]);
			      					GeneData[0][27] = Double.toString(((NEATSelfRegulationGene)chrome).getHueristics().get(0)[7]);
			      					
			      					GeneData[0][28] = Double.toString(((NEATSelfRegulationGene)chrome).getSurvivalThreshold());
		      						
		      						GeneDataTable.setModel(new DefaultTableModel(GeneData,GeneDataHeading));
		      						GeneDataTable.updateUI();
		      						
		      					}
		      					
		    				}
		    				                       
		      				ChromosomeDataTable.setModel(new DefaultTableModel(ChromosomeData,ChromosomeDataHeading));
		      				ChromosomeDataTable.updateUI();
	            		  
	            	  }
	              }
	              catch(Exception x)
	              {}
	              
	          }
		});
	}
	
	//set up options for the next run according to set parameters
	public static void setOptions(MarioAIOptions options){
		
		switch(LevelModeIndex){
		case 0:
	        options.setLevelDifficulty(difficulty);
	        options.setArgs("-ls " + seed);
			break;
		case 1:
			options.setArgs("-ls " + levelName);
			break;
		case 2:
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
	
	//automatically run a series of runs with constraints
	void AutorunBox(final Container content){
		
		JPanel StatsPanel = new JPanel(new GridLayout(0, 1));
	     GenerationLabel = new JLabel("Current Generation: 0"); 
		
		StatsPanel.add(GenerationLabel);

		
		StatData = new String[10][4];
		StatDataTable = new JTable(StatData,StatDataHeading);
		JScrollPane StatPane = new JScrollPane(StatDataTable);
		StatPane.setPreferredSize(new Dimension(400, 50));
		StatsPanel.add(StatPane);
		
		content.add(StatsPanel);
	}
	
	
	public void setQueueLevels(){
		queueData = new String[levelQueue.size()][1];
		int i = 0;
		for(MarioAIOptions Op: levelQueue){
			queueData[i][0] = Op.asString();
		    i++;
		}
		task.levelQueue = levelQueue;
		queueDataTable.setModel(new DefaultTableModel(queueData,queueDataHeading));
		queueDataTable.updateUI();
	}
	
	public void StatisticsTableUp(){
		StatData = new String[levelStat.size()][StatDataHeading.length];
		for(int i = 0; i < levelStat.size();i++){
			StatData[i][0] = levelStat.get(i).getRunName();
			StatData[i][1] = Double.toString(levelStat.get(i).getBestFitness());
			StatData[i][2] = Double.toString(levelStat.get(i).getBestFitGen());
			StatData[i][3] = Integer.toString(levelStat.get(i).getGeneration());
		}
		StatDataTable.setModel(new DefaultTableModel(StatData,StatDataHeading));
		StatDataTable.updateUI();
	}
	
	//Display stats
	public void StatisticBox(final Container content){
		
		JPanel StatsPanel = new JPanel(new GridLayout(0, 1));
	     GenerationLabel = new JLabel("Current Generation: 0"); 
		
		StatsPanel.add(GenerationLabel);

		
		StatData = new String[10][4];
		StatDataTable = new JTable(StatData,StatDataHeading);
		JScrollPane StatPane = new JScrollPane(StatDataTable);
		StatPane.setPreferredSize(new Dimension(400, 50));
		StatsPanel.add(StatPane);
		
		
		StatDataTable.addMouseListener( new MouseAdapter() {
	          public void mouseClicked(MouseEvent e) {
	              try
	              {
	            	  selectedRun = StatDataTable.getSelectedRow();
	              }
	              catch(Exception x)
	              {}
	          }
		});
		
				
		JButton DemoButton = new JButton("View best of selected run");
		StatsPanel.add(DemoButton);
		DemoButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(levelStat.size() > selectedRun ){
				runStatistics run = levelStat.get(selectedRun);
				Thread threadworker = new ThreadDemo(run.getBestchrome(), run.getDifficulty(),
						new MarioAIOptions(), Vision,
						gam, run.getAiconfig(),run.getLevelModeIndex(),
						run.getSeed(), run.getLevelName());
				threadworker.start();
				}
				
			}
		});
		
		
		
		content.add(StatsPanel);
	}
	
	
	//used to set up a series of levels and display them
	public void levelQueue(final Container content){
		JPanel QueuePanel = new JPanel(new GridLayout(0, 1));
		
		
		queueData = new String[10][1];
		queueDataTable = new JTable(queueData,queueDataHeading);
		JScrollPane queuePane = new JScrollPane(queueDataTable);
		queuePane.setPreferredSize(new Dimension(400, 50));
		QueuePanel.add(queuePane);
		
		
		JButton AddLevelButton = new JButton("Add level");
		QueuePanel.add(AddLevelButton);
		AddLevelButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				MarioAIOptions WorkerOptions = new MarioAIOptions("");
				WorkerOptions.setLevelDifficulty(difficulty);
				WorkerOptions.setFPS(options.globalOptions.MaxFPS);
				WorkerOptions.setVisualization(false);
				setOptions(WorkerOptions);
				levelQueue.add(WorkerOptions);
	
				setQueueLevels();
				
			}
		});
		
		
		JButton RemoveLevelButton = new JButton("Remove selected level");
		QueuePanel.add(RemoveLevelButton);
		RemoveLevelButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int RecentClicked = queueDataTable.getSelectedRow();
				if(RecentClicked < levelQueue.size() && RecentClicked >= -1)
					levelQueue.remove(RecentClicked);
				setQueueLevels();
			}
		});
		content.add(QueuePanel);
	}
	
	
	
	//function to add level options such as difficulty or task trails
	public void NextRunOptions(final Container content){
		
		JLabel Title = new JLabel("Auto Next Run Options");
		
		JRadioButton setNoAuto = new JRadioButton("Always Same Run");
		setNoAuto.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
					Autorun = false;
		        }

		});
		
		JRadioButton AutoOnGeneration = new JRadioButton("New Run On generation:");
		AutoOnGeneration.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
					Autorun = true;
					AutoRunMode = 0;
		        }

		});
		
		AutoOnGeneration.setSelected(true);
		
		JRadioButton AutoOnScore = new JRadioButton("New Run On score:");
		AutoOnScore.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
					Autorun = true;
					AutoRunMode = 1;
		        }

		});
		
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(setNoAuto);
		group.add(AutoOnGeneration);
		group.add(AutoOnScore);

		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(Title);
		radioPanel.add(setNoAuto);
		radioPanel.add(AutoOnScore);
		
		
		JPanel ScorePanel = new JPanel(new GridLayout(0, 3));
		JLabel ScoreLabel = new JLabel("Score");
	    ScoreText = new JTextField(10);
		ScoreText.setText(Integer.toString(ScoreLimit));
		
		JButton ScoreButton = new JButton("Set Score");
		ScoreButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ScoreLimit = Integer.valueOf(ScoreText.getText());
			}
		});
		
		ScorePanel.add(ScoreLabel);
		ScorePanel.add(ScoreText);
		ScorePanel.add(ScoreButton);

		radioPanel.add(ScorePanel);
		
		JPanel GenPanel = new JPanel(new GridLayout(0, 3));
		JLabel GenLabel = new JLabel("Generation");
		GenText = new JTextField(10);
		GenText.setText(Integer.toString(GenerationLimit));
		
		JButton GenButton = new JButton("Set Generation");
		GenButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GenerationLimit = Integer.valueOf(GenText.getText());
			}
		});
		
		GenPanel.add(GenLabel);
		GenPanel.add(GenText);
		GenPanel.add(GenButton);
		
		radioPanel.add(AutoOnGeneration);
		
		radioPanel.add(GenPanel);
		

		
		content.add(radioPanel);
		
        
        
	}
	
	
	
	
	
	//function to add level options such as difficulty or task trails
	public void levelOptions(final Container content){
		
		
		
		
		JRadioButton setLevelSingle = new JRadioButton("Use level from file for all runs");
		setLevelSingle.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
		        final JFileChooser fc = new JFileChooser();
		        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		        fc.showOpenDialog(content);
		        if(fc.getSelectedFile() != null){
		        levelName = fc.getSelectedFile().getAbsolutePath();
		        LevelModeIndex = 1;
		        }

			}
		});
		
		
		JRadioButton setLevelRandomOnly = new JRadioButton("Set all runs to one random level");
		setLevelRandomOnly.setSelected(true);
		
		setLevelRandomOnly.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelModeIndex = 0;
			}
		});
		
		JRadioButton setEachLevelRandomOnly = new JRadioButton("Set each run to have a new level");
		setEachLevelRandomOnly.setSelected(false);
		
		setEachLevelRandomOnly.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelModeIndex = 2;
			}
		});
		
		JRadioButton setSeed = new JRadioButton("Set all runs to have the same seed");
		setSeed.setSelected(false);
		
		setSeed.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelModeIndex = 3;
			}
		});
		
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(setLevelSingle);
		group.add(setLevelRandomOnly);
		group.add(setEachLevelRandomOnly);
		group.add(setSeed);
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(setLevelSingle);
		radioPanel.add(setLevelRandomOnly);
		radioPanel.add(setEachLevelRandomOnly);
		radioPanel.add(setSeed);
		////////Seed panel
		JPanel SeedPanel = new JPanel(new GridLayout(0, 3));
		JLabel SeedLabel = new JLabel("Seed");
		 SeedText = new JTextField(10);
		SeedText.setText(Integer.toString(seed));
		
		JButton SeedButton = new JButton("SetSeed");
		SeedButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				seed = Integer.valueOf(SeedText.getText());
			}
		});
		
		SeedPanel.add(SeedLabel);
		SeedPanel.add(SeedText);
		SeedPanel.add(SeedButton);
		radioPanel.add(SeedPanel);
		//Difficulty settings
		JPanel DiffPanel = new JPanel(new GridLayout(0, 3));
		JLabel DiffLabel = new JLabel("Difficulty");

		
		String[] List = new String[19];
		for(int i = 0;i < 11; i++){
			List[i] = Integer.toString(i);
		}
		
		JComboBox Xstart = new JComboBox(List);
		Xstart.setSelectedIndex(0);

		Xstart.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				difficulty = ((JComboBox)e.getSource()).getSelectedIndex();
				}

		});
		
		
		DiffPanel.add(DiffLabel);
		DiffPanel.add(Xstart);
		radioPanel.add(DiffPanel);
		/////////
		
		JButton LevelButton = new JButton("Edit levels");
		LevelButton.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelEditor();
			}
		});
		radioPanel.add(LevelButton);
		
		content.add(radioPanel);
		
        
        
	}
	//Config options panel
	
	//only use on textboxes there is an unsafe cast
	public class Neatboxes implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			configs.updateConfig(e.getActionCommand() , ((JTextField)e.getSource()).getText());
		}
	}
	
	public void NEATConfig(final Container content){
		Neatboxes Listener = this.new Neatboxes();
		
		JPanel OptionsPanel = new JPanel(new GridLayout(0, 2));
		OptionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JPanel LabelPanel =  new JPanel(new FlowLayout());
		LabelPanel.add(new JLabel("Evolution Options"));
		OptionsPanel.add(LabelPanel);
		OptionsPanel.add( new JPanel(new FlowLayout()));
		
		JLabel PopLabel = new JLabel("Population size");
		JTextField PopSize = new JTextField(5);
		OptionsPanel.add(PopLabel);
		OptionsPanel.add(PopSize);
		OptionsBoxes.add(PopSize);
		OptionsBoxesHash.add("POP.SIZE");
		
		JLabel MutationLabel = new JLabel("Mutation rate (what kind?)");
		JTextField MutationText = new JTextField(5);
		OptionsPanel.add(MutationLabel);
		OptionsPanel.add(MutationText);
		OptionsBoxes.add(MutationText);
		OptionsBoxesHash.add("PROBABILITY.MUTATION");
		
		JLabel CrossoverLabel = new JLabel("Crossover rate");
		JTextField CrossoverText = new JTextField(5);
		CrossoverText.addActionListener(Listener);
		OptionsPanel.add(CrossoverLabel);
		OptionsPanel.add(CrossoverText);
		OptionsBoxes.add(CrossoverText);
		OptionsBoxesHash.add("PROBABILITY.CROSSOVER");
		
		JLabel AddLinkLabel = new JLabel("Add link rate");
		JTextField AddLinkText = new JTextField(5);
		OptionsPanel.add(AddLinkLabel);
		OptionsPanel.add(AddLinkText);
		OptionsBoxes.add(AddLinkText);
		OptionsBoxesHash.add("PROBABILITY.ADDLINK");
		
		JLabel AddNodeLabel = new JLabel("Add node rate");
		JTextField AddNodeText = new JTextField(5);
		OptionsPanel.add(AddNodeLabel);
		OptionsPanel.add(AddNodeText);
		OptionsBoxes.add(AddNodeText);
		OptionsBoxesHash.add("PROBABILITY.ADDNODE");
		
		JLabel MutateBiasLabel = new JLabel("Mutation bias rate");
		JTextField MutateBiasText = new JTextField(5);
		OptionsPanel.add(MutateBiasLabel);
		OptionsPanel.add(MutateBiasText);
		OptionsBoxes.add(MutateBiasText);
		OptionsBoxesHash.add("PROBABILITY.MUTATEBIAS");
		
		JLabel ToggleLinkLabel = new JLabel("Toggle link rate");
		JTextField  ToggleLinkText = new JTextField(5);
		OptionsPanel.add(ToggleLinkLabel);
		OptionsPanel.add(ToggleLinkText);
		OptionsBoxes.add(ToggleLinkText);
		OptionsBoxesHash.add("PROBABILITY.TOGGLELINK");
		
		JLabel WeightReplaceLabel = new JLabel("Bias weight replace rate");
		JTextField  WeightReplaceText = new JTextField(5);
		OptionsPanel.add(WeightReplaceLabel);
		OptionsPanel.add(WeightReplaceText);
		OptionsBoxes.add(WeightReplaceText);
		OptionsBoxesHash.add("PROBABILITY.WEIGHT.REPLACED");
		
		JLabel SpecieCountLabel = new JLabel("Number of Species to try to keep");
		JTextField  SpecieCountText = new JTextField(5);
		OptionsPanel.add(SpecieCountLabel);
		OptionsPanel.add(SpecieCountText);
		OptionsBoxes.add(SpecieCountText);
		OptionsBoxesHash.add("SPECIE.COUNT");
		
		JLabel ThreadCountLabel = new JLabel("Computation Threads");
		JTextField  ThreadCountText = new JTextField(5);
		ThreadCountText.setText("4");
		OptionsPanel.add(ThreadCountLabel);
		OptionsPanel.add(ThreadCountText);
		OptionsBoxes.add(ThreadCountText);
		OptionsBoxesHash.add("THREADS");
		
    	for(int i = 0;i < OptionsBoxes.size();i++){
    		OptionsBoxes.get(i).setActionCommand(OptionsBoxesHash.get(i));
    		OptionsBoxes.get(i).addActionListener(Listener);
    	}
		
		LoadConfigText();
		
		JButton SetAll = new JButton("Set All");
		SetAll.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(JTextField text:OptionsBoxes){
					text.postActionEvent();
				}
			}
		});
		
		OptionsPanel.add(SetAll);
		
		JButton LoadOptions = new JButton("Load Options");
		LoadOptions.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
		        final JFileChooser fc = new JFileChooser();
		        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		        fc.showOpenDialog(content);
		        if(fc.getSelectedFile() != null){
		        	configs = new NEATLoader().loadConfig(fc.getSelectedFile().getAbsolutePath());
		        	LoadConfigText();
			}
		}
		});
		OptionsPanel.add(LoadOptions);
		
		
		content.add(OptionsPanel);
	}
	//load config into text boxes
	public void LoadConfigText()
	{
    	for(int i = 0;i < OptionsBoxes.size();i++){
    		if(configs != null)
    		OptionsBoxes.get(i).setText(configs.configElement(OptionsBoxesHash.get(i)));
    	}
	}
	
	public void LevelEditor(){
		LevelEditor LevelEdit = new LevelEditor();
		LevelEdit.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		LevelEdit.setVisible(true);
	}
	
	public void VisionRange(final Container content){
		
		JPanel VisionPanel = new JPanel(new GridLayout(0, 2));
		VisionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JLabel VisionLabel1 = new JLabel("Vision of Mario");
		JLabel VisionLabel2 = new JLabel("(Where mario is at -2)");
		VisionPanel.add(VisionLabel1);
		VisionPanel.add(VisionLabel2);
		
		String[] List = new String[19];
		for(int i = -11;i < 8; i++){
			List[i + 11] = Integer.toString(i);
		}
		
		JLabel XstartLabel = new JLabel("Starting X value");
		JComboBox Xstart = new JComboBox(List);
		Xstart.setSelectedIndex(Vision.XVisionStart + 11);
		VisionPanel.add(XstartLabel);
		VisionPanel.add(Xstart);
		Xstart.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Vision.XVisionStart = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + extraFeatures));
				}

		});
		
		JLabel XendLabel = new JLabel("Ending X value");
		JComboBox Xend = new JComboBox(List);
		Xend.setSelectedIndex(Vision.XVisionEnd + 11);
		VisionPanel.add(XendLabel);
		VisionPanel.add(Xend);
		Xend.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.XVisionEnd = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + extraFeatures));}
		});
		
		JLabel YstartLabel = new JLabel("Starting Y value");
		JComboBox Ystart = new JComboBox(List);
		Ystart.setSelectedIndex(Vision.YVisionStart + 11);
		VisionPanel.add(YstartLabel);
		VisionPanel.add(Ystart);
		Ystart.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.YVisionStart = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + extraFeatures ));}
		});
		
		JLabel YendLabel = new JLabel("Ending Y value");
		JComboBox Yend = new JComboBox(List);
		Yend.setSelectedIndex(Vision.YVisionEnd + 11);
		VisionPanel.add(YendLabel);
		VisionPanel.add(Yend);
		Yend.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.YVisionEnd = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + extraFeatures));}
		});
		
		content.add(VisionPanel);

	}
	
	public void FitnessFunction(final Container content){
		
	   class Heuristicboxes implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Distance")){
					DistanceHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setDistanceHeuristic(DistanceHeuristic);
				} 
				else if(e.getActionCommand().equals("Mushroom")){
					MushroomHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setMushroomHeuristic(MushroomHeuristic);
				}				
				else if(e.getActionCommand().equals("Flower")){
					FlowerHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setFlowerHeuristic(FlowerHeuristic);
				}
				else if(e.getActionCommand().equals("Coins")){
					CoinsHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setCoinsHeuristic(CoinsHeuristic);
				}
				else if(e.getActionCommand().equals("StompKills")){
					StompKillsHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setStompKillsHeuristic(StompKillsHeuristic);
				}
				else if(e.getActionCommand().equals("ShellKill")){
					ShellKillHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setShellKillHeuristic(ShellKillHeuristic);
				}
				else if(e.getActionCommand().equals("Connection")){
					ConnectionHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setConnectionHeuristic(ConnectionHeuristic);
				}
				else if(e.getActionCommand().equals("Neuron")){
					NeuronHeuristic = Double.valueOf(((JTextField)e.getSource()).getText());
					task.setNeuronHeuristic(NeuronHeuristic);
				}
			}
		}
	    Heuristicboxes HBoxes = new Heuristicboxes();
		
		JPanel OptionsPanel = new JPanel(new GridLayout(0, 2));
		OptionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JPanel LabelPanel =  new JPanel(new FlowLayout());
		LabelPanel.add(new JLabel("Heuristic Options"));
		OptionsPanel.add(LabelPanel);
		OptionsPanel.add( new JPanel(new FlowLayout()));
		
		JLabel DistanceLabel = new JLabel("Distance Heuristic Wieght");
		JTextField DistanceText = new JTextField(5);
		DistanceText.addActionListener(HBoxes);
		DistanceText.setActionCommand("Distance");
		DistanceText.setText(Double.toString(DistanceHeuristic));
		OptionsPanel.add(DistanceLabel);
		OptionsPanel.add(DistanceText);
		HeuristicBoxes.add(DistanceText);
		
		JLabel MushroomLabel = new JLabel("Mushroom Heuristic Wieght");
		JTextField MushroomText = new JTextField(5);
		MushroomText.addActionListener(HBoxes);
		MushroomText.setActionCommand("Mushroom");
		MushroomText.setText(Double.toString(MushroomHeuristic));
		OptionsPanel.add(MushroomLabel);
		OptionsPanel.add(MushroomText);
		HeuristicBoxes.add(MushroomText);
		
		JLabel FlowerLabel = new JLabel("Flower Heuristic Wieght");
		JTextField FlowerText = new JTextField(5);
		FlowerText.addActionListener(HBoxes);
		FlowerText.setActionCommand("Flower");
		FlowerText.setText(Double.toString(FlowerHeuristic));
		OptionsPanel.add(FlowerLabel);
		OptionsPanel.add(FlowerText);
		HeuristicBoxes.add(FlowerText);
		
		JLabel CoinsLabel = new JLabel("Coins Heuristic Wieght");
		JTextField CoinsText = new JTextField(5);
		CoinsText.addActionListener(HBoxes);
		CoinsText.setActionCommand("Coins");
		CoinsText.setText(Double.toString(CoinsHeuristic));
		OptionsPanel.add(CoinsLabel);
		OptionsPanel.add(CoinsText);
		HeuristicBoxes.add(CoinsText);
				
		JLabel StompKillsLabel = new JLabel("StompKills Heuristic Wieght");
		JTextField StompKillsText = new JTextField(5);
		StompKillsText.addActionListener(HBoxes);
		StompKillsText.setActionCommand("StompKills");
		StompKillsText.setText(Double.toString(StompKillsHeuristic));
		OptionsPanel.add(StompKillsLabel);
		OptionsPanel.add(StompKillsText);
		HeuristicBoxes.add(StompKillsText);
		
		JLabel ShellKillLabel = new JLabel("ShellKill Heuristic Wieght");
		JTextField ShellKillText = new JTextField(5);
		ShellKillText.addActionListener(HBoxes);
		ShellKillText.setActionCommand("ShellKill");
		ShellKillText.setText(Double.toString(ShellKillHeuristic));
		OptionsPanel.add(ShellKillLabel);
		OptionsPanel.add(ShellKillText);
		HeuristicBoxes.add(ShellKillText);
		
		JLabel ConnectionLabel = new JLabel("Connection Heuristic Wieght");
		JTextField ConnectionText = new JTextField(5);
		ConnectionText.addActionListener(HBoxes);
		ConnectionText.setActionCommand("Connection");
		ConnectionText.setText(Double.toString(ConnectionHeuristic));
		OptionsPanel.add(ConnectionLabel);
		OptionsPanel.add(ConnectionText);
		HeuristicBoxes.add(ConnectionText);
		
		JLabel NeuronLabel = new JLabel("Neuron Heuristic Wieght");
		JTextField NeuronText = new JTextField(5);
		NeuronText.addActionListener(HBoxes);
		NeuronText.setActionCommand("Neuron");
		NeuronText.setText(Double.toString(NeuronHeuristic));
		OptionsPanel.add(NeuronLabel);
		OptionsPanel.add(NeuronText);
		HeuristicBoxes.add(NeuronText);
		
		JButton SetAll = new JButton("Set All");
		SetAll.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(JTextField text:HeuristicBoxes){
					text.postActionEvent();
				}
			}
		});
		
		OptionsPanel.add(SetAll);

		content.add(OptionsPanel);
		
	}

	
	

}
