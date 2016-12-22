package mx.nic.jool.pktgen.proto.l3;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.PacketContent;

public abstract class Layer3Header implements PacketContent {

	@Override
	public int getLayer() {
		return 3;
	}

	public abstract byte[] getPseudoHeader(int payloadLength, int nexthdr) throws IOException;

}
