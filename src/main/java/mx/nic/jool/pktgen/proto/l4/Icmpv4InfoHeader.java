package mx.nic.jool.pktgen.proto.l4;

/**
 * A header representing the meat of an IPv4 error report.
 */
public class Icmpv4InfoHeader extends Icmpv4Header {

	public Icmpv4InfoHeader() {
		super(8, 0);
	}

	@Override
	public String getName() {
		return "ICMPv4 Info Header";
	}

}
