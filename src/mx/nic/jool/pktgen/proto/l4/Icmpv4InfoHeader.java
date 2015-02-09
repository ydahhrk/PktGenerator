package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.PacketContent;

public class Icmpv4InfoHeader extends Icmpv4Header {

	public Icmpv4InfoHeader() {
		type = 8;
		code = 0;
		restOfHeader1 = 1;
		restOfHeader2 = 2;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv4InfoHeader());
	}

	@Override
	public String getShortName() {
		return "icmp4info";
	}

}
