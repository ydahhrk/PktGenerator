package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class PaddingPayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "padding";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;

		int length = Integer.valueOf(value);
		payload.setBytes(new byte[length]);
	}

}
