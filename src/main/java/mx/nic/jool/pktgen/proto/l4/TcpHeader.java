package mx.nic.jool.pktgen.proto.l4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;

public class TcpHeader extends Layer4Header {

	public static final int LENGTH = 20;

	@HeaderField
	private int sourcePort = 2000;
	@HeaderField
	private int destinationPort = 4000;
	@HeaderField
	private long sequenceNumber = 0;
	@HeaderField
	private long acknowledgmentNumber = 0;
	@HeaderField
	private int dataOffset = LENGTH >> 2;
	@HeaderField
	private int reserved = 0;
	@HeaderField
	private boolean ns = false;
	@HeaderField
	private boolean cwr = false;
	@HeaderField
	private boolean ece = false;
	@HeaderField
	private boolean urg = false;
	@HeaderField
	private boolean ack = false;
	@HeaderField
	private boolean psh = false;
	@HeaderField
	private boolean rst = false;
	@HeaderField
	private boolean syn = true;
	@HeaderField
	private boolean fin = false;
	@HeaderField
	private int windowSize = 100;
	@HeaderField
	private Integer checksum = null;
	@HeaderField
	private int urgentPointer = 0;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		sourcePort = scanner.readInt("Source Port");
		destinationPort = scanner.readInt("Destination Port");
		sequenceNumber = scanner.readLong("Sequence Number", 0);
		acknowledgmentNumber = scanner.readLong("Acknowledgment Number", 0);
		dataOffset = scanner.readInt("Data Offset", LENGTH >> 2);
		reserved = scanner.readInt("Reserved", 0);
		ns = scanner.readBoolean("NS", false);
		cwr = scanner.readBoolean("CWR", false);
		ece = scanner.readBoolean("ECE", false);
		urg = scanner.readBoolean("URG", false);
		ack = scanner.readBoolean("ACK", false);
		psh = scanner.readBoolean("PSH", false);
		rst = scanner.readBoolean("RST", false);
		syn = scanner.readBoolean("SYN", false);
		fin = scanner.readBoolean("FIN", false);
		windowSize = scanner.readInt("Window Size", 100);
		checksum = scanner.readInteger("Checksum");
		urgentPointer = scanner.readInt("Urgent Pointer", 0);
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (checksum == null) {
			checksum = buildChecksum(packet, fragment, true);
		}
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write16BitInt(out, sourcePort);
		PacketUtils.write16BitInt(out, destinationPort);
		PacketUtils.write32BitInt(out, sequenceNumber);
		PacketUtils.write32BitInt(out, acknowledgmentNumber);
		PacketUtils.write8BitInt(out, (dataOffset << 4) | (reserved << 1) | (ns ? 1 : 0));
		PacketUtils.write8BitInt(out,
				(cwr ? (1 << 7) : 0) //
						| (ece ? (1 << 6) : 0) //
						| (urg ? (1 << 5) : 0) //
						| (ack ? (1 << 4) : 0) //
						| (psh ? (1 << 3) : 0) //
						| (rst ? (1 << 2) : 0) //
						| (syn ? (1 << 1) : 0) //
						| (fin ? 1 : 0));
		PacketUtils.write16BitInt(out, windowSize);
		PacketUtils.write16BitInt(out, checksum);
		PacketUtils.write16BitInt(out, urgentPointer);
		return out.toByteArray();
	}

	@Override
	public PacketContent createClone() {
		TcpHeader result = new TcpHeader();

		result.sourcePort = sourcePort;
		result.destinationPort = destinationPort;
		result.sequenceNumber = sequenceNumber;
		result.acknowledgmentNumber = acknowledgmentNumber;
		result.dataOffset = dataOffset;
		result.reserved = reserved;
		result.ns = ns;
		result.cwr = cwr;
		result.ece = ece;
		result.urg = urg;
		result.ack = ack;
		result.psh = psh;
		result.rst = rst;
		result.syn = syn;
		result.fin = fin;
		result.windowSize = windowSize;
		result.checksum = checksum;
		result.urgentPointer = urgentPointer;

		return result;
	}

	@Override
	public String getShortName() {
		return "tcp";
	}

	@Override
	public int getHdrIndex() {
		return 6;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);

		sourcePort = Util.joinBytes(header, 0, 1);
		destinationPort = Util.joinBytes(header, 2, 3);
		sequenceNumber = Util.joinBytes(header[4], header[5], header[6], header[7]);
		acknowledgmentNumber = Util.joinBytes(header[8], header[9], header[10], header[11]);
		dataOffset = header[12] >> 4;
		reserved = (header[12] >> 1) & 0x7;
		ns = (header[12] & 0x1) == 1;
		cwr = ((header[13] >> 7) & 0x1) == 1;
		ece = ((header[13] >> 6) & 0x1) == 1;
		urg = ((header[13] >> 5) & 0x1) == 1;
		ack = ((header[13] >> 4) & 0x1) == 1;
		psh = ((header[13] >> 3) & 0x1) == 1;
		rst = ((header[13] >> 2) & 0x1) == 1;
		syn = ((header[13] >> 1) & 0x1) == 1;
		fin = ((header[13] >> 0) & 0x1) == 1;
		windowSize = Util.joinBytes(header, 14, 15);
		checksum = Util.joinBytes(header, 16, 17);
		urgentPointer = Util.joinBytes(header, 18, 19);

		return new Payload();
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		sourcePort = random.nextInt(0x10000);
		destinationPort = random.nextInt(0x10000);
		sequenceNumber = random.nextLong(0x100000000L);
		acknowledgmentNumber = random.nextLong(0x100000000L);
		// dataOffset = LENGTH >> 2; // TODO
		reserved = (random.nextInt(10) > 0) ? random.nextInt(8) : 0;
		ns = random.nextBoolean();
		cwr = random.nextBoolean();
		ece = random.nextBoolean();
		urg = random.nextBoolean();
		ack = random.nextBoolean();
		psh = random.nextBoolean();
		rst = random.nextBoolean();
		syn = random.nextBoolean();
		fin = random.nextBoolean();
		windowSize = random.nextInt(0x10000);
		checksum = null;
		urgentPointer = random.nextInt(0x10000);
	}

	@Override
	public void unsetChecksum() {
		this.checksum = null;
	}

	@Override
	public void unsetLengths() {
		// TODO not sure if dataOffset counts.
	}
}
