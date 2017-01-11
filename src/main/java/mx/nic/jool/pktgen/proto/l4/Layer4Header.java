package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.util.Stack;

import mx.nic.jool.pktgen.ChecksumBuilder;
import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;

/**
 * A header from the {@link Layer#TRANSPORT} layer, plus ICMP.
 */
public abstract class Layer4Header implements Header {

	/**
	 * Adds the pseudoheader bytes to the <code>csum</code> checksum (which is
	 * assumed to be this header's checksum).
	 */
	private void includePseudoHeader(Packet packet, Fragment fragment, ChecksumBuilder csum) throws IOException {
		Layer3Header lastL3Header = null;

		for (Header header : fragment) {
			if (header instanceof Layer3Header)
				lastL3Header = (Layer3Header) header;
			else if (header == this)
				break;
		}

		if (lastL3Header == null) {
			System.out.println("Warning: I'm a transport header and I don't have a network header. "
					+ "My checksum will not include a pseudoheader.");
			return;
		}

		int payloadLength = this.toWire().length;
		for (Header header : packet.getUpperLayerHeadersAfter(this)) {
			payloadLength += header.toWire().length;
		}

		Stack<Header> headerBefore = fragment.getHeaderBefore(this);
		while (!headerBefore.empty()) {
			Header header = headerBefore.pop();
			if (header instanceof Layer3Header) {
				byte[] pseudoHeader = ((Layer3Header) header).getPseudoHeader(payloadLength, getHdrIndex());
				if (pseudoHeader != null) {
					csum.write(pseudoHeader);
					break;
				}
			}
		}
	}

	/**
	 * Computes and returns this header's checksum.
	 * <p>
	 * It is assumed to be the typical IP checksum: A negated sum of this
	 * header, its payload and (optionally) a pseudoheader. Zero not allowed.
	 */
	protected int buildChecksum(Packet packet, Fragment fragment, boolean includePseudoheader) throws IOException {
		try (ChecksumBuilder csum = new ChecksumBuilder()) {
			if (includePseudoheader)
				includePseudoHeader(packet, fragment, csum);

			csum.write(this.toWire());
			for (Header header : packet.getUpperLayerHeadersAfter(this)) {
				csum.write(header.toWire());
			}

			int result = csum.finish(null);
			if (result == 0) {
				result = ~result;
			}
			return result;
		}
	}

	@Override
	public Layer getLayer() {
		return Layer.TRANSPORT;
	}

}
