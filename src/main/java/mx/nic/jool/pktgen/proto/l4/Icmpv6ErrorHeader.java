package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.shortcut.IcmpLengthShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

/**
 * A header representing the meat of an IPv6 error report.
 */
public class Icmpv6ErrorHeader extends Icmpv6Header {

	public Icmpv6ErrorHeader() {
		super(1, 4);
	}

	@Override
	public String getName() {
		return "ICMPv6 Error Header";
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { new IcmpLengthShortcut() };
	}

	public void setLength(int length) {
		int rest1 = this.rest1.getValue();
		rest1 &= 0xFF;
		rest1 |= (length & 0xFF) << 8;
		this.rest1.setValue(rest1);
	}
}
