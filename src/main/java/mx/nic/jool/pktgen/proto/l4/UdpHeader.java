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
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;

public class UdpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	@Readable(defaultValue="2000", type=Type.INT)
	private int sourcePort = 2000;
	@Readable(defaultValue="2000", type=Type.INT)
	private int destinationPort = 4000;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer length = null;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer checksum = null;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		sourcePort = scanner.readInt("Source Port");
		destinationPort = scanner.readInt("Destination Port");
		length = scanner.readInteger("Length", "auto");
		checksum = scanner.readInteger("Checksum", "auto");
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
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
	public byte[] toWire() throws IOException {
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
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
//		modifyFieldValues(null, scanner);
	}

	@Override
	public int getHdrIndex() {
		return 17;
	}
	
	@Override
	public PacketContent loadFromStream(FileInputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);

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
}
