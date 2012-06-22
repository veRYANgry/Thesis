package ch.idsia.agents.controllers;

import org.neat4j.neat.data.core.NetworkInput;

public class MarioInput implements NetworkInput {

	
	double[] inputs;
	@Override
	public double[] pattern() {
		return inputs;
	}
	
	public void SetNetworkInput(double[] inputs) {
		this.inputs = inputs;
	}
	
	

}
