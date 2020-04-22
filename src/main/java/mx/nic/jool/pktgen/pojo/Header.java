package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.io.InputStream;

import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

/**
 * A group of continuous fields in a packet that share a protocol.
 * <p>
 * Please note: {@link Payload} is also considered a header as far as this
 * implementation is concerned, even though technically it's not. This class
 * used to be called "PacketContent" to reflect this, but I realized it's also
 * not strictly correct either because header fields are also technically packet
 * contents yet aren't supposed to inherit from this class. So I considered
 * calling this class "LogicalAggrupationOfPacketBytesOftenByProtocol", but
 * found "Header" to be far more snappy and meaningful for people reading the
 * code.
 */
public interface Header {

	/**
	 * Assigns random values to this header's fields, but tries hard to make it
	 * so the header is still reasonably valid.
	 * <p>
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
	 * <p>
	 * Returns an instance of the header that follows so the caller can continue
	 * reading.
	 */
	public Header loadFromStream(InputStream in) throws IOException;

	/**
	 * Returns a short string uniquely identifing this header's protocol.
	 */
	public String getShortName();
	
	public String getName();

	/**
	 * Returns the identifier the IANA assigned to this header type.
	 * <p>
	 * The previous header identifies this header's type by having this number
	 * in its "nexthdr" (or, in IPv4's case, "protocol") field.
	 * <p>
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
	 * <p>
	 * TODO Review the deepness of the copy implementations.
	 */
	public Header createClone();

	/**
	 * Nullifies all the checksums in this header.
	 * <p>
	 * (The idea is to correct all checksums. Checksums left unset are
	 * autocomputed later when the packet is committed into a file.)
	 */
	public void unsetChecksum();

	/**
	 * Nullifies all the length fields in this header.
	 * <p>
	 * (The idea is to correct all lengths. Lengths left unset are autocomputed
	 * later when the packet is committed into a file.)
	 */
	public void unsetLengths();

	public void swapIdentifiers();

	public Shortcut[] getShortcuts();

}
