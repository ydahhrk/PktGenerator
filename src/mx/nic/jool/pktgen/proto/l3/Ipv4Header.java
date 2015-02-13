package mx.nic.jool.pktgen.proto.l3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
import mx.nic.jool.pktgen.proto.Protocol;
import mx.nic.jool.pktgen.proto.optionsdata4.EndOptionList;
import mx.nic.jool.pktgen.proto.optionsdata4.InetTimestamp;
import mx.nic.jool.pktgen.proto.optionsdata4.Ipv4OptionHeader;
import mx.nic.jool.pktgen.proto.optionsdata4.Ipv4Options;
import mx.nic.jool.pktgen.proto.optionsdata4.NoOperation;
import mx.nic.jool.pktgen.proto.optionsdata4.RecordRoute;
import mx.nic.jool.pktgen.proto.optionsdata4.StreamIdentifier;

public class Ipv4Header implements Layer3Header {

	public static Inet4Address DEFAULT_REMOTE;
	public static Inet4Address DEFAULT_LOCAL;

	private static void setDefaults(String remote, String local) {
		try {
			DEFAULT_REMOTE = (Inet4Address) InetAddress.getByName(remote);
			DEFAULT_LOCAL = (Inet4Address) InetAddress.getByName(local);
		} catch (UnknownHostException e) {
			// It's hardcoded so this is unexpected.
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void stateless() {
		setDefaults("198.51.100.2", "192.0.2.33");
	}
	
	public static void stateful() {
		setDefaults("192.0.2.5", "192.0.2.2");
	}
	
	static {
		stateful();
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

	private ArrayList<Ipv4OptionHeader> options = new ArrayList<>();
	private int paddingOctects; // TODO: implementar

	public Ipv4Header() {
		source = DEFAULT_REMOTE;
		destination = DEFAULT_LOCAL;
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

		options = new ArrayList<>();
		setIpv4Options(scanner);
	}

	private void setIpv4Options(FieldScanner scanner) {
		Ipv4Options nextOption;
		do {
			Integer nextOptionInt = scanner.readIpv4OptionTypes(
					"Next Ipv4 Option", "exit");
			if (nextOptionInt == null)
				break;

			nextOption = Ipv4Options.fromInt(nextOptionInt);
			if (nextOption == null) {
				System.out.println("Invalid; try again.");
				continue;
			}

			Ipv4OptionHeader nextContent = null;
			switch (nextOption) {
			case END_OPTION_LIST:
				nextContent = new EndOptionList();
				break;
			case INTERNET_TIMESTAMP:
				nextContent = new InetTimestamp();
				break;
			case NO_OPERATION:
				nextContent = new NoOperation();
				break;
			case RECORDS_ROUTE:
				nextContent = new RecordRoute();
				break;
			case STREAM_IDENTIFIER:
				nextContent = new StreamIdentifier();
				break;
			}

			nextContent.readFromStdIn(scanner);
			options.add(nextContent);
		} while (true);
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
		for (Ipv4OptionHeader ip4Option : options) {
			ip4Option.postProcess(null, null);
		}

		if (ihl == null) {
			int mod;
			int headerLength = LENGTH;
			for (Ipv4OptionHeader ipv4Option : options) {
				headerLength += ipv4Option.toWire().length;
			}
			mod = headerLength % 4;
			if (mod == 0) {
				ihl = headerLength >> 2;
				paddingOctects = 0;
			} else {
				ihl = (headerLength >> 2) + 1;
				paddingOctects = 4 - mod;
			}
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

		for (Ipv4OptionHeader ipv4Option : options) {
			out.write(ipv4Option.toWire());
		}
		for (int i = 0; i < paddingOctects; i++) {
			PacketUtils.write8BitInt(out, 0);
		}

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
		
		result.options = new ArrayList<>();
		for (Ipv4OptionHeader option : options) {
			result.options.add((Ipv4OptionHeader) option.createClone());
		}
		result.paddingOctects = paddingOctects;
		
		return result;
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, Protocol nextProtocol)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		out.write(source.getAddress());
		out.write(destination.getAddress());
		PacketUtils.write8BitInt(out, 0);
		PacketUtils.write8BitInt(out, nextProtocol.toWire());
		PacketUtils.write16BitInt(out, payloadLength);
		
		return out.toByteArray();
	}

	@Override
	public Protocol getProtocol() {
		return Protocol.IPV4;
	}

	@Override
	public String getShortName() {
		return "4";
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
		stateful();
		options = new ArrayList<>();
		Util.modifyFieldValues(this, scanner);

	}
	
}
