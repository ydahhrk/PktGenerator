package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;

public class Icmpv6ErrorHeader extends Icmpv6Header {

	public Icmpv6ErrorHeader() {
		type = 1;
		code = 4;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv6ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "i6err";
	}

	@Override
	protected PacketContent getNextContent() {
		return new Ipv6Header();
	}

}
