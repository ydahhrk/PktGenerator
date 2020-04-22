package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class FilePayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public void apply(Header header, FieldScanner scanner) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;

		byte[] bytes = scanner.readBytesFromFile();
		payload.setBytes(bytes);
	}

}
