package org.markandersen.security;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 
 * @author e63582
 * 
 */
public class FastRandom extends SecureRandom {

	public FastRandom() {

	}

	public byte[] generateSeed(int numBytes) {
		System.out.println("FastRandom.generateSeed()");
		byte[] bytes = new byte[numBytes];
		Arrays.fill(bytes, (byte) 0x01);
		return bytes;
		// return super.generateSeed(numBytes);
	}

	public synchronized void nextBytes(byte[] bytes) {
		// nop.
		// System.out.println("FastRandom.nextBytes()");
	}

	public synchronized void setSeed(byte[] seed) {
		// nop.
		// System.out.println("FastRandom.setSeed()");
	}

	public void setSeed(long seed) {
		// nop
		// System.out.println("FastRandom.setSeed(seed)");
	}

	public boolean nextBoolean() {
		return true;
	}

	public double nextDouble() {
		return 1.0d;
	}

	public float nextFloat() {
		return 1.0f;
	}

	public synchronized double nextGaussian() {
		return 1.0d;
	}

	public int nextInt() {
		return 2;
	}

	public int nextInt(int n) {
		return 2;
	}

	public long nextLong() {
		return 2;
	}

}
