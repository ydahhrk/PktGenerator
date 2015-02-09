package mx.nic.jool.pktgen.proto.optionsdata4;

public enum Ipv4Options {

	END_OPTION_LIST(1),
	NO_OPERATION(2),
	RECORDS_ROUTE(3),
	STREAM_IDENTIFIER(4),
	INTERNET_TIMESTAMP(5);
	
	private int code;
	
	private Ipv4Options(int code){
		this.code = code;
	}
	
	public static Ipv4Options fromInt(int code) {
		for (Ipv4Options ipv4Option : values()) {
			if (ipv4Option.code == code) {
				return ipv4Option;
			}
		}
		
		return null;
	}
	
	public int toWire() {
		return code;
	}
}
