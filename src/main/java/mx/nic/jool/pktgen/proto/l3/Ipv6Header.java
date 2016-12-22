package mx.nic.jool.pktgen.proto.l3;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.PacketContentFactory;

public class Ipv6Header extends Layer3Header {

	private static final Inet6Address DEFAULT_SRC;
	private static final Inet6Address DEFAULT_DST;

	static {
		try {
			Properties properties = new Properties();
			FileInputStream fis = new FileInputStream("address.properties");
			try {
				properties.load(fis);
			} finally {
				fis.close();
			}
			DEFAULT_SRC = (Inet6Address) InetAddress.getByName(properties.getProperty("ipv6.source"));
			DEFAULT_DST = (Inet6Address) InetAddress.getByName(properties.getProperty("ipv6.destination"));
		} catch (IOException e) {
			throw new IllegalArgumentException("There's something wrong with address.properties.");
		}
	}

	public static final int LENGTH = 40;

	@HeaderField
	private int version = 6;
	@HeaderField
	private int trafficClass = 0;
	@HeaderField
	private int flowLabel = 0;
	@HeaderField
	private Integer payloadLength = null;
	@HeaderField
	private Integer nextHeader = null;
	@HeaderField
	private int hopLimit = 64;
	@HeaderField
	private Inet6Address source;
	@HeaderField
	private Inet6Address destination;

	public Ipv6Header() {
		source = DEFAULT_SRC;
		destination = DEFAULT_DST;
	}

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		version = scanner.readInt("Version", 6);
		trafficClass = scanner.readInt("Traffic Class", 0);
		flowLabel = scanner.readInt("Flow Label", 0);
		payloadLength = scanner.readInteger("Payload Length");
		nextHeader = scanner.readProtocol("Next Header");
		hopLimit = scanner.readInt("Hop Limit", 64);
		source = scanner.readAddress6("Source Address");
		destination = scanner.readAddress6("Destination Address");
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
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
		PacketUtils.write8BitInt(out, ((trafficClass & 0xFF) << 4) | (flowLabel >> 16));
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
	public byte[] getPseudoHeader(int payloadLength, int nextHdr) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(source.getAddress());
		out.write(destination.getAddress());
		PacketUtils.write32BitInt(out, new Long(payloadLength));
		PacketUtils.write16BitInt(out, 0);
		PacketUtils.write8BitInt(out, 0);
		PacketUtils.write8BitInt(out, nextHdr);

		return out.toByteArray();
	}

	@Override
	public String getShortName() {
		return "v6";
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
	public int getHdrIndex() {
		return -6;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);

		version = header[0] >> 4;
		trafficClass = ((header[0] & 0xF) << 4) | (header[1] >> 4);
		flowLabel = Util.joinBytes(header[1] & 0xF, header[2], header[3]);
		payloadLength = Util.joinBytes(header, 4, 5);
		nextHeader = header[6];
		hopLimit = header[7];
		source = loadAddress(header, 8);
		destination = loadAddress(header, 24);

		return PacketContentFactory.forNexthdr(nextHeader);
	}

	private Inet6Address loadAddress(int[] bytes, int offset) throws UnknownHostException {
		return (Inet6Address) Inet6Address.getByAddress(new byte[] { //
				(byte) bytes[offset], //
				(byte) bytes[offset + 1], //
				(byte) bytes[offset + 2], //
				(byte) bytes[offset + 3], //
				(byte) bytes[offset + 4], //
				(byte) bytes[offset + 5], //
				(byte) bytes[offset + 6], //
				(byte) bytes[offset + 7], //
				(byte) bytes[offset + 8], //
				(byte) bytes[offset + 9], //
				(byte) bytes[offset + 10], //
				(byte) bytes[offset + 11], //
				(byte) bytes[offset + 12], //
				(byte) bytes[offset + 13], //
				(byte) bytes[offset + 14], //
				(byte) bytes[offset + 15], //
		});
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		// version = 6;
		trafficClass = random.nextInt(0x100);
		flowLabel = random.nextInt(0x100000);
		// payloadLength = null;
		// nextHeader = null;
		hopLimit = random.nextInt(0x100);
		// source; TODO
		// destination;
	}
}
