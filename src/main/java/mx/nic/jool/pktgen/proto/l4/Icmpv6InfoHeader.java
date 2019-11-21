package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

/**
 * A header representing the meat of an IPv6 error report.
 */
public class Icmpv6InfoHeader extends Icmpv6Header {

	public Icmpv6InfoHeader() {
		type = 128;
		code = 0;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv6InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i6info";
	}

	@Override
	public String getName() {
		return "ICMPv6 Info Header";
	}

	@Override
	protected Header getNextHeader() {
		return new Payload();
	}

	@Override
	protected Integer[][] getAvailableTypes() {
		return new Integer[][] { //
				{ 128, 0 }, // Echo Request
				{ 129, 0 }, // Echo Reply
		};
	}
}
