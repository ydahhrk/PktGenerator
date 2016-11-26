package mx.nic.jool.pktgen.parser;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.PacketContent;

public interface Parser {

	void handleContent(PacketContent nextContent, FieldScanner scanner);

}
