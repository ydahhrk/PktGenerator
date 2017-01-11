package mx.nic.jool.pktgen;

public enum ChecksumStatus {

	/** A correct checksum. */
	CORRECT("csumok"),
	/** An incorrect checksum, except zero. */
	WRONG("csumfail"),
	/** Zero. */
	ZERO("csum0");

	private String shortName;

	private ChecksumStatus(String shortName) {
		this.shortName = shortName;
	}

	public String getShortName() {
		return shortName;
	}

}
