package org.markandersen.test;

/**
 * DOCUMENT ME!
 */
public class ExampleClass {

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private int size;

	/**
	 * 
	 */
	private int index;

	/**
	 * Creates a new ExampleClass object.
	 */
	public ExampleClass() {
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param index
	 */
	public ExampleClass(int index) {
		this.index = index;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg1
	 * @param arg2
	 * 
	 * @return
	 */
	public int add(int arg1, int arg2) {
		System.out.println("ExampleClass.add().  index = " + index);
		return arg1 + arg2;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public String toString() {
		return name;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
}