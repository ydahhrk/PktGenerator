package mx.nic.jool.pktgen.proto.l3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.ChecksumBuilder;
import mx.nic.jool.pktgen.ChecksumStatus;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.proto.HeaderFactory;

/**
 * https://tools.ietf.org/html/rfc791#section-3.1
 */
public class Ipv4Header extends Layer3Header {

	private static final Inet4Address DEFAULT_SRC;
	private static final Inet4Address DEFAULT_DST;

	static {
		try {
			Properties properties = new Properties();
			try (FileInputStream fis = new FileInputStream("address.properties")) {
				properties.load(fis);
			}
			DEFAULT_SRC = (Inet4Address) InetAddress.getByName(properties.getProperty("ipv4.source"));
			DEFAULT_DST = (Inet4Address) InetAddress.getByName(properties.getProperty("ipv4.destination"));
		} catch (IOException e) {
			throw new IllegalArgumentException("There's something wrong with address.properties.");
		}
	}

	public static final int LENGTH = 20;

	@HeaderField
	private int version = 4;
	@HeaderField
	private Integer ihl = null;
	@HeaderField
	private int tos = 0;
	@HeaderField
	private Integer totalLength = null;
	@HeaderField
	private int identification = 0;
	@HeaderField
	private boolean reserved = false;
	@HeaderField
	private boolean df = true;
	@HeaderField
	private Boolean mf = null;
	@HeaderField
	private Integer fragmentOffset = null;
	@HeaderField
	private int ttl = 64;
	@HeaderField
	private Integer protocol = null;
	@HeaderField
	private Integer headerChecksum = null;
	@HeaderField
	private Inet4Address source;
	@HeaderField
	private Inet4Address destination;
	@HeaderField
	private byte[] options;

	public Ipv4Header() {
		this.source = DEFAULT_SRC;
		this.destination = DEFAULT_DST;
	}

	private int buildChecksum() throws IOException {
		try (ChecksumBuilder csum = new ChecksumBuilder()) {
			csum.write(toWire());
			return csum.finish(ChecksumStatus.CORRECT);
		}
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (ihl == null) {
			int headerLength = LENGTH;
			if (options != null)
				headerLength += options.length;

			ihl = headerLength >> 2;
			if ((headerLength & 3) != 0)
				ihl++;
		}

		if (totalLength == null) {
			totalLength = ihl << 2;
			for (Header header : fragment.sliceExclusive(this)) {
				totalLength += header.toWire().length;
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

		if (options != null && ((options.length & 3) != 0)) {
			int fixedLength = options.length + (4 - options.length & 3);
			options = Arrays.copyOf(options, fixedLength);
		}
	}

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int ihl = (this.ihl != null) ? this.ihl : 0;
		PacketUtils.write8BitInt(out, (version << 4) | ihl);

		PacketUtils.write8BitInt(out, tos);
		PacketUtils.write16BitInt(out, totalLength);
		PacketUtils.write16BitInt(out, identification);

		int fragOffset = (fragmentOffset != null) ? fragmentOffset : 0;
		boolean mFrags = (mf != null) ? mf : false;
		PacketUtils.write16BitInt(out,
				((reserved ? 1 : 0) << 15) //
						| ((df ? 1 : 0) << 14) //
						| ((mFrags ? 1 : 0) << 13) //
						| (fragOffset >> 3));

		PacketUtils.write8BitInt(out, ttl);
		PacketUtils.write8BitInt(out, protocol);
		PacketUtils.write16BitInt(out, headerChecksum);
		out.write(source.getAddress());
		out.write(destination.getAddress());

		if (options != null)
			out.write(options);

		return out.toByteArray();
	}

	@Override
	public Header createClone() {
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
		result.options = options;

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

	public void swapAddresses() {
		Inet4Address tmp = source;
		source = destination;
		destination = tmp;
	}

	@Override
	public int getHdrIndex() {
		return -4;
	}

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, LENGTH);

		version = header[0] >> 4;
		ihl = header[0] & 0xF;
		tos = header[1];
		totalLength = PacketUtils.joinBytes(header, 2, 3);
		identification = PacketUtils.joinBytes(header, 4, 5);
		reserved = (header[6] >> 7) == 1;
		df = (header[6] >> 6) == 1;
		mf = (header[6] >> 5) == 1;
		fragmentOffset = PacketUtils.joinBytes(header[6] & 0x1F, header[7]);
		ttl = header[8];
		protocol = header[9];
		headerChecksum = PacketUtils.joinBytes(header, 10, 11);
		source = loadAddress(header, 12);
		destination = loadAddress(header, 16);

		if (ihl > 5)
			options = PacketUtils.streamToByteArray(in, 4 * ihl - LENGTH);

		return HeaderFactory.forNexthdr(protocol);
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
		options = maybeCreateIpv4Options(random);
	}

	private byte[] maybeCreateIpv4Options(ThreadLocalRandom random) {
		if (random.nextInt(10) > 3)
			return null;

		// IHL is a 4-bit value, which means header + options can span up to 15
		// words (60 bytes).
		// 5 of those are already taken by the IPv4 header.
		int words = random.nextInt(1, 10);
		byte[] result = new byte[4 * words];
		random.nextBytes(result);
		return result;
	}

	@Override
	public void unsetChecksum() {
		this.headerChecksum = null;
	}

	@Override
	public void unsetLengths() {
		this.ihl = null;
		this.totalLength = null;
	}
}
