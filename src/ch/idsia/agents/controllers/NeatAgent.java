/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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

package ch.idsia.agents.controllers;

import java.util.ArrayList;

import org.neat4j.neat.applications.train.VisionBound;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy,
 * sergey.karakovskiy@gmail.com Date: Apr 8, 2009 Time: 4:03:46 AM
 */

public class NeatAgent extends BasicMarioAIAgent implements Agent {
	final int numberOfOutputs = Environment.numberOfKeys;
	private VisionBound Vision;
	private int numberOfInputs;
	private NeuralNet net;
	private int pulse = 0;
	ArrayList<double[]> Hueristics;

	public NeatAgent() {
		super("NeatAgent");
		this.zLevelEnemies = 1;
		this.zLevelScene = 1;

	}

	public ArrayList<double[]> getHueristics() {
		return Hueristics;
	}

	public NeuralNet getNet() {
		return net;
	}

	public NeatAgent(NeuralNet net,VisionBound Vision, ArrayList<double[]> Hueristics) {
		super("NeatAgent");
		this.net = net;
		this.Vision = Vision;
		numberOfInputs = (Vision.XVisionStart - Vision.XVisionEnd)*(Vision.YVisionStart - Vision.YVisionEnd);
		this.Hueristics = Hueristics;
		reset();
	}

	public void reset() {

	}

	public boolean[] getAction() {
		NetworkOutputSet opSet;
		MarioInput ip;
		double[] op;

		byte[][] scene = levelScene;
		double[] inputs = new double[numberOfInputs];
	//System.out.println( "level1 is:" + levelScene[9][10] + "enemy" + enemies[9][10]);

		int which = 0;
		for (int i = Vision.XVisionStart; i < Vision.XVisionEnd ; i++) {
			for (int j = Vision.YVisionStart; j < Vision.YVisionEnd ; j++) {
				inputs[which++] = probe(i, j, scene);
			}
		}
		if(pulse == 0)
			pulse = 1;
		else
			pulse = 0;
		//inputs[inputs.length - 1] = pulse;
		//inputs[inputs.length - 5] = marioMode == 0 ? 1 : 0;
		//inputs[inputs.length - 1] = isMarioCarrying ? 1 : 0;
		//inputs[inputs.length - 3] = isMarioOnGround ? 1 : 0;
		//inputs[inputs.length - 1] = isMarioAbleToJump ? 1 : 0;
		//inputs[inputs.length - 1] = isMarioAbleToShoot ? 1 : 0;

		ip = new MarioInput();

		ip.SetNetworkInput(inputs);

		// execute net over data set

		opSet = net.execute(ip);
		op = opSet.nextOutput().values();

		boolean[] action = new boolean[numberOfOutputs];
/*		for (int i = 0; i < action.length; i++) {
			action[i] = op[i] > .75;
		}*/
		action[1] = op[0] > .75;
		action[3] = op[1] > .75;

		return action;
	}

	private double probe(int x, int y, byte[][] scene) {
		int realX = x + 11;
		int realY = y + 11;
		// System.out.println( "level is:" + levelScene[realX][realY] +
		// "enemy is:" + enemies[realX][realY] + "output is" +
		// (double)(levelScene[realX][realY] + enemies[realX][realY]) / 100);
		//return levelScene[realX][realY] + enemies[realX][realY];
		if (levelScene[realY][realX] != 0)
			return 1;
		else if (enemies[realY][realX] > 2)
			return -1;
		else
			return 0;
	}
}