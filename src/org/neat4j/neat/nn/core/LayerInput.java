/*
 * Created on Sep 27, 2004
 *
 */
package org.neat4j.neat.nn.core;

import java.io.Serializable;

/**
 * @author MSimmerson
 *
 */
public interface LayerInput  extends Serializable {
	double[] inputs();
}
