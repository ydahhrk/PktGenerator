package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class NoOperation extends Ipv4OptionHeader {

	private final int optionType = 1;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		// No code, option Type = 1;
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);

		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		return new NoOperation();
	}

	@Override
	public String getShortName() {
		return "nopt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		// No code
	}

	@Override
	public void modifyFromStdIn(FieldScanner scanner) {
		// No code, option Type = 1;
	}

	@Override
	public void randomize() {
		// No code
	}
}
