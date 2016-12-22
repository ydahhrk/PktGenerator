package mx.nic.jool.pktgen.enums;

/**
 * This refers to the TCP/IP model, not the OSI one.
 */
public enum Layer {

	NETWORK_ACCESS(2), //
	/** TCP/IP equivalent of OSI's "Network" or "IP" Layer. */
	INTERNET(3), //
	TRANSPORT(4), //
	APPLICATION(5); //

	/**
	 * This layer's number +1.
	 * 
	 * Why +1? Because everyone expects IP to be layer 3. Likewise for TCP/UDP.
	 */
	private int num;

	private Layer(int num) {
		this.num = num;
	}

	/** "Is this layer higher than <code>other</code> in the stack?" */
	public boolean ht(Layer other) {
		return this.num > other.num;
	}
}
