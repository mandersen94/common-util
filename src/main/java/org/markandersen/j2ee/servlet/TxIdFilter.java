package org.markandersen.j2ee.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;

/**
 * 
 * @author mark@markandersen.org
 */
public class TxIdFilter implements Filter {

	public static final String TXID_KEY = "txId";
	
	private IdGenerator generator;
	
	/**
	 * 
	 */
	public void init(FilterConfig arg0) throws ServletException {
		generator = new SimpleIdGenerator();
	}

	/**
	 * 
	 */
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;

		try {
			addTxIdToThread();
			filterChain.doFilter(request, response);
		} finally {
			removeTxIdFromThread();
		}
	}

	/**
	 * 
	 */
	private void addTxIdToThread() {
		String txId = generator.getNextId();
		TxId.set(txId);
		MDC.put(TXID_KEY, txId);
		NDC.push(txId);
	}

	/**
	 * 
	 */
	private void removeTxIdFromThread() {
		TxId.clear();
		MDC.remove(TXID_KEY);
		NDC.remove();
	}

	public void destroy() {

	}

}
