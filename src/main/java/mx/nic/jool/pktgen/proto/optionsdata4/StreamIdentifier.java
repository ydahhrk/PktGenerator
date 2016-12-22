package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class StreamIdentifier extends Ipv4OptionHeader {

	private static final int DEFAULT_LENGTH = 4;

	@HeaderField
	private int optionType = 136;
	@HeaderField
	private int length = 4;
	@HeaderField
	private int streamID;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		optionType = 136; // scanner.readInt("option Type", 136);
		length = DEFAULT_LENGTH; // scanner.readInt("Length", 4);
		streamID = scanner.readInt("Stream ID", 000);

	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, optionType);
		PacketUtils.write8BitInt(out, length);
		PacketUtils.write16BitInt(out, streamID);

		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		StreamIdentifier result = new StreamIdentifier();

		result.optionType = optionType;
		result.length = length;
		result.streamID = streamID;

		return result;
	}

	@Override
	public String getShortName() {
		return "siopt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		// no code

	}

	@Override
	public void randomize() {
		throw new IllegalArgumentException("Sorry; StreamIdentifiers are not supported in random mode yet.");
	}
}
