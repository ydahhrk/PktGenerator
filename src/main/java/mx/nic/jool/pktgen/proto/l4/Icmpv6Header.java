package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;

public abstract class Icmpv6Header extends IcmpHeader {

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (checksum == null) {
			checksum = buildChecksum(packet, fragment, true);
		}
	}

	@Override
	public int getHdrIndex() {
		return 58;
	}

}
