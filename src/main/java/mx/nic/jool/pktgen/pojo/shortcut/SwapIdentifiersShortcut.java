package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Header;

public class SwapIdentifiersShortcut implements Shortcut {

	@Override
	public String getName() {
		return "swap";
	}

	@Override
	public void apply(Header header, FieldScanner scanner) {
		header.swapIdentifiers();
	}

}
