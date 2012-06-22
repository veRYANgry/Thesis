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

import java.io.Serializable;

import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy,
 * sergey.karakovskiy@gmail.com Date: Apr 8, 2009 Time: 4:03:46 AM
 */

public class NeatAgent extends BasicMarioAIAgent implements Agent {
	final int numberOfOutputs = Environment.numberOfKeys;
	final int numberOfInputs = 14;
	NeuralNet net;

	public NeatAgent() {
		super("NeatAgent");
		this.zLevelEnemies = 2;
		this.zLevelScene = 2;

	}

	public NeatAgent(NeuralNet net) {
		super("NeatAgent");
		this.net = net;
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

		int which = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				inputs[which++] = probe(i, j, scene);
			}
		}

		inputs[inputs.length - 5] = marioMode == 0 ? 1 : 0;
		inputs[inputs.length - 4] = isMarioCarrying ? 1 : 0;
		inputs[inputs.length - 3] = isMarioOnGround ? 1 : 0;
		inputs[inputs.length - 2] = isMarioAbleToJump ? 1 : 0;
		inputs[inputs.length - 1] = isMarioAbleToShoot ? 1 : 0;

		ip = new MarioInput();

		ip.SetNetworkInput(inputs);

		// execute net over data set

		opSet = net.execute(ip);
		op = opSet.nextOutput().values();

		boolean[] action = new boolean[numberOfOutputs];
		for (int i = 0; i < action.length; i++) {
			action[i] = op[i] > .75;
		}

		return action;
	}

	private double probe(int x, int y, byte[][] scene) {
		int realX = x + 11;
		int realY = y + 11;
		// System.out.println( "level is:" + levelScene[realX][realY] +
		// "enemy is:" + enemies[realX][realY] + "output is" +
		// (double)(levelScene[realX][realY] + enemies[realX][realY]) / 100);
		if (levelScene[realX][realY] == 1)
			return 1;
		else if (enemies[realX][realY] == 1)
			return 0;
		else
			return .5;
	}
}