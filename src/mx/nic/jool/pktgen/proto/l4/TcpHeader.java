package mx.nic.jool.pktgen.proto.l4;

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

public class TcpHeader extends Layer4Header {

	public static final int LENGTH = 20;

	@Readable(defaultValue = "2000", type = Type.INT)
	private int sourcePort = 2000;
	
	@Readable(defaultValue = "4000", type = Type.INT)
	private int destinationPort = 4000;
	
	@Readable(defaultValue = "0", type = Type.LONG)
	private long sequenceNumber = 0;
	
	@Readable(defaultValue = "0", type = Type.LONG)
	private long acknowledgmentNumber = 0;
	
	@Readable(defaultValue = "5", type = Type.INT)
	private int dataOffset = LENGTH >> 2;
	
	@Readable(defaultValue = "0", type = Type.INT)
	private int reserved = 0;
	
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean ns = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean cwr = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean ece = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean urg = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean ack = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean psh = false;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean rst = false;
	@Readable(defaultValue = "true", type = Type.BOOLEAN)
	private boolean syn = true;
	@Readable(defaultValue = "false", type = Type.BOOLEAN)
	private boolean fin = false;
	
	@Readable(defaultValue = "100", type = Type.INT)
	private int windowSize = 100;
	
	@Readable(defaultValue = "auto", type = Type.INTEGER)
	private Integer checksum = null;
	
	@Readable(defaultValue = "0", type = Type.INT)
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
		checksum = scanner.readInteger("Checksum", "auto");
		urgentPointer = scanner.readInt("Urgent Pointer", 0);
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
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
		PacketUtils.write8BitInt(out, (dataOffset << 4) | (reserved << 1)
				| (ns ? 1 : 0));
		PacketUtils.write8BitInt(out, (cwr ? (1 << 7) : 0)
				| (ece ? (1 << 6) : 0) | (urg ? (1 << 5) : 0)
				| (ack ? (1 << 4) : 0) | (psh ? (1 << 3) : 0)
				| (rst ? (1 << 2) : 0) | (syn ? (1 << 1) : 0) | (fin ? 1 : 0));
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
	public Protocol getProtocol() {
		return Protocol.TCP;
	}

	@Override
	public String getShortName() {
		return "tcp";
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}

}
