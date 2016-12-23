package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class ExistingPacketContentMenuEntry extends MenuEntry {

	private PacketContent content;

	public ExistingPacketContentMenuEntry(PacketContent content) {
		super(null, null);
		this.content = content;
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		parser.buildPacketContentOutOfInput(content, scanner);
	}
}
