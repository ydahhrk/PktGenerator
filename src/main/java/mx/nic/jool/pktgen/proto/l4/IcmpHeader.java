package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.pojo.PacketContent;

/**
 * Yes, layer 4. It is layer 4 for most intents and purposes. Shut up.
 */
public abstract class IcmpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	@HeaderField
	protected int type;
	@HeaderField
	protected int code;
	@HeaderField
	protected Integer checksum = null;
	@HeaderField
	protected int restOfHeader1 = 0;
	@HeaderField
	protected int restOfHeader2 = 0;

	@Override
	public byte[] toWire() {
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

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		int[] header = Util.streamToIntArray(in, LENGTH);

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

	@Override
	public void unsetChecksum() {
		this.checksum = null;
	}

	@Override
	public void unsetLengths() {
		// No lengths.
	}
}
