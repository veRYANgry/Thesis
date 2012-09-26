/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of the Mario AI nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.benchmark.tasks;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.NeatAgent;
import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.tools.MarioAIOptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.neat4j.neat.core.NEATNeuralNet;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: ch.idsia.maibe.tasks
 */

public final class ProgressTask extends BasicTask implements Task, Cloneable
{
private int uniqueSeed;
private int fitnessEvaluations = 0;
public int uid;
private String fileTimeStamp = "-uid-" + uid + "-" + GlobalOptions.getTimeStamp();

private double DistanceHeuristic = 1, MushroomHeuristic = 0, FlowerHeuristic = 0,  CoinsHeuristic = 0, StompKillsHeuristic = 200,ShellKillHeuristic = 500;
private double ConnectionHeuristic = 0, NeuronHeuristic = 0;

//    private int startingSeed;

public void setConnectionHeuristic(double connectionHeuristic) {
	ConnectionHeuristic = connectionHeuristic;
}

public void setNeuronHeuristic(double neuronHeuristic) {
	NeuronHeuristic = neuronHeuristic;
}

public void setFitnessEvaluations(int fitnessEvaluations) {
	this.fitnessEvaluations = fitnessEvaluations;
}

public void setDistanceHeuristic(double distanceHeuristic) {
	DistanceHeuristic = distanceHeuristic;
}

public void setMushroomHeuristic(double mushroomHeuristic) {
	MushroomHeuristic = mushroomHeuristic;
}

public void setFlowerHeuristic(double flowerHeuristic) {
	FlowerHeuristic = flowerHeuristic;
}

public void setCoinsHeuristic(double coinsHeuristic) {
	CoinsHeuristic = coinsHeuristic;
}

public void setStompKillsHeuristic(double stompKillsHeuristic) {
	StompKillsHeuristic = stompKillsHeuristic;
}

public void setShellKillHeuristic(double shellKillHeuristic) {
	ShellKillHeuristic = shellKillHeuristic;
}

public void setTotalEpisodes(int totalEpisodes) {
	this.totalEpisodes = totalEpisodes;
}

public ProgressTask(MarioAIOptions evaluationOptions)
{
    super(evaluationOptions);
    System.out.println("evaluationOptions = " + evaluationOptions);
    setOptionsAndReset(evaluationOptions);
}

public int totalEpisodes = 0;

private double[] evaluateSingleLevel(int ld, int tl, int ls, boolean vis, Agent controller)
{
    this.totalEpisodes++;
    Random rand = new Random(System.currentTimeMillis());
    float distanceTravelled = 0;
    int randomStartDistance;
    double results[];
    //options.setMarioInitialPos(rand.nextInt(100), 120);
    
    if(((NeatAgent)controller).getHueristics() != null){
    	results = new double[2];
    }
    else
    	results = new double[1];
    randomStartDistance = rand.nextInt(50);
    options.setMarioInitialPos(randomStartDistance, 120);
    options.setMarioMode(0);
    
    options.setAgent(controller);
//        options.setLevelDifficulty(ld);
//        options.setTimeLimit(tl);
//        options.setLevelRandSeed(ls);
//        options.setVisualization(vis);
//        options.setFPS(vis ? 42 : 100);
//        this.setAgent(controller);
    this.setOptionsAndReset(options);
    this.runSingleEpisode(1);
   // distanceTravelled += (this.getEnvironment().getEvaluationInfo().marioStatus == Mario.STATUS_WIN ? 1 : 0) * 1000;
    distanceTravelled += (this.getEnvironment().getEvaluationInfo().computeDistancePassed() -  randomStartDistance)* DistanceHeuristic;
    distanceTravelled += this.getEnvironment().getEvaluationInfo().mushroomsDevoured * MushroomHeuristic;
    //distanceTravelled -= (this.getEnvironment().getEvaluationInfo().marioMode == 0 ? 1 : 0) * 1000;
    //distanceTravelled += this.getEnvironment().getEvaluationInfo().greenMushroomsDevoured * 100;
    distanceTravelled += this.getEnvironment().getEvaluationInfo().flowersDevoured * FlowerHeuristic;
    distanceTravelled += this.getEnvironment().getEvaluationInfo().coinsGained * CoinsHeuristic;
    //distanceTravelled += this.getEnvironment().getEvaluationInfo().killsByFire * 60;
    distanceTravelled += this.getEnvironment().getEvaluationInfo().killsByShell * ShellKillHeuristic;
    distanceTravelled += this.getEnvironment().getEvaluationInfo().killsByStomp * StompKillsHeuristic;
    distanceTravelled += ((NEATNeuralNet)((NeatAgent)controller).getNet()).connectionCount() * ConnectionHeuristic;
    distanceTravelled += ((NEATNeuralNet)((NeatAgent)controller).getNet()).connectionCount() * NeuronHeuristic;
    //distanceTravelled -= this.getEnvironment().getEvaluationInfo().collisionsWithCreatures * 1000;
    //might remove timeSpent (bad heuristic)
   // distanceTravelled += this.getEnvironment().getEvaluationInfo().timeSpentMovingTowardememy / 100;
    if(distanceTravelled < 0)
    	distanceTravelled = 0;
    results[0] = distanceTravelled;
    distanceTravelled = 0;
    //TODO for each level type run and get results
    if(((NeatAgent)controller).getHueristics() != null){
    	int i = 0;
        distanceTravelled += (this.getEnvironment().getEvaluationInfo().computeDistancePassed() -  randomStartDistance) / 4000 * ((NeatAgent)controller).getHueristics().get(i)[0];
        distanceTravelled += this.getEnvironment().getEvaluationInfo().mushroomsDevoured * ((NeatAgent)controller).getHueristics().get(i)[1];
        distanceTravelled += this.getEnvironment().getEvaluationInfo().flowersDevoured * ((NeatAgent)controller).getHueristics().get(i)[2];
        distanceTravelled += this.getEnvironment().getEvaluationInfo().coinsGained / 100 * ((NeatAgent)controller).getHueristics().get(i)[3];
        distanceTravelled += this.getEnvironment().getEvaluationInfo().killsByShell * ((NeatAgent)controller).getHueristics().get(i)[4];
        distanceTravelled += this.getEnvironment().getEvaluationInfo().killsByStomp / 10 * ((NeatAgent)controller).getHueristics().get(i)[5];
        distanceTravelled += ((NEATNeuralNet)((NeatAgent)controller).getNet()).connectionCount() * ((NeatAgent)controller).getHueristics().get(i)[6];
        distanceTravelled += ((NEATNeuralNet)((NeatAgent)controller).getNet()).connectionCount() * ((NeatAgent)controller).getHueristics().get(i)[7];
    	
        if(distanceTravelled < 0)
        	distanceTravelled = 0;
        results[i + 1] = distanceTravelled;
        distanceTravelled = 0;
    }
    
    return results;
}

public double[] evaluateAll(Agent controller)
{

   double fitn[] = this.evaluateSingleLevel(0, 40, this.uniqueSeed, false, controller);


    this.uniqueSeed += 1;
    this.fitnessEvaluations++;
    //this.dumpFitnessEvaluation(fitn, "fitnesses-");
    return fitn;
}

public void dumpFitnessEvaluation(float fitness, String fileName)
{
    try
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName + fileTimeStamp + ".txt", true));
        out.write(this.fitnessEvaluations + " " + fitness + "\n");
        out.close();
    } catch (IOException e)
    {
        e.printStackTrace();
    }
}

public void doEpisodes(int amount, boolean verbose, final int repetitionsOfSingleEpisode)
{
    System.out.println("amount = " + amount);
}

public boolean isFinished()
{
    System.out.println("options = " + options);
    return false;
}

}
