package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.IOException;
import java.io.InputStream;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Layer3Header;

public abstract class Ipv4OptionHeader extends Layer3Header {

	@Override
	public int getHdrIndex() {
		return -1;
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, int nexthdr) throws IOException {
		return null;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		throw new IllegalArgumentException("Not implemented yet; sorry.");
	}

}
