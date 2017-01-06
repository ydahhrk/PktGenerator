package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Fragment;

/**
 * A menu option that unsets all checksums in a packet so they can be
 * automatically recomputed later.
 */
public class RecomputeCsumMenuEntry extends MainMenuEntry {

	public RecomputeCsumMenuEntry() {
		super("csumfix", "Recompute all checksums");
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		frag.unsetAllChecksums();
	}
}
