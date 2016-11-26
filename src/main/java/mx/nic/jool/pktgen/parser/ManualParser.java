package mx.nic.jool.pktgen.parser;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class ManualParser implements Parser {

	@Override
	public void handleContent(PacketContent nextContent, FieldScanner scanner) {
		nextContent.readFromStdIn(scanner);
	}

}
