package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;

/**
 * A menu option that updates a specific header already initialized and stored
 * in a packet.
 */
public class ExistingHeaderMenuEntry extends MainMenuEntry {

	/** The header we're editing. */
	private Header header;

	public ExistingHeaderMenuEntry(Header header) {
		super(null, null);
		this.header = header;
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		scanner.scan(header);
	}
}
