package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.util.Stack;

import mx.nic.jool.pktgen.ChecksumBuilder;
import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;

public abstract class Layer4Header extends PacketContent {

	private void includePseudoHeader(Packet packet, Fragment fragment, ChecksumBuilder csum) throws IOException {
		Layer3Header lastL3Header = null;

		for (PacketContent content : fragment) {
			if (content instanceof Layer3Header)
				lastL3Header = (Layer3Header) content;
			else if (content == this)
				break;
		}

		if (lastL3Header == null) {
			System.out.println("Warning: I'm a transport header and I don't have a network header. "
					+ "My checksum will not include a pseudoheader.");
			return;
		}

		int payloadLength = this.toWire().length;
		for (PacketContent content : packet.getL4ContentAfter(this)) {
			payloadLength += content.toWire().length;
		}

		Stack<PacketContent> contentBefore = fragment.getContentBefore(this);
		while (!contentBefore.empty()) {
			PacketContent content = contentBefore.pop();
			if (content instanceof Layer3Header) {
				byte[] pseudoHeader = ((Layer3Header) content).getPseudoHeader(payloadLength, getHdrIndex());
				if (pseudoHeader != null) {
					csum.write(pseudoHeader);
					break;
				}
			}
		}
	}

	protected int buildChecksum(Packet packet, Fragment fragment, boolean includePseudoheader) throws IOException {
		try (ChecksumBuilder csum = new ChecksumBuilder()) {
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
		}
	}

	@Override
	public Layer getLayer() {
		return Layer.TRANSPORT;
	}

}
