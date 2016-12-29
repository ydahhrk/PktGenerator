package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

/**
 * A menu option that unsets all lenghts in a packet so they can be
 * automatically recomputed later.
 */
public class RecomputeLengthsMenuEntry extends MainMenuEntry {

	protected RecomputeLengthsMenuEntry() {
		super("lenfix", "Recompute all lengths");
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		frag.unsetAllLengths();
	}

}
