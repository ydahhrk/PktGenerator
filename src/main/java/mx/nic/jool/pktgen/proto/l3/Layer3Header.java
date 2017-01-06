package mx.nic.jool.pktgen.proto.l3;

import java.io.IOException;

import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.PacketContent;

public abstract class Layer3Header implements PacketContent {

	@Override
	public Layer getLayer() {
		return Layer.INTERNET;
	}

	public abstract byte[] getPseudoHeader(int payloadLength, int nexthdr) throws IOException;

}
