package mx.nic.jool.pktgen.proto.l3;

import java.io.IOException;

import mx.nic.jool.pktgen.enums.Layer;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

/**
 * A header from the {@link Layer#INTERNET} layer, except ICMP.
 */
public abstract class Layer3Header implements Header {

	@Override
	public Layer getLayer() {
		return Layer.INTERNET;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return null;
	}

	public abstract byte[] getPseudoHeader(int payloadLength, int nexthdr) throws IOException;

}
