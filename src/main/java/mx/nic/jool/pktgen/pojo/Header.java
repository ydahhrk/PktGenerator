package mx.nic.jool.pktgen.pojo;

import java.io.IOException;

import mx.nic.jool.pktgen.BitArrayOutputStream;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.type.Field;

/**
 * A group of continuous fields in a packet that share a protocol.
 * <p>
 * Please note: {@link Payload} is also considered a header as far as this
 * implementation is concerned, even though technically it's not. This class
 * used to be called "PacketContent" to reflect this, but I realized it's also
 * not strictly correct either because header fields are also technically packet
 * contents yet aren't supposed to inherit from this class.
 */
public abstract class Header {

	private Header previous;
	private Header next;

	public Header getPrevious() {
		return previous;
	}

	public Header getNext() {
		return next;
	}

	public void setNext(Header next) {
		this.next = next;
		next.previous = this;
	}

	public abstract String getName();

	/**
	 * Returns the identifier the IANA assigned to this header type.
	 * <p>
	 * The previous header identifies this header's type by having this number in
	 * its "nexthdr" (or, in IPv4's case, "protocol") field.
	 * <p>
	 * If the header type is not supposed to be previously nexthdr'd, this will
	 * return a negative number.
	 */
	public abstract int getHdrIndex();

	public int getNextHdr() {
		Header next = this.getNext();
		if (next == null)
			throw new IllegalArgumentException("There are no valid nexthdrs after this. I don't know what to do.");

		if (next.getHdrIndex() >= 0)
			return next.getHdrIndex();

		throw new IllegalArgumentException("Next header lacks nexthdr value. Try assigning nexthdr manually.");
	}

	public abstract Field[] getFields();

	public abstract Shortcut[] getShortcuts();

	public int getLength() {
		return Field.getLength(getFields());
	}

	/**
	 * Automatically assigns a value to any fields the user left unset ("auto").
	 */
	public abstract void postProcess() throws IOException;

	public byte[] toWire() {
		BitArrayOutputStream stream = new BitArrayOutputStream(getLength());
		for (Field field : getFields())
			field.write(stream);
		return stream.toByteArray();
	}

	public abstract void swapIdentifiers();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("[");

		sb.append(getName()).append("\n");
		for (Field field : getFields())
			sb.append("\t").append(field.getName()).append(": ").append(field).append("\n");

		return sb.append("]").toString();
	}

}
