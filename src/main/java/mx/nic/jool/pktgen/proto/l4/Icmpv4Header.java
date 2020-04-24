package mx.nic.jool.pktgen.proto.l4;

/**
 * https://tools.ietf.org/html/rfc792#page-4
 */
public abstract class Icmpv4Header extends IcmpHeader {

	public Icmpv4Header(int defaultType, int defaultCode) {
		super(defaultType, defaultCode);
	}

	@Override
	public boolean csumIncludesPseudoheader() {
		return false;
	}

	@Override
	public int getHdrIndex() {
		return 1;
	}

}
