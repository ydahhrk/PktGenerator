package mx.nic.jool.pktgen.parser;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class AutoParser implements Parser {

	@Override
	public void buildPacketContentOutOfInput(PacketContent nextContent, FieldScanner scanner) {
		nextContent.modifyFromStdIn(scanner);
	}

}
