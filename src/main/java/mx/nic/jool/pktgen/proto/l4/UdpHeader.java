package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.SwapIdentifiersShortcut;

/**
 * https://tools.ietf.org/html/rfc768
 */
public class UdpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	@HeaderField
	private int src = 2000;
	@HeaderField
	private int dst = 4000;
	@HeaderField
	private Integer length = null;
	@HeaderField
	private Integer checksum = null;

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (length == null) {
			length = LENGTH;
			for (Header header : packet.getUpperLayerHeadersAfter(this)) {
				length += header.toWire().length;
			}
		}

		if (checksum == null) {
			checksum = buildChecksum(packet, fragment, true);
		}
	}

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write16BitInt(out, src);
		PacketUtils.write16BitInt(out, dst);
		PacketUtils.write16BitInt(out, length);
		PacketUtils.write16BitInt(out, checksum);

		return out.toByteArray();
	}

	@Override
	public Header createClone() {
		UdpHeader result = new UdpHeader();

		result.src = src;
		result.dst = dst;
		result.length = length;
		result.checksum = checksum;

		return result;
	}

	@Override
	public String getShortName() {
		return "udp";
	}

	@Override
	public String getName() {
		return "UDP Header";
	}

	@Override
	public int getHdrIndex() {
		return 17;
	}

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, LENGTH);

		src = PacketUtils.joinBytes(header, 0, 1);
		dst = PacketUtils.joinBytes(header, 2, 3);
		length = PacketUtils.joinBytes(header, 4, 5);
		checksum = PacketUtils.joinBytes(header, 6, 7);

		return new Payload();
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		src = random.nextInt(0x10000);
		dst = random.nextInt(0x10000);
		length = null;
		checksum = null;
	}

	@Override
	public void unsetChecksum() {
		this.checksum = null;
	}

	@Override
	public void unsetLengths() {
		this.length = null;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { new SwapIdentifiersShortcut() };
	}

	@Override
	public void swapIdentifiers() {
		int tmp = src;
		src = dst;
		dst = tmp;
	}
}
