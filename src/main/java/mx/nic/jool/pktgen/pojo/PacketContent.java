package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.io.InputStream;

import mx.nic.jool.pktgen.enums.Layer;

public interface PacketContent {

	/**
	 * Assigns random values to this header's fields, but tries hard to make it
	 * so the header is still reasonably valid.
	 * 
	 * "Random" mode.
	 */
	public void randomize();

	/**
	 * Serializes this header into its binary representation, exactly as it
	 * would be represented in a network packet.
	 */
	public byte[] toWire();

	/**
	 * Loads this header from its {@link #toWire()} representation, being read
	 * from <code>in</code>.
	 * 
	 * Returns an instance of the header that follows so the caller can continue
	 * reading.
	 */
	public PacketContent loadFromStream(InputStream in) throws IOException;

	/**
	 * Returns a short string uniquely identifing this header's protocol.
	 */
	public String getShortName();

	/**
	 * Returns the identifier the IANA assigned to this header type.
	 * 
	 * The previous header identifies this header's type by having this number
	 * in its "nexthdr" (or, in IPv4's case, "protocol") field.
	 * 
	 * If the header type is not supposed to be previously nexthdr'd, this will
	 * return a negative number.
	 */
	public int getHdrIndex();

	/**
	 * Returns the layer in the IP/TCP protocol stack this header belongs to.
	 */
	public Layer getLayer();

	/**
	 * Automatically assigns a value to any fields the user left unset ("auto").
	 */
	public void postProcess(Packet packet, Fragment fragment) throws IOException;

	/**
	 * Builds and returns a deep copy of this header.
	 * 
	 * TODO Review the deepness of the copy implementations.
	 */
	public PacketContent createClone();

	public void unsetChecksum();

	public void unsetLengths();

}
