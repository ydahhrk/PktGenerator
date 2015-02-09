package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class EndOptionList implements Ipv4OptionHeader {

	private final int optionType = 0;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		// No code, option Type = 0;
	}
	
	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);

		return out.toByteArray();
	}
	
	@Override
	public PacketContent createClone() {
		return new EndOptionList();
	}

	@Override
	public Protocol getProtocol() {
		// No code
		return null;
	}

	@Override
	public String getShortName() {
		return "eopt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		// No code
	}

}
