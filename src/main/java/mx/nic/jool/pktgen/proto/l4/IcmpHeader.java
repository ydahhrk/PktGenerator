package mx.nic.jool.pktgen.proto.l4;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.PacketContent;

/**
 * Yes, layer 4. It is layer 4 for most intents and purposes. Shut up.
 */
public abstract class IcmpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	@Readable(defaultValue = "8", type = Type.INT)
	protected int type;
	@Readable(defaultValue = "0", type = Type.INT)
	protected int code;
	@Readable(defaultValue = "auto", type = Type.INTEGER)
	protected Integer checksum = null;
	@Readable(defaultValue = "0", type = Type.INT)
	protected int restOfHeader1 = 0;
	@Readable(defaultValue = "0", type = Type.INT)
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

	private void printValues() {
		System.out.println("type: " + type);
		System.out.println("code: " + code);
		System.out.println("checksum: " + (checksum != null ? checksum : "(auto)"));
		System.out.println("restOfHeader1: " + restOfHeader1);
		System.out.println("restOfHeader2: " + restOfHeader2);
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		String fieldToModify;
		do {
			printValues();

			fieldToModify = scanner.readLine("Field to edit (case sensitive)", "exit");
			if (fieldToModify.equalsIgnoreCase("exit"))
				break;

			if (fieldToModify == null || fieldToModify.isEmpty())
				continue;

			switch (fieldToModify) {
			case "type":
				type = scanner.readInt("Type", 8);
				break;
			case "code":
				code = scanner.readInt("Code", 0);
				break;
			case "checksum":
				checksum = scanner.readInteger("Checksum", "auto");
				break;
			case "restOfHeader1":
				restOfHeader1 = scanner.readInt("Unused [higher 16 bits]", 0);
				break;
			case "restOfHeader2":
				restOfHeader2 = scanner.readInt("Unused [lower 16 bits]", 0);
				break;
			default:
				System.out.println(fieldToModify + " is not the name of a header field.");
				break;
			}

		} while (true);
	}

	@Override
	public PacketContent loadFromStream(FileInputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);

		type = header[0];
		code = header[1];
		checksum = Util.joinBytes(header[2], header[3]);
		restOfHeader1 = Util.joinBytes(header[4], header[5]);
		restOfHeader2 = Util.joinBytes(header[6], header[7]);

		return getNextContent();
	}

	protected abstract PacketContent getNextContent();

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		type = random.nextInt(0x100);
		code = random.nextInt(0x100);
		// checksum = null;
		restOfHeader1 = random.nextInt(0x10000);
		restOfHeader2 = random.nextInt(0x10000);
	}
}
