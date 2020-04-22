package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;

public class TtlDecShortcut implements Shortcut {

	@Override
	public String getName() {
		return "ttl--";
	}

	@Override
	public void apply(Header header, FieldScanner scanner) {
		if (header instanceof Ipv4Header) {
			Ipv4Header header4 = (Ipv4Header) header;
			header4.decTtl();
		} else if (header instanceof Ipv6Header) {
			Ipv6Header header6 = (Ipv6Header) header;
			header6.decTtl();
		} else {
			throw new IllegalArgumentException("Header has no TTL. Don't know what to do.");
		}
	}

}
