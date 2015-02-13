package mx.nic.jool.pktgen.proto.l3;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Reflect;
import mx.nic.jool.pktgen.proto.Protocol;

public interface Layer3Header extends PacketContent {

	public byte[] getPseudoHeader(int payloadLength, Protocol nextProtocol)
			throws IOException;

}
