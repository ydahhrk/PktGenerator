package mx.nic.jool.pktgen.proto.optionsdata6;

public enum OptionDataTypes {
	
	PAD1(0x00),							//				[IPV6]
	PADN(0x01);							//				[IPV6]
//	JUMBO_PAYLOAD(0xC2),				//				[RFC2675]
//	RPL_OPTION(0x63),					//				[RFC6553]
//	TUNNEL_ENCAPSULATION_LIMIT(0x04),   //				[RFC2473]
//	ROUTER_ALERT(0x05),					//Router Alert	[RFC2711]
//	QUICK_START(0x26),					//Quick-Start	[RFC4782][RFC Errata 2034]
//	CALIPSO(0x07),						//CALIPSO		[RFC5570]
//	SMF_DPD(0x08),						//SMF_DPD		[RFC6621]
//	HOME_ADDRESS(0xC9),					//Home Address	[RFC6275]
//	ENDPOINT_IDENTIFICATION(0x8A),		//Endpoint Identification (DEPRECATED)	[[CHARLES LYNN]]
//	ILNP_NONCE(0x8B),					//ILNP Nonce	[RFC6744]
//	LINE_IDENTIFICATION(0x8C),			//Line-Identification Option	[RFC6788]
//	MPL_OPTION(0x6D),					//MPL Option	[draft-ietf-roll-trickle-mcast]
//	IP_DFF(0xEE);						//IP_DFF		[RFC6971]
	
	private int optType;
	
	private OptionDataTypes(int number) {
		this.optType = number;
	}
	
	public static OptionDataTypes fromInt(int optType) {
		for (OptionDataTypes optData : values()) {
			if (optData.optType == optType)
				return optData;
		}
		
		return null;
	}

	public int toWire() {
		return optType;
	}
	
}
