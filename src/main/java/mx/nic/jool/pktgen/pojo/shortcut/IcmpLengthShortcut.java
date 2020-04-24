package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6ErrorHeader;

public class IcmpLengthShortcut implements Shortcut {

	@Override
	public String getName() {
		return "length";
	}

	@Override
	public void apply(Header header, String value) {
		int length = Integer.valueOf(value);

		if (header instanceof Icmpv4ErrorHeader) {
			Icmpv4ErrorHeader hdr4 = (Icmpv4ErrorHeader) header;
			hdr4.setLength(length);

		} else if (header instanceof Icmpv6ErrorHeader) {
			Icmpv6ErrorHeader hdr6 = (Icmpv6ErrorHeader) header;
			hdr6.setLength(length);

		} else {
			throw new IllegalArgumentException("Header has no tricky length. Don't know what to do.");
		}
	}

}
