package org.markandersen.service.invocation.ha;

/**
 * 
 * @author mandersen
 */
public interface ViewPropagationStrategy {

	/**
	 * 
	 * @param view
	 */
	void addViewToRequest(View view);

}
