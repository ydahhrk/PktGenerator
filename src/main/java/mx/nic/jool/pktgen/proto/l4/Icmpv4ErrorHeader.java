package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.shortcut.IcmpLengthShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

/**
 * A header representing the meat of an IPv4 error report.
 */
public class Icmpv4ErrorHeader extends Icmpv4Header {

	public Icmpv4ErrorHeader() {
		super(3, 3);
	}

	@Override
	public String getName() {
		return "ICMPv4 Error Header";
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { new IcmpLengthShortcut() };
	}

	public void setLength(int length) {
		int rest1 = this.rest1.getValue();
		rest1 &= 0xFF00;
		rest1 |= length & 0xFF;
		this.rest1.setValue(rest1);
	}

}
