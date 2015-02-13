package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.CsumBuilder;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Reflect;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;


public abstract class Layer4Header implements PacketContent {

	private void includePseudoHeader(Packet packet, Fragment fragment,
			CsumBuilder csum) throws IOException {
		Layer3Header lastL3Header = null;

		for (PacketContent content : fragment) {
			if (content instanceof Layer3Header)
				lastL3Header = (Layer3Header) content;
			else if (content == this)
				break;
		}

		if (lastL3Header == null) {
			System.out.println("Warning: I'm a " + getProtocol()
					+ " header and I don't have a network header. "
					+ "My checksum won't include a pseudoheader.");
			return;
		}

		int payloadLength = this.toWire().length;
		for (PacketContent content : packet.getL4ContentAfter(this)) {
			payloadLength += content.toWire().length;
		}
		csum.write(lastL3Header.getPseudoHeader(payloadLength, getProtocol()));
	}

	protected int buildChecksum(Packet packet, Fragment fragment,
			boolean includePseudoheader) throws IOException {
		CsumBuilder csum = new CsumBuilder();

		try {
			if (includePseudoheader)
				includePseudoHeader(packet, fragment, csum);

			csum.write(this.toWire());
			for (PacketContent content : packet.getL4ContentAfter(this)) {
				csum.write(content.toWire());
			}

			int result = csum.finish(null);
			if (result == 0) {
				result = ~result;
			}
			return result;

		} finally {
			csum.close();
		}
	}

}
