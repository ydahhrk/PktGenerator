package mx.nic.jool.pktgen.proto.l3;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

/**
 * A header from the {@link Layer#INTERNET} layer, except ICMP.
 */
public abstract class Layer3Header extends Header {

	@Override
	public Shortcut[] getShortcuts() {
		return null;
	}

	public abstract byte[] getPseudoHeader(int payloadLength, int nextHdr);

	public int getNextHdr() {
		Header next = this.getNext();
		if (next == null)
			throw new IllegalArgumentException("There are no valid nexthdrs after this. I don't know what to do.");

		if (next.getHdrIndex() >= 0)
			return next.getHdrIndex();

		throw new IllegalArgumentException("Next header lacks nexthdr value. Try assigning nexthdr manually.");
	}

}
