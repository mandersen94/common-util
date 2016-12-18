package org.markandersen.service.invocation;

import java.util.List;

/**
 * DOCUMENT ME!
 * 
 * @author MAndersen
 */
public interface EnhancedInvocationStrategyFactory<T> extends
		InvocationStrategyInstanceFactory<T> {

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> calculateGoodReferences();

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public List<T> calculateBadReferences();
}