package org.neat4j.neat.applications.train;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.gui.NEATFrame;
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
	private static Chromosome DemoMember;
	
	static SwingWorker<Void, Void> worker;
	private static NEATGeneticAlgorithmMario ga;
	private static AIConfig config;
	
	private static String[][] specData;
	static String[] SpecDataHeading = {"Species" , "Species members"};
	static JTable SpecDataTable;
	static int currentSpecies;
	
	private static String[][] specMemberData;
	static String[] SpecMemberDataHeading = {"Species member" , "Other info???"};
	static JTable SpecMemberDataTable;
	///////////////////////////////
	//Mario testbed stuff
	///////////////////////////////
	static Task task;
	final static MarioAIOptions options = new MarioAIOptions("nothing");
	final static NEATGATrainingManager gam = new NEATGATrainingManager();
	static int difficulty = 0;
	static int seed = 0;
	static Random rand = new Random(System.currentTimeMillis());

	/**
	 * @param args
	 */

	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		final String[] s = {"xor_neat.ga"};
		new realtimeInterface();
		
		try {
			if (s.length != 1) {
				System.out
						.println("Usage: NEATGAManager <ga ga config file");
				return;
			} else {
				AIConfig configs = new NEATLoader().loadConfig(s[0]);
				//lets shove the config from the old main class into this one
				gam.initialise(configs);
				//gam.evolve();
				ga = (NEATGeneticAlgorithmMario)gam.ga();
				config = gam.GetConfig();
			}
		} catch (InitialisationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//TODO: for this worker give feedback data every so often and update info (said info needs to be created)
		worker = new SwingWorker<Void, Void>() {

			@Override
			public Void doInBackground() {
				double firstbest = 100;
				int i = 0,diffGen = 0;
				boolean first = false;
				
		       // options.setArgs("-ls " + "resources/test.lvl" );
		        options.setFPS(GlobalOptions.MaxFPS);
		        options.setVisualization(false);
		        task = new ProgressTask(options);
			    
			    for (difficulty = 0; difficulty < 11; difficulty++)
			    {
			        options.setLevelDifficulty(difficulty);
			        System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");

				
				while (true) {
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
							e.printStackTrace();
						}
					}
					//////////////////////////////////////////
					
					if(first){
						seed = rand.nextInt();
				        options.setArgs("-ls " + seed);
						firstbest = (((NEATGeneticAlgorithmMario) ga).genBest());
						first = false;
					}

					options.setVisualization(false);
					
						
					
					
					if ((((NEATGeneticAlgorithmMario) ga).genBest() > firstbest)){
						System.out.println("Improvment" + firstbest + "to -->" + ((NEATGeneticAlgorithmMario) ga).genBest());
				       
						
				        options.setLevelDifficulty(difficulty);
				        options.setFPS(32);
				        options.setVisualization(true);
				        
				        NeuralNet nets = null;
						try {
							nets = gam.createNet(config);
						} catch (InitialisationFailedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						((NEATNetDescriptor)(nets.netDescriptor())).updateStructure(ga.discoverdBestMember());
						((NEATNeuralNet)nets).updateNetStructure();
						
				        
						 task.evaluate((Agent) new NeatAgent(nets));
				        


					        first = true;
					        System.out.println("better found in " + diffGen + "runs");
					        diffGen = 0;
							//((NEATGeneticAlgorithmMario) this.ga).resetdiscoverdBestMember();
						
						
						
				      
				        options.setFPS(GlobalOptions.MaxFPS);
				        options.setVisualization(false);

						if(((NEATGeneticAlgorithmMario) ga).genBest() >= 2000){
							NEATFrame frame = new NEATFrame((NEATNeuralNet)nets);
							frame.setTitle("Generation: " + i + " Score :" + ((NEATGeneticAlgorithmMario) ga).genBest());
							frame.showNet();
							seed = rand.nextInt();
					        options.setArgs("-ls " + seed);

							first = true;
							break;
						}
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
		};

	}

	public void actionPerformed(ActionEvent e) {
		worker.execute();
	}

	public realtimeInterface() {
		IsPaused = false;
		
		this.setSize(800, 800);
		setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container content = this.getContentPane();
	    content.setLayout(new FlowLayout());
	    
		JButton b1 = new JButton("START");
		b1.addActionListener(this);
		content.add(b1);
		
		JButton b2 = new JButton("Pause on next break");
		b2.addActionListener(new  ActionListener(){
			public void actionPerformed(ActionEvent e) {
				IsPaused = !IsPaused;
			}
		});
		content.add(b2);
		
		//species data table
		specData = new String[10][2];
	    SpecDataTable = new JTable(specData,SpecDataHeading);
		
		JScrollPane SpecieInfo = new JScrollPane(SpecDataTable);

		
		content.add(SpecieInfo);
		SpecDataTable.setVisible(true);
		SpecieInfo.setVisible(true);
		
		//species member data
		
		specMemberData = new String[10][2];
		SpecMemberDataTable = new JTable(specMemberData,SpecMemberDataHeading);
		JScrollPane SpecieMemberInfo = new JScrollPane(SpecMemberDataTable);
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
					SwingWorker<Void, Void> demoWorker = new SwingWorker<Void, Void>() {

						@Override
						public Void doInBackground() {
							MarioAIOptions WorkerOptions = new MarioAIOptions("nothing");
							
							WorkerOptions.setLevelDifficulty(difficulty);
							WorkerOptions.setFPS(32);
							WorkerOptions.setVisualization(true);
							WorkerOptions.setArgs("-ls " + seed);
							
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
							
					        
							WorkerTask.evaluate((Agent) new NeatAgent(nets));
							 
							
							return null;
						}
					};
					demoWorker.execute();
				}
				else{
					System.out.println("Chromosome is null something is wrong");
				}

			}
		});
		content.add(b3);
		
		
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

}
