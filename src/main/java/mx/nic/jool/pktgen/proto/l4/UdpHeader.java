package mx.nic.jool.pktgen.proto.l4;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;

public class UdpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	@HeaderField
	private int sourcePort = 2000;
	@HeaderField
	private int destinationPort = 4000;
	@HeaderField
	private Integer length = null;
	@HeaderField
	private Integer checksum = null;

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (length == null) {
			length = LENGTH;
			for (PacketContent content : packet.getL4ContentAfter(this)) {
				length += content.toWire().length;
			}
		}

		if (checksum == null) {
			checksum = buildChecksum(packet, fragment, true);
		}
	}

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write16BitInt(out, sourcePort);
		PacketUtils.write16BitInt(out, destinationPort);
		PacketUtils.write16BitInt(out, length);
		PacketUtils.write16BitInt(out, checksum);

		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		UdpHeader result = new UdpHeader();

		result.sourcePort = sourcePort;
		result.destinationPort = destinationPort;
		result.length = length;
		result.checksum = checksum;

		return result;
	}

	@Override
	public String getShortName() {
		return "udp";
	}

	public void swapPorts() {
		int tmp = sourcePort;
		sourcePort = destinationPort;
		destinationPort = tmp;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getChecksum() {
		return checksum;
	}

	public void setChecksum(Integer checksum) {
		this.checksum = checksum;
	}

	@Override
	public int getHdrIndex() {
		return 17;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		int[] header = Util.streamToIntArray(in, LENGTH);

		sourcePort = Util.joinBytes(header, 0, 1);
		destinationPort = Util.joinBytes(header, 2, 3);
		length = Util.joinBytes(header, 4, 5);
		checksum = Util.joinBytes(header, 6, 7);

		return new Payload();
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		sourcePort = random.nextInt(0x10000);
		destinationPort = random.nextInt(0x10000);
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
}
