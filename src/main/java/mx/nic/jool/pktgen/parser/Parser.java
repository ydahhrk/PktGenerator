package mx.nic.jool.pktgen.parser;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.PacketContent;

/**
 * Builds {@link PacketContent}s out of user input.
 */
public interface Parser {

	void buildPacketContentOutOfInput(PacketContent nextContent, FieldScanner scanner);

}
