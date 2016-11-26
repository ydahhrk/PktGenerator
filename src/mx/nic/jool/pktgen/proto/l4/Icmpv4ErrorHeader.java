package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;

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
		return "i4err";
	}

	@Override
	protected PacketContent getNextContent() {
		return new Ipv4Header();
	}

}
