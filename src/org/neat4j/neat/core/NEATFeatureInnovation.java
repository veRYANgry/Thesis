package org.neat4j.neat.core;

/**
 * Extra Feature innovation database entry. 
 * @author MSimmerson
 *
 */
public class NEATFeatureInnovation implements NEATInnovation {
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
