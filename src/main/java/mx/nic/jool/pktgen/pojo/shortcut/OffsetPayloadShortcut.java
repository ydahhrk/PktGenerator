package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class OffsetPayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "offset";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;
		int offset = Integer.valueOf(value);

		byte[] bytes = payload.getBytes();
		for (int x = 0; x < bytes.length; x++)
			bytes[x] = (byte) ((x + offset) & 0xFF);
	}

}
