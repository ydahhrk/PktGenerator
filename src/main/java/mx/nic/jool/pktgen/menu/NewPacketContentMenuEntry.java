package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.PacketContent;

/**
 * A menu option that appends a new header to the end of a packet.
 */
public class NewPacketContentMenuEntry extends MainMenuEntry {

	/** The new header will look like this by default. */
	private PacketContent template;

	public NewPacketContentMenuEntry(PacketContent template) {
		super(template.getShortName(), template.getClass().getSimpleName());
		this.template = template;
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		PacketContent newContent = template.createClone();
		parser.buildPacketContentOutOfInput(newContent, scanner);
		frag.add(newContent);
	}

}
