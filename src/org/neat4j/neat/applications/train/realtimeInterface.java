package org.neat4j.neat.applications.train;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
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
	///////////////////////////////
	//NEAT stuff
	///////////////////////////////
	static NEATGATrainingManager gam;
	private static NEATGeneticAlgorithmMario ga;
	private static AIConfig config;
	private static Chromosome DemoMember;
	private static AIConfig configs;
	
	///////////////////////////////
	//Mario testbed stuff
	///////////////////////////////
	static Task task;
	static MarioAIOptions options = new MarioAIOptions("nothing");
	static int difficulty = 0;
	static int seed = 0;
	static Random rand = new Random(System.currentTimeMillis());
	
	static String levelName = "resources/test.lvl";
	
	////////////
	//Demo stuff
	////////////
	SwingWorker<Void, Void> demoWorker;

	/**
	 * @param args
	 */

	public static void main(final String[] args) {
		configs = new NEATLoader().loadConfig("xor_neat.ga");
		new realtimeInterface();
	}
	
	public class mainWorker extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			double firstbest = 100;
			int i = 0,diffGen = 0;
			boolean first = false;
			System.out.println("in worker thread");
			options = new MarioAIOptions("nothing");
	        options.setFPS(GlobalOptions.MaxFPS);
	        options.setVisualization(false);
	        task = new ProgressTask(options);
		    
		    for (difficulty = 0; difficulty < 11; difficulty++)
		    {
				seed = rand.nextInt();

		        System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");

			
			while (true) {

				setOptions(options);
		        System.out.println("Running Epoch[" + i + "] with diff:" + difficulty);
		        
				((NEATGeneticAlgorithmMario)ga).runEpoch(task);

				
				/////////////////////////////////////////
				//Gui Process stuff
				/////////////
				//Get results
				publish();
				//pause to read results
				while(IsPaused){

		               try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						return null;
					}
				}
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
			System.out.println("cancel!?");
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
			System.out.println("runiingngng");
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
		System.out.println("?!?!?");
		worker.execute();
		}

	}

	public realtimeInterface() {
		IsPaused = false;
		
		this.setSize(800, 800);
		setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container content = this.getContentPane();
	    content.setLayout(new FlowLayout());
	    
	    JPanel GridPanel = new JPanel(new FlowLayout());
	    
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
		NEATConfig(content);
		
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
		//TODO figure out why this thread has to end (cant be killed though) before another can be created!?
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
						        
								WorkerTask.evaluate((Agent) new NeatAgent(nets));
								
								 
								
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
		default:
	        options.setLevelDifficulty(difficulty);
	        options.setArgs("-ls " + seed);
			break;

		}
		
	}
	
	//function to add level options such as difficulty or task trails
	public void levelOptions(final Container content){
		JRadioButton setLevelSingle = new JRadioButton("Use single level from file");
		setLevelSingle.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
		        final JFileChooser fc = new JFileChooser();
		        fc.setCurrentDirectory(new File("/home/bbb/workspace/Thesis/resources"));
		        fc.showOpenDialog(content);
		        levelName = fc.getSelectedFile().getAbsolutePath();
		        LevelModeIndex = 1;
			}
		});
		
		
		JRadioButton setLevelRandomOnly = new JRadioButton("Set all levels to be random");
		setLevelRandomOnly.setSelected(true);
		
		setLevelRandomOnly.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LevelModeIndex = 0;
			}
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add(setLevelSingle);
		group.add(setLevelRandomOnly);
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(setLevelSingle);
		radioPanel.add(setLevelRandomOnly);
		
		
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
		
		JLabel PopLabel = new JLabel("Population size");
		JTextField PopSize = new JTextField(5);
		PopSize.setActionCommand("POP.SIZE");
		PopSize.setText(configs.configElement("POP.SIZE"));
		PopSize.addActionListener(Listener);
		OptionsPanel.add(PopLabel);
		OptionsPanel.add(PopSize);
		
		JLabel MutationLabel = new JLabel("Mutation rate (what kind?)");
		JTextField MutationText = new JTextField(5);
		MutationText.setActionCommand("PROBABILITY.MUTATION");
		MutationText.setText(configs.configElement("PROBABILITY.MUTATION"));
		MutationText.addActionListener(Listener);
		OptionsPanel.add(MutationLabel);
		OptionsPanel.add(MutationText);
		
		JLabel CrossoverLabel = new JLabel("Crossover rate");
		JTextField CrossoverText = new JTextField(5);
		CrossoverText.setActionCommand("PROBABILITY.CROSSOVER");
		CrossoverText.setText(configs.configElement("PROBABILITY.CROSSOVER"));
		CrossoverText.addActionListener(Listener);
		OptionsPanel.add(CrossoverLabel);
		OptionsPanel.add(CrossoverText);
		
		JLabel AddLinkLabel = new JLabel("Add link rate");
		JTextField AddLinkText = new JTextField(5);
		AddLinkText.setActionCommand("PROBABILITY.ADDLINK");
		AddLinkText.setText(configs.configElement("PROBABILITY.ADDLINK"));
		AddLinkText.addActionListener(Listener);
		OptionsPanel.add(AddLinkLabel);
		OptionsPanel.add(AddLinkText);
		
		JLabel AddNodeLabel = new JLabel("Add node rate");
		JTextField AddNodeText = new JTextField(5);
		AddNodeText.setActionCommand("PROBABILITY.ADDNODE");
		AddNodeText.setText(configs.configElement("PROBABILITY.ADDNODE"));
		AddNodeText.addActionListener(Listener);
		OptionsPanel.add(AddNodeLabel);
		OptionsPanel.add(AddNodeText);
		
		JLabel MutateBiasLabel = new JLabel("Mutation bias rate");
		JTextField MutateBiasText = new JTextField(5);
		MutateBiasText.setActionCommand("PROBABILITY.MUTATEBIAS");
		MutateBiasText.setText(configs.configElement("PROBABILITY.MUTATEBIAS"));
		MutateBiasText.addActionListener(Listener);
		OptionsPanel.add(MutateBiasLabel);
		OptionsPanel.add(MutateBiasText);
		
		JLabel ToggleLinkLabel = new JLabel("Toggle link rate");
		JTextField  ToggleLinkText = new JTextField(5);
		ToggleLinkText.setActionCommand("PROBABILITY.TOGGLELINK");
		ToggleLinkText.setText(configs.configElement("PROBABILITY.TOGGLELINK"));
		ToggleLinkText.addActionListener(Listener);
		OptionsPanel.add(ToggleLinkLabel);
		OptionsPanel.add(ToggleLinkText);
		
		JLabel WeightReplaceLabel = new JLabel("Bias weight replace rate");
		JTextField  WeightReplaceText = new JTextField(5);
		WeightReplaceText.setActionCommand("PROBABILITY.WEIGHT.REPLACED");
		WeightReplaceText.setText(configs.configElement("PROBABILITY.WEIGHT.REPLACED"));
		WeightReplaceText.addActionListener(Listener);
		OptionsPanel.add(WeightReplaceLabel);
		OptionsPanel.add(WeightReplaceText);
		
		
		
		
		content.add(OptionsPanel);
	}

	
	

}
