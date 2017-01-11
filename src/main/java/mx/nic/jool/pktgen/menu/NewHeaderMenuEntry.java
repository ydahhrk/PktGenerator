package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;

/**
 * A menu option that appends a new header to the end of a packet.
 */
public class NewHeaderMenuEntry extends MainMenuEntry {

	/** The new header will look like this by default. */
	private Header template;

	public NewHeaderMenuEntry(Header template) {
		super(template.getShortName(), template.getClass().getSimpleName());
		this.template = template;
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		Header newHeader = template.createClone();
		scanner.scan(newHeader);
		frag.add(newHeader);
	}

}
