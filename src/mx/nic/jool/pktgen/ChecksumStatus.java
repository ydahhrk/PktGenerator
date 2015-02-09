package mx.nic.jool.pktgen;

public enum ChecksumStatus {

	CORRECT("csumok"), //
	WRONG("csumfail"), //
	ZERO("csum0");

	private String shortName;

	private ChecksumStatus(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

}
