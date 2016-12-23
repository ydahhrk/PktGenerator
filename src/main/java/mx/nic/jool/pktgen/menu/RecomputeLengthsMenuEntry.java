package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

public class RecomputeLengthsMenuEntry extends MenuEntry {

	protected RecomputeLengthsMenuEntry() {
		super("lengthfix", "Recompute all lengths");
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		frag.unsetAllLengths();
	}

}
