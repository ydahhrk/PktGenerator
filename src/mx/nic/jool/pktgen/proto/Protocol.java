package mx.nic.jool.pktgen.proto;

public enum Protocol {

	PAYLOAD(-1, 7),
	IPV4(-4, 3),
	IPV6(-6, 3),
	ICMPV4(1, 4), /* Shut up. It's layer 4 because it's totally layer 4. */
	TCP(6, 4),
	UDP(17, 4),
	ICMPV6(58, 4),
	HOP_BY_HOP_EXT6HDR(0, 3),
	ROUTING_EXT6HDR(43, 3),
	FRAGMENT_EXT6HDR(44, 3),
	DESTINATION_OPTION_EXT6HDR(60, 3);
	
	private int code;
	private int layer;
	
	private Protocol(int number, int layer) {
		this.code = number;
		this.layer = layer;
	}
	
	public static Protocol fromInt(int proto) {
		for (Protocol protocol : values()) {
			if (protocol.code == proto)
				return protocol;
		}
		
		return null;
	}

	public int toWire() {
		return code;
	}

	public int getLayer() {
		return layer;
	}
	
}
