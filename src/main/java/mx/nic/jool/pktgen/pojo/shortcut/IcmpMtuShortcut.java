package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l4.IcmpHeader;

public class IcmpMtuShortcut implements Shortcut {

	@Override
	public String getName() {
		return "mtu";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof IcmpHeader))
			throw new IllegalArgumentException("Header is not ICMP. Don't know what to do.");
		IcmpHeader icmp = (IcmpHeader) header;
		
		icmp.setRest2(Integer.valueOf(value));
	}

}
