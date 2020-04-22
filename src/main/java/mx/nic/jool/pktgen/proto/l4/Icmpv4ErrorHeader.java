package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.IcmpLengthShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;

/**
 * A header representing the meat of an IPv4 error report.
 */
public class Icmpv4ErrorHeader extends Icmpv4Header {

	public Icmpv4ErrorHeader() {
		type = 3;
		code = 3;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv4ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "i4err";
	}
	
	@Override
	public String getName() {
		return "ICMPv4 Error Header";
	}

	@Override
	protected Header getNextHeader() {
		return new Ipv4Header();
	}

	@Override
	protected Integer[][] getAvailableTypes() {
		return new Integer[][] { //
				/* Destination Unreachable */
				{ 3, 0 }, //
				{ 3, 1 }, //
				{ 3, 2 }, //
				{ 3, 3 }, //
				{ 3, 4 }, //
				{ 3, 5 }, //
				{ 3, 6 }, //
				{ 3, 7 }, //
				{ 3, 8 }, //
				{ 3, 9 }, //
				{ 3, 10 }, //
				{ 3, 11 }, //
				{ 3, 12 }, //
				{ 3, 13 }, //
				{ 3, 14 }, //
				{ 3, 15 }, //
				/* Time Exceeded */
				{ 11, null }, //
				/* Parameter Problem */
				{ 12, 0 }, //
				{ 12, 1 }, //
				{ 12, 2 }, //
		};
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { new IcmpLengthShortcut() };
	}

	public void setLength(int length) {
		this.rest1 &= 0xFF00;
		this.rest1 |= length & 0xFF;
	}

}
