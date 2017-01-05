package mx.nic.jool.pktgen.proto.l3.exthdr;

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
