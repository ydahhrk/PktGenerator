package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class NewPacketContentMenuEntry extends MenuEntry {

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
