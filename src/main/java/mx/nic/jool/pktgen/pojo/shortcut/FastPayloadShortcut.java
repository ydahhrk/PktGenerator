package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class FastPayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "fast";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;

		int length = Integer.valueOf(value);
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++)
			bytes[i] = (byte) (i & 0xFF);
		payload.setBytes(bytes);
	}

}
