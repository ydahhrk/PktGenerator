package mx.nic.jool.pktgen.proto.l4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;


public abstract class IcmpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	protected int type;
	protected int code;
	protected Integer checksum = null;
	protected int restOfHeader1 = 0;
	protected int restOfHeader2 = 0;
	
	@Override
	public void readFromStdIn(FieldScanner scanner) {
		type = scanner.readInt("Type", 3);
		code = scanner.readInt("Code", 0);
		checksum = scanner.readInteger("Checksum", "auto");
		restOfHeader1 = scanner.readInt("Unused [higher 16 bits]", 0);
		restOfHeader2 = scanner.readInt("Unused [lower 16 bits]", 0);
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, type);
		PacketUtils.write8BitInt(out, code);
		PacketUtils.write16BitInt(out, checksum);
		PacketUtils.write16BitInt(out, restOfHeader1);
		PacketUtils.write16BitInt(out, restOfHeader2);

		return out.toByteArray();
	}
	
	protected IcmpHeader createCloneIcmp(IcmpHeader result) {
		result.type = type;
		result.code = code;
		result.checksum = checksum;
		result.restOfHeader1 = restOfHeader1;
		result.restOfHeader2 = restOfHeader2;
		return result;
	}
	
}
