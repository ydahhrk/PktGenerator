package mx.nic.jool.pktgen.proto.l4;

/**
 * A header representing the meat of an IPv6 error report.
 */
public class Icmpv6InfoHeader extends Icmpv6Header {

	public Icmpv6InfoHeader() {
		super(128, 0);
	}

	@Override
	public String getName() {
		return "ICMPv6 Info Header";
	}

}
