package org.neat4j.neat.applications.train;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
import org.neat4j.neat.core.InnovationDatabase;
import org.neat4j.neat.core.NEATChromosome;
import org.neat4j.neat.core.NEATGeneticAlgorithmMario;
import org.neat4j.neat.core.NEATLoader;
import org.neat4j.neat.core.NEATNetDescriptor;
import org.neat4j.neat.core.NEATNeuralNet;
import org.neat4j.neat.ga.core.Chromosome;
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
	
	static SwingWorker<Void, Void> worker;

	
	private static String[][] specData;
	static String[] SpecDataHeading = {"Species" , "Species members"};
	static JTable SpecDataTable;
	static int currentSpecies;
	
	private static String[][] specMemberData;
	static String[] SpecMemberDataHeading = {"Species member" , "Fitness"};
	static JTable SpecMemberDataTable;
	
	static int LevelModeIndex = 0;
	
	ArrayList<JTextField> OptionsBoxes = new ArrayList<JTextField>();
	ArrayList<String> OptionsBoxesHash = new ArrayList<String>();
	
	ArrayList<JTextField> HeuristicBoxes = new ArrayList<JTextField>();
	
	JLabel RunningStatus;
	String RunningPausedNotification = "Paused";
	JTextField SeedText;
	///////////////////////////////
	//NEAT stuff
	///////////////////////////////
	static NEATGATrainingManager gam;
	private static NEATGeneticAlgorithmMario ga;
	private static AIConfig config;
	private static Chromosome DemoMember;
	private static AIConfig configs;
	private VisionBound Vision = new VisionBound(-2,3,-2,3);
	///////////////////////////////
	//Mario testbed stuff
	///////////////////////////////
	static ProgressTask task;
	static MarioAIOptions options = new MarioAIOptions("nothing");
	static int difficulty = 0;
	static int seed = 0;
	static Random rand = new Random(System.currentTimeMillis());
	static String levelName = "resources/test.lvl";
	
	private double DistanceHeuristic = 1, MushroomHeuristic = 0, FlowerHeuristic = 0,  CoinsHeuristic = 0, StompKillsHeuristic = 200,ShellKillHeuristic = 500;

	
	////////////
	//Demo stuff
	////////////
	SwingWorker<Void, Void> demoWorker;

	/**
	 * @param args
	 */

	public static void main(final String[] args) {
		configs = new NEATLoader().loadConfig("xor_neat.ga");
		options = new MarioAIOptions("nothing");
        options.setFPS(GlobalOptions.MaxFPS);
        options.setVisualization(false);
        task = new ProgressTask(options);
    	seed = rand.nextInt();
		new realtimeInterface();
	}
	
	public class mainWorker extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			int i = 0,diffGen = 0;
		    for (difficulty = 0; difficulty < 11; difficulty++)
		    {
		        System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");
			
			while (true) {

				setOptions(options);
		        System.out.println("Running Epoch[" + i + "] with diff:" + difficulty);
		        
				((NEATGeneticAlgorithmMario)ga).runEpoch(task,Vision);

				
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


					if(((NEATGeneticAlgorithmMario) ga).genBest() >= 10000){
						break;
					}
				diffGen++;
				i++;
			}
		    }
			
			return null;
		}
		@Override
		protected void process(List<Void> t) {
			RunningStatus.setText(RunningPausedNotification);
			
			//specData[0][0] = Integer.toString(ga.population().genoTypes().length);
			specData = new String[ga.GetSpecies().specieList().size()][2];
			for(int i = 0; i < ga.GetSpecies().specieList().size() ; i++){
				specData[i][0] = Integer.toString(((Specie)ga.GetSpecies().specieList().get(i)).id());
				specData[i][1] = Integer.toString(((Specie)ga.GetSpecies().specieList().get(i)).specieMembers().size());
			}
			                       
			SpecDataTable.setModel(new DefaultTableModel(specData,SpecDataHeading));
			SpecDataTable.updateUI();
		}

		/*
		 * @Override public void done() { ; }
		 */
	}

	public void actionPerformed(ActionEvent e) {
		 
		if(worker != null && !worker.isDone()){

			worker.cancel(true);
			try {
				Thread.sleep(600);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(worker == null || worker.isDone()){
		worker = new mainWorker();
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
		config = gam.GetConfig();

		worker.execute();
		}

	}

	public realtimeInterface() {
		IsPaused = false;
		
		this.setSize(800, 800);
		setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("NEAT Mario Nets Alpha2");
		
		Container content = this.getContentPane();
	    content.setLayout(new FlowLayout());
	    
	    JPanel GridPanel = new JPanel(new FlowLayout());
	    
	    RunningStatus = new JLabel(RunningPausedNotification);
	    GridPanel.add(RunningStatus);
	    
		JButton b1 = new JButton("Start New Run");
		b1.addActionListener(this);
		GridPanel.add(b1);
		
		JButton b2 = new JButton("Pause on next break");
		b2.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				IsPaused = !IsPaused;
			}
		});
		GridPanel.add(b2);
		
		JPanel StackPanel = new JPanel(new GridLayout(0, 1));
		
		StackPanel.add(GridPanel);
		SpeciesBoxes(StackPanel);
		content.add(StackPanel);
		
		levelOptions(content);
		
		JPanel StackPanel2 = new JPanel(new GridLayout(0, 1));
		
		NEATConfig(StackPanel2);
		FitnessFunction(StackPanel2);
		content.add(StackPanel2);
		
		VisionRange(content);
		
		this.pack();

	}
	
	public void SpeciesBoxes(final Container content){
		//species data table
		specData = new String[10][2];
	    SpecDataTable = new JTable(specData,SpecDataHeading);
		
		JScrollPane SpecieInfo = new JScrollPane(SpecDataTable);
		SpecieInfo.setPreferredSize(new Dimension(300, 200));

		
		content.add(SpecieInfo);
		SpecDataTable.setVisible(true);
		SpecieInfo.setVisible(true);
		
		//species member data
		
		specMemberData = new String[10][2];
		SpecMemberDataTable = new JTable(specMemberData,SpecMemberDataHeading);
		JScrollPane SpecieMemberInfo = new JScrollPane(SpecMemberDataTable);
		SpecieMemberInfo.setPreferredSize(new Dimension(300, 200));
		
		//update on species click
		SpecDataTable.addMouseListener( new MouseAdapter() {
	          public void mouseClicked(MouseEvent e) {
	        	  
	              int RecentClicked;
	              try
	              {
	            	  RecentClicked = SpecDataTable.getSelectedRow();
	            	  if(RecentClicked < ga.GetSpecies().specieList().size()){
	            		  currentSpecies = RecentClicked;
	            		  specMemberData = new String[((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size()][2];
	            		  ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size();
	            		  
	      				for(int i = 0; i < ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().size() ; i++){
	      					specMemberData[i][0] = Integer.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).getSpecieId() );
	      					specMemberData[i][1] = Double.toString(((NEATChromosome) ((Specie)ga.GetSpecies().specieList().get(RecentClicked)).specieMembers().get(i)).fitness());
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
					if(demoWorker == null || demoWorker.isDone()){

						demoWorker = new SwingWorker<Void, Void>() {

							@Override
							public Void doInBackground() {

								MarioAIOptions WorkerOptions = new MarioAIOptions("nothing");
								
								WorkerOptions.setLevelDifficulty(difficulty);
								WorkerOptions.setFPS(32);
								WorkerOptions.setVisualization(true);
								setOptions(WorkerOptions);
								Task WorkerTask = new ProgressTask(WorkerOptions);
								
						        NeuralNet nets = null;
								try {
									nets = gam.createNet(config);
								} catch (InitialisationFailedException a) {
									// TODO Auto-generated catch block
									a.printStackTrace();
								}
								
								((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(DemoMember);
								((NEATNeuralNet)nets).updateNetStructure();
								
								NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
								frame.setTitle("Demo");
								frame.showNet();
						        
								WorkerTask.evaluate((Agent) new NeatAgent(nets, Vision));
								
								 
								
								return null;
							}
						};
						demoWorker.execute();


					}
				}
				else{
					System.out.println("Chromosome is null something is wrong");
				}

			}
		});
		 JPanel FlowPanel = new JPanel(new FlowLayout());
		 FlowPanel.add(b3);
		 
		JButton DemoGenBestButton = new JButton("View demo of best member this gen");
		DemoGenBestButton.addActionListener(new  ActionListener(){
				public void actionPerformed(ActionEvent e) {

					if(DemoMember != null)
					{
						if(demoWorker == null || demoWorker.isDone()){

							demoWorker = new SwingWorker<Void, Void>() {

								@Override
								public Void doInBackground() {

									MarioAIOptions WorkerOptions = new MarioAIOptions("nothing");
									
									WorkerOptions.setLevelDifficulty(difficulty);
									WorkerOptions.setFPS(32);
									WorkerOptions.setVisualization(true);
									setOptions(WorkerOptions);
									Task WorkerTask = new ProgressTask(WorkerOptions);
									
							        NeuralNet nets = null;
									try {
										nets = gam.createNet(config);
									} catch (InitialisationFailedException a) {
										// TODO Auto-generated catch block
										a.printStackTrace();
									}
									
									((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(ga.generationBest());
									((NEATNeuralNet)nets).updateNetStructure();
									
									NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
									frame.setTitle("Demo");
									frame.showNet();
							        
									WorkerTask.evaluate((Agent) new NeatAgent(nets, Vision));
									
									 
									
									return null;
								}
							};
							demoWorker.execute();


						}
					}
					else{
						System.out.println("Chromosome is null something is wrong");
					}

				}
			});
		FlowPanel.add(DemoGenBestButton);
		 
		content.add(FlowPanel);
		
		
		SpecMemberDataTable.addMouseListener( new MouseAdapter() {
	          public void mouseClicked(MouseEvent e) {
	        	  
	              int RecentClicked;
	              try
	              {
	            	  RecentClicked = SpecMemberDataTable.getSelectedRow();
	            	  if(RecentClicked < ((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().size()){
	            		  DemoMember = (Chromosome) ((Specie)ga.GetSpecies().specieList().get(currentSpecies)).specieMembers().get(RecentClicked);
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
		
		
		JRadioButton setLevelRandomOnly = new JRadioButton("Set all levels one random level");
		setLevelRandomOnly.setSelected(true);
		
		setLevelRandomOnly.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelModeIndex = 0;
			}
		});
		
		JRadioButton setEachLevelRandomOnly = new JRadioButton("Set Each run to have a new level");
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
    		if(configs!= null)
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
		Xstart.setSelectedIndex(9);
		VisionPanel.add(XstartLabel);
		VisionPanel.add(Xstart);
		Xstart.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				Vision.XVisionStart = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + 1));
				}

		});
		
		JLabel XendLabel = new JLabel("Ending X value");
		JComboBox Xend = new JComboBox(List);
		Xend.setSelectedIndex(14);
		VisionPanel.add(XendLabel);
		VisionPanel.add(Xend);
		Xend.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.XVisionEnd = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + 1));}
		});
		
		JLabel YstartLabel = new JLabel("Starting Y value");
		JComboBox Ystart = new JComboBox(List);
		Ystart.setSelectedIndex(9);
		VisionPanel.add(YstartLabel);
		VisionPanel.add(Ystart);
		Ystart.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.YVisionStart = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + 1));}
		});
		
		JLabel YendLabel = new JLabel("Ending Y value");
		JComboBox Yend = new JComboBox(List);
		Yend.setSelectedIndex(14);
		VisionPanel.add(YendLabel);
		VisionPanel.add(Yend);
		Yend.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vision.YVisionEnd = ((JComboBox)e.getSource()).getSelectedIndex() + -11;
				configs.updateConfig("INPUT.NODES" , Integer.toString((Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd) + 1));}
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
