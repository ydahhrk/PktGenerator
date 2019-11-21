package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;

/**
 * A header representing the meat of an IPv6 error report.
 */
public class Icmpv6ErrorHeader extends Icmpv6Header {

	public Icmpv6ErrorHeader() {
		type = 1;
		code = 4;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv6ErrorHeader());
	}

	@Override
	public String getShortName() {
		return "i6err";
	}

	@Override
	public String getName() {
		return "ICMPv6 Error Header";
	}

	@Override
	protected Header getNextHeader() {
		return new Ipv6Header();
	}

	protected Integer[][] getAvailableTypes() {
		return new Integer[][] { //
				/* Destination Unreachable */
				{ 1, 0 }, //
				{ 1, 1 }, //
				{ 1, 2 }, //
				{ 1, 3 }, //
				{ 1, 4 }, //
				/* Packet Too Big */
				{ 2, null }, //
				/* Time Exceeded */
				{ 3, null }, //
				/* Parameter problem */
				{ 4, 0 }, //
				{ 4, 1 }, //
				{ 4, 2 }, //
		};
	}
}
