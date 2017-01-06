package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.PacketContent;

/**
 * A menu option that updates a specific header already initialized and stored
 * in a packet.
 */
public class ExistingPacketContentMenuEntry extends MainMenuEntry {

	/** The header we're editing. */
	private PacketContent content;

	public ExistingPacketContentMenuEntry(PacketContent content) {
		super(null, null);
		this.content = content;
	}

	@Override
	public void execute(FieldScanner scanner, Fragment frag) {
		scanner.scan(content);
	}
}
