package mx.nic.jool.pktgen.proto.l4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class UdpHeader extends Layer4Header {

	public static final int LENGTH = 8;

	private int sourcePort = 2000;
	private int destinationPort = 4000;
	private Integer length = null;
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
	public Protocol getProtocol() {
		return Protocol.UDP;
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
	
}
