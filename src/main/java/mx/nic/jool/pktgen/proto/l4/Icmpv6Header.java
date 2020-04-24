package mx.nic.jool.pktgen.proto.l4;

/**
 * https://tools.ietf.org/html/rfc4443#section-2.1
 */
public abstract class Icmpv6Header extends IcmpHeader {

	public Icmpv6Header(int defaultType, int defaultCode) {
		super(defaultType, defaultCode);
	}

	@Override
	public boolean csumIncludesPseudoheader() {
		return true;
	}

	@Override
	public int getHdrIndex() {
		return 58;
	}

}
