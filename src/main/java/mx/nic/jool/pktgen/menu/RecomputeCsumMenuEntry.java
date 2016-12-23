package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

public class RecomputeCsumMenuEntry extends MenuEntry {

	public RecomputeCsumMenuEntry() {
		super("csumfix", "Recompute all checksums");
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		frag.unsetAllChecksums();
	}
}
