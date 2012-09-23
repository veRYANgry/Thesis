package org.neat4j.neat.core;

public class NEATSelfRegulationInnovation implements NEATInnovation {
	private int innvovationId;
	
	public void setInnovationId(int id) {
		this.innvovationId = id;
	}

	public int innovationId() {
		return (this.innvovationId);
	}

	public int type() {
		return 0;
	}

}
