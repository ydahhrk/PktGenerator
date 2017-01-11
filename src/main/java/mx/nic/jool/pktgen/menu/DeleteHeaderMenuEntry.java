package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;

/**
 * A menu option that discards a specific header already initialized and stored
 * in a packet.
 */
public class DeleteHeaderMenuEntry extends MainMenuEntry {

	protected DeleteHeaderMenuEntry() {
		super("rm", "Remove header");
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		int headerIndex = scanner.readInt("Header index");
		if (0 <= headerIndex && headerIndex < frag.size()) {
			Header removed = frag.remove(headerIndex);
			System.out.println("Deleted a " + removed.getClass().getSimpleName() + ".\n");
			return;
		}

		System.err.println("That's not a header index. Try again maybe.\n");
	}

}
