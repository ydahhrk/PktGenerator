package mx.nic.jool.pktgen.proto.l3;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ChecksumStatus;
import mx.nic.jool.pktgen.CsumBuilder;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.enums.Type;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.PacketContentFactory;
import mx.nic.jool.pktgen.proto.optionsdata4.Ipv4OptionHeader;

public class Ipv4Header extends Layer3Header {

	private static final Inet4Address DEFAULT_SRC;
	private static final Inet4Address DEFAULT_DST;

	static {
		try {
			Properties properties = new Properties();
			FileInputStream fis = new FileInputStream("address.properties");
			try {
				properties.load(fis);
			} finally {
				fis.close();
			}
			DEFAULT_SRC = (Inet4Address) InetAddress.getByName(properties.getProperty("ipv4.source"));
			DEFAULT_DST = (Inet4Address) InetAddress.getByName(properties.getProperty("ipv4.destination"));
		} catch (IOException e) {
			throw new IllegalArgumentException("There's something wrong with address.properties.");
		}
	}
	
	public static final int LENGTH = 20;

	@Readable(defaultValue="4", type=Type.INT)
	private int version = 4;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer ihl = null;
	@Readable(defaultValue="0", type=Type.INT)
	private int tos = 0;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer totalLength = null;
	@Readable(defaultValue="0", type=Type.INT)
	private int identification = 0;
	@Readable(defaultValue="false", type=Type.BOOLEAN)
	private boolean reserved = false;
	@Readable(defaultValue="true", type=Type.BOOLEAN)
	private boolean df = true;
	@Readable(defaultValue="null", type=Type.BOOLEAN)
	private Boolean mf = null;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer fragmentOffset = null;
	@Readable(defaultValue="64", type=Type.INT)
	private int ttl = 64;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer protocol = null;
	@Readable(defaultValue="auto", type=Type.INTEGER)
	private Integer headerChecksum = null;
	@Readable(defaultValue="auto", type=Type.INET4ADDRESS)
	private Inet4Address source;
	@Readable(defaultValue="auto", type=Type.INET4ADDRESS)
	private Inet4Address destination;

