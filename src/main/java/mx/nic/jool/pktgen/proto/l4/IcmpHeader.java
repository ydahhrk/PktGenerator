package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.IcmpLengthShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;

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
	protected int rest1 = 0;
	@HeaderField
	protected int rest2 = 0;

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, type);
		PacketUtils.write8BitInt(out, code);
		PacketUtils.write16BitInt(out, checksum);
		PacketUtils.write16BitInt(out, rest1);
		PacketUtils.write16BitInt(out, rest2);

		return out.toByteArray();
	}

	protected IcmpHeader createCloneIcmp(IcmpHeader result) {
		result.type = type;
		result.code = code;
		result.checksum = checksum;
		result.rest1 = rest1;
		result.rest2 = rest2;
		return result;
	}

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, LENGTH);

		type = header[0];
		code = header[1];
		checksum = PacketUtils.joinBytes(header[2], header[3]);
		rest1 = PacketUtils.joinBytes(header[4], header[5]);
		rest2 = PacketUtils.joinBytes(header[6], header[7]);

		return getNextHeader();
	}

	protected abstract Header getNextHeader();

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		Integer[][] availableTypes = getAvailableTypes();
		int chosen = random.nextInt(availableTypes.length);
		this.type = availableTypes[chosen][0];
		if (availableTypes[chosen][1] != null)
			this.code = availableTypes[chosen][1];

		// checksum = null;
		rest1 = random.nextInt(0x10000);
		rest2 = random.nextInt(0x10000);
	}

	protected abstract Integer[][] getAvailableTypes();

	@Override
	public void unsetChecksum() {
		this.checksum = null;
	}

	@Override
	public void unsetLengths() {
		// No lengths.
	}

	@Override
	public void swapIdentifiers() {
		// No IDs.
	}

}
