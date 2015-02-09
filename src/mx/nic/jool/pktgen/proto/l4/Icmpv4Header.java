package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.proto.Protocol;

public abstract class Icmpv4Header extends IcmpHeader {

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		if (checksum == null) {
			checksum = buildChecksum(packet, fragment, false);
		}
	}
	
	@Override
	public Protocol getProtocol() {
		return Protocol.ICMPV4;
	}

}
