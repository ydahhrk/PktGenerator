package mx.nic.jool.pktgen.proto.l3.exthdr;

/**
 * https://tools.ietf.org/html/rfc2460#section-4.6
 */
public class DestinationOptionExt6Header extends TlvExt6Header {

	@Override
	public TlvExt6Header instanceSelf() {
		return new DestinationOptionExt6Header();
	}

	@Override
	public String getShortName() {
		return "dext";
	}

	@Override
	public int getHdrIndex() {
		return 60;
	}

}
