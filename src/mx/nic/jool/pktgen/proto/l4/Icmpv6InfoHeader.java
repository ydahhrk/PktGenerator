package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.PacketContent;

public class Icmpv6InfoHeader extends Icmpv6Header {

	public Icmpv6InfoHeader() {
		type = 128;
		code = 0;
		restOfHeader1 = 1;
		restOfHeader2 = 2;
	}

	@Override
	public PacketContent createClone() {
		return createCloneIcmp(new Icmpv6InfoHeader());
	}
	
	@Override
	public String getShortName() {
		return "icmp6info";
	}

}