	public Ipv4Header() {
		this.source = DEFAULT_SRC;
		this.destination = DEFAULT_DST;
	}

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		version = scanner.readInt("Version", 4);
		ihl = scanner.readInteger("IHL", "auto");
		tos = scanner.readInt("TOS", 0);
		totalLength = scanner.readInteger("Total Length", "auto");
		identification = scanner.readInt("Identification", 0);
		reserved = scanner.readBoolean("Reserved", false);
		df = scanner.readBoolean("DF", true);
		mf = scanner.readBoolean("MF", null);
		fragmentOffset = scanner.readInteger("Fragment Offset (bytes)", "auto");
		ttl = scanner.readInt("TTL", 64);
		protocol = scanner.readProtocol("Protocol", "auto");
		headerChecksum = scanner.readInteger("Checksum", "auto");
		source = scanner.readAddress4("Source Address");
		destination = scanner.readAddress4("Destination Address");
	}

	private int buildChecksum() throws IOException {
		CsumBuilder csum = new CsumBuilder();
		try {
			csum.write(toWire());
			return csum.finish(ChecksumStatus.CORRECT);
		} finally {
			csum.close();
		}
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
		if (ihl == null) {
			int mod;
			int headerLength = LENGTH;

			for (PacketContent content : fragment.sliceExclusive(this)) {
				if (!(content instanceof Ipv4OptionHeader))
					break;
				headerLength += content.toWire().length;
			}
			
			mod = headerLength % 4;
			// TODO add padding?
			ihl = (mod == 0) ? (headerLength >> 2) : ((headerLength >> 2) + 1);
		}

		if (totalLength == null) {
			totalLength = ihl << 2;
			for (PacketContent content : fragment.sliceExclusive(this)) {
				totalLength += content.toWire().length;
			}
		}

		if (mf == null) {
			mf = fragment != packet.get(packet.size() - 1);
		}

		if (fragmentOffset == null) {
			fragmentOffset = 0;
			for (Fragment currentFragment : packet) {
				if (fragment == currentFragment)
					break;
				fragmentOffset += currentFragment.getL3PayloadLength();
			}
		}

		if (protocol == null) {
			protocol = fragment.getNextHdr(packet, this);
		}

		if (headerChecksum == null) {
			headerChecksum = buildChecksum();
		}
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int ihl = (this.ihl != null) ? this.ihl : 0;
		PacketUtils.write8BitInt(out, (version << 4) | ihl);

		PacketUtils.write8BitInt(out, tos);
		PacketUtils.write16BitInt(out, totalLength);
		PacketUtils.write16BitInt(out, identification);

		int fragOffset = (fragmentOffset != null) ? fragmentOffset : 0;
		boolean mFrags = (mf != null) ? mf : false;
		PacketUtils.write16BitInt(out, ((reserved ? 1 : 0) << 15)
				| ((df ? 1 : 0) << 14) | ((mFrags ? 1 : 0) << 13)
				| (fragOffset >> 3));

		PacketUtils.write8BitInt(out, ttl);
		PacketUtils.write8BitInt(out, protocol);
		PacketUtils.write16BitInt(out, headerChecksum);
		out.write(source.getAddress());
		out.write(destination.getAddress());

		return out.toByteArray();
	}
	
	@Override
	public PacketContent createClone() {
		Ipv4Header result = new Ipv4Header();
		
		result.version = version;
		result.ihl = ihl;
		result.tos = tos;
		result.totalLength = totalLength;
		result.identification = identification;
		result.reserved = reserved;
		result.df = df;
		result.mf = mf;
		result.fragmentOffset = fragmentOffset;
		result.ttl = ttl;
		result.protocol = protocol;
		result.headerChecksum = headerChecksum;
		result.source = source;
		result.destination = destination;
		
		return result;
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, int nextHdr) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(source.getAddress());
		out.write(destination.getAddress());
		PacketUtils.write8BitInt(out, 0);
		PacketUtils.write8BitInt(out, nextHdr);
		PacketUtils.write16BitInt(out, payloadLength);
		
		return out.toByteArray();
	}

	@Override
	public String getShortName() {
		return "v4";
	}

	public void setTotalLength(Integer totalLength) {
		this.totalLength = totalLength;
	}
	
	public void setIdentification(int identification) {
		this.identification = identification;
	}

	public void setDf(boolean df) {
		this.df = df;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public void swapAddresses() {
		Inet4Address tmp = source;
		source = destination;
		destination = tmp;
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}

	@Override
	public int getHdrIndex() {
		return -4;
	}

	@Override
	public PacketContent loadFromStream(FileInputStream in) throws IOException {
		int[] header = Util.streamToArray(in, LENGTH);
		
		version = header[0] >> 4;
		ihl = header[0] & 0xF;
		if (ihl != 5)
			throw new IOException("The packet contains IPv4 options. This is not supported by load-from-file mode.");
		tos = header[1];
		totalLength = Util.joinBytes(header, 2, 3);
		identification = Util.joinBytes(header, 4, 5);
		reserved = (header[6] >> 7) == 1;
		df = (header[6] >> 6) == 1;
		mf = (header[6] >> 5) == 1;
		fragmentOffset = Util.joinBytes(header[6] & 0x1F, header[7]);
		ttl = header[8];
		protocol = header[9];
		headerChecksum = Util.joinBytes(header, 10, 11);
		source = loadAddress(header, 12);
		destination = loadAddress(header, 16);

		return PacketContentFactory.forNexthdr(protocol);
	}

	private Inet4Address loadAddress(int[] bytes, int offset) throws UnknownHostException {
		return (Inet4Address) Inet4Address.getByAddress(new byte[] { //
				(byte) bytes[offset], //
				(byte) bytes[offset + 1], //
				(byte) bytes[offset + 2], //
				(byte) bytes[offset + 3], //
		});
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		// version = 4;
		// ihl = null;
		tos = random.nextInt(0x100);
		// totalLength = null;
		identification = random.nextInt(0x10000);
		// reserved = false;
		df = random.nextBoolean();
		mf = random.nextBoolean();
		// fragmentOffset = null;
		ttl = random.nextInt(0x100);
		// protocol = null;
		// headerChecksum = null;
		// source;
		// destination;
	}
}
