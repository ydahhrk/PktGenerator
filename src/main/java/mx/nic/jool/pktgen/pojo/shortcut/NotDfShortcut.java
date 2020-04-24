package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;

public class NotDfShortcut implements Shortcut {

	@Override
	public String getName() {
		return "!df";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof Ipv4Header))
			throw new IllegalArgumentException("Header is not IPv4 header. Don't know what to do.");

		((Ipv4Header) header).notDf();
	}

}
