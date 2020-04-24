package mx.nic.jool.pktgen.pojo.shortcut;

import java.util.Arrays;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Payload;

public class TruncatePayloadShortcut implements Shortcut {

	@Override
	public String getName() {
		return "truncate";
	}

	@Override
	public void apply(Header header, String value) {
		if (!(header instanceof Payload))
			throw new IllegalArgumentException("Header is not Payload. Don't know what to do.");
		Payload payload = (Payload) header;

		int newLength = Integer.valueOf(value);
		payload.setBytes(Arrays.copyOfRange(payload.getBytes(), 0, newLength));
	}

}
