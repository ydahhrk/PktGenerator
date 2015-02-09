package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.PacketContent;

public class Icmpv4ErrorHeader extends Icmpv4Header {

	public Icmpv4ErrorHeader() {
		type = 3;
		code = 3;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv4ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "icmp4err";
	}

}
