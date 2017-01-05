package mx.nic.jool.pktgen.proto.l3.exthdr;

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
