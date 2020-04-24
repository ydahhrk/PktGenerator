package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.ChecksumBuilder;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;

/**
 * A header from the {@link Layer#TRANSPORT} layer, plus ICMP.
 */
public abstract class Layer4Header extends Header {

	/**
	 * Adds the pseudoheader bytes to the <code>csum</code> checksum (which is
	 * assumed to be this header's checksum).
	 */
	private void includePseudoHeader(ChecksumBuilder csum) {
		Layer3Header lastL3Header = null;

		for (Header iterator = getPrevious(); iterator != null; iterator = iterator.getPrevious()) {
			if (iterator instanceof Ipv4Header || iterator instanceof Ipv6Header) {
				lastL3Header = (Layer3Header) iterator;
				break;
			}
		}
		
		if (lastL3Header == null) {
			System.out.println("Warning: I'm a transport header and I don't have a network header. "
					+ "My checksum will not include a pseudoheader.");
			return;
		}

		int payloadLength = 0;
		for (Header header = this; header != null; header = header.getNext())
			payloadLength += header.getLength();

		csum.write(lastL3Header.getPseudoHeader(payloadLength, getHdrIndex()));
	}

	/**
	 * Computes and returns this header's checksum.
	 * <p>
	 * It is assumed to be the typical IP checksum: A negated sum of this
	 * header, its payload and (optionally) a pseudoheader. Zero not allowed.
	 */
	protected int buildChecksum(boolean includePseudoheader) throws IOException {
		try (ChecksumBuilder csum = new ChecksumBuilder()) {
			if (includePseudoheader)
				includePseudoHeader(csum);

			for (Header header = this; header != null; header = header.getNext())
				csum.write(header.toWire());

			int result = csum.finish(null);
			if (result == 0) {
				result = ~result;
			}
			return result;
		}
	}

	@Override
	public Shortcut[] getShortcuts() {
		return null;
	}
}
