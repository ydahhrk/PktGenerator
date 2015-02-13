package mx.nic.jool.pktgen.proto.optionsdata4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class StreamIdentifier implements Ipv4OptionHeader {

	private static final int DEFAULT_LENGTH = 4;

	@Readable(defaultValue = "136", type = Type.INT)
	private int optionType;
	@Readable(defaultValue = "4", type = Type.INT)
	private int length;
	@Readable(defaultValue = "000", type = Type.INT)
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
	public Protocol getProtocol() {
		// no code
		return null;
	}

	@Override
	public String getShortName() {
		return "siopt";
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		// no code

	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}

}
