package mx.nic.jool.pktgen.proto.l3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class Ipv6Header implements Layer3Header {

	public static Inet6Address DEFAULT_REMOTE;
	public static Inet6Address DEFAULT_LOCAL;

	private static void setDefaults(String remote, String local) {
		try {
			DEFAULT_REMOTE = (Inet6Address) InetAddress.getByName(remote);
			DEFAULT_LOCAL = (Inet6Address) InetAddress.getByName(local);
		} catch (UnknownHostException e) {
			// It's hardcoded so this is unexpected.
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void stateless() {
		setDefaults("2001:db8:1c0:2:21::", "2001:db8:1c6:3364:2::");
	}
	
	public static void stateful() {
		setDefaults("2001:db8::5", "64:ff9b::192.0.2.5");
	}
	
	static {
		stateful();
	}
	
	public static final int LENGTH = 40;

	@Readable(defaultValue = "6", type = Type.INT)
	private int version = 6;
	@Readable(defaultValue = "0", type = Type.INT)
	private int trafficClass = 0;
	@Readable(defaultValue = "0", type = Type.INT)
	private int flowLabel = 0;

	@Readable(defaultValue = "auto", type = Type.INTEGER)
	private Integer payloadLength = null;
	@Readable(defaultValue = "auto", type = Type.INTEGER)
	private Integer nextHeader = null;
	@Readable(defaultValue = "64", type = Type.INT)
	private int hopLimit = 64;

	@Readable(defaultValue = "", type = Type.INET6ADDRESS)
	private Inet6Address source;
	@Readable(defaultValue = "", type = Type.INET6ADDRESS)
	private Inet6Address destination;

	public Ipv6Header() {
		source = DEFAULT_REMOTE;
		destination = DEFAULT_LOCAL;
	}

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		version = scanner.readInt("Version", 6);
		trafficClass = scanner.readInt("Traffic Class", 0);
		flowLabel = scanner.readInt("Flow Label", 0);
		payloadLength = scanner.readInteger("Payload Length", "auto");
		nextHeader = scanner.readProtocol("Next Header", "auto");
		hopLimit = scanner.readInt("Hop Limit", 64);
		source = scanner.readAddress6("Source Address");
		destination = scanner.readAddress6("Destination Address");
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		if (payloadLength == null) {
			payloadLength = 0;
			for (PacketContent content : fragment.sliceExclusive(this)) {
				payloadLength += content.toWire().length;
			}
		}

		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, (version << 4) | (trafficClass >> 4));
		PacketUtils.write8BitInt(out, ((trafficClass & 0xFF) << 4)
				| (flowLabel >> 16));
		PacketUtils.write16BitInt(out, flowLabel & 0xFFFF);
		PacketUtils.write16BitInt(out, payloadLength);
		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hopLimit);
		out.write(source.getAddress());
		out.write(destination.getAddress());

		return out.toByteArray();
	}
	
	@Override
	public PacketContent createClone() {
		Ipv6Header result = new Ipv6Header();
		
		result.version = version;
		result.trafficClass = trafficClass;
		result.flowLabel = flowLabel;
		result.payloadLength = payloadLength;
		result.nextHeader = nextHeader;
		result.hopLimit = hopLimit;
		result.source = source;
		result.destination = destination;
		
		return result;
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, Protocol nextProtocol)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(source.getAddress());
		out.write(destination.getAddress());
		PacketUtils.write32BitInt(out, new Long(payloadLength));
		PacketUtils.write16BitInt(out, 0);
		PacketUtils.write8BitInt(out, 0);
		PacketUtils.write8BitInt(out, nextProtocol.toWire());

		return out.toByteArray();
	}

	@Override
	public Protocol getProtocol() {
		return Protocol.IPV6;
	}

	@Override
	public String getShortName() {
		return "6";
	}

	public void setPayloadLength(Integer payloadLength) {
		this.payloadLength = payloadLength;
	}
	
	public void setHopLimit(int hopLimit) {
		this.hopLimit = hopLimit;
	}

	public void swapAddresses() {
		Inet6Address tmp = source;
		source = destination;
		destination = tmp;
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}
	
}
