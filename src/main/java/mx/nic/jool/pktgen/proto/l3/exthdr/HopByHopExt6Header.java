package mx.nic.jool.pktgen.proto.l3.exthdr;

/**
 * https://tools.ietf.org/html/rfc2460#section-4.3
 */
public class HopByHopExt6Header extends TlvExt6Header {

	@Override
	public TlvExt6Header instanceSelf() {
		return new HopByHopExt6Header();
	}

	@Override
	public String getShortName() {
		return "hhext";
	}

	@Override
	public int getHdrIndex() {
		return 0;
	}

}
