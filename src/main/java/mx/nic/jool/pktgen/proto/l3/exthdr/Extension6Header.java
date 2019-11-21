package mx.nic.jool.pktgen.proto.l3.exthdr;

import mx.nic.jool.pktgen.proto.l3.Layer3Header;

/**
 * https://tools.ietf.org/html/rfc2460#section-4
 */
public abstract class Extension6Header extends Layer3Header {

	@Override
	public byte[] getPseudoHeader(int payloadLength, int nexthdr) {
		return null;
	}
	
	@Override
	public void unsetChecksum() {
		// No checksum.
	}
}
