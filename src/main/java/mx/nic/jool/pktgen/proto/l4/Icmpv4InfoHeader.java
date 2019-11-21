package mx.nic.jool.pktgen.proto.l4;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

/**
 * A header representing the meat of an IPv4 error report.
 */
public class Icmpv4InfoHeader extends Icmpv4Header {

	public Icmpv4InfoHeader() {
		type = 8;
		code = 0;
	}

	@Override
	public Header createClone() {
		return createCloneIcmp(new Icmpv4InfoHeader());
	}

	@Override
	public String getShortName() {
		return "i4info";
	}

	@Override
	public String getName() {
		return "ICMPv4 Info Header";
	}

	@Override
	protected Header getNextHeader() {
		return new Payload();
	}

	@Override
	protected Integer[][] getAvailableTypes() {
		return new Integer[][] { //
				{ 8, 0 }, // Echo Request
				{ 0, 0 }, // Echo Reply
		};
	}
}
