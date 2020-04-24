package mx.nic.jool.pktgen.proto.l3;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Properties;

import mx.nic.jool.pktgen.BitArrayOutputStream;
import mx.nic.jool.pktgen.ChecksumBuilder;
import mx.nic.jool.pktgen.ChecksumStatus;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.SwapIdentifiersShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.TtlDecShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.NotDfShortcut;
import mx.nic.jool.pktgen.type.BoolField;
import mx.nic.jool.pktgen.type.ByteArrayField;
import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;
import mx.nic.jool.pktgen.type.IpAddrField;

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

	private IntField version = new IntField("version", 4, 4);
	private IntField ihl = new IntField("ihl", 4, null);
	private IntField tos = new IntField("tos", 8, 0);
	private IntField totalLength = new IntField("total length", 16, null);
	private IntField identification = new IntField("identification", 16, 0);
	private BoolField reserved = new BoolField("reserved", false);
	private BoolField df = new BoolField("df", true);
	private BoolField mf = new BoolField("mf", false);
	private IntField fragmentOffset = new IntField("fragment offset", 13, 0);
	private IntField ttl = new IntField("ttl", 8, 64);
	private IntField protocol = new IntField("protocol", 8, null);
	private IntField headerChecksum = new IntField("header checksum", 16, null, IntField.FLAG_HEX);
	private IpAddrField src = new IpAddrField("src", DEFAULT_SRC);
	private IpAddrField dst = new IpAddrField("dst", DEFAULT_DST);
	private ByteArrayField options = new ByteArrayField("options", null);

	private Field[] fields = new Field[] { //
			version, ihl, tos, totalLength, //
			identification, reserved, df, mf, fragmentOffset, //
			ttl, protocol, headerChecksum, //
			src, //
			dst, //
			options //
	};

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public void postProcess() throws IOException {
		if (ihl.getValue() == null) {
			int headerLength = Field.getLength(fields);
			int ihl = headerLength >> 2;
			if ((headerLength & 3) != 0)
				ihl++;
			if (ihl > 15)
				System.err.println("Warning: ihl (" + ihl + ") > 15");
			this.ihl.setValue(ihl);
		}

		if (totalLength.getValue() == null) {
			int totalLength = 0;
			for (Header header = this; header != null; header = header.getNext())
				totalLength += header.getLength();
			this.totalLength.setValue(totalLength);
		}

		if (protocol.getValue() == null) {
			protocol.setValue(getNextHdr());
		}

		if (headerChecksum.getValue() == null) {
			headerChecksum.setValue(0);
			try (ChecksumBuilder csum = new ChecksumBuilder()) {
				csum.write(toWire());
				headerChecksum.setValue(csum.finish(ChecksumStatus.CORRECT));
			}
		}

		if (options.getValue() == null) {
			options.setValue(new byte[0]);
		}

//		if (options != null && ((options.length & 3) != 0)) {
//			int fixedLength = options.length + (4 - options.length & 3);
//			options = Arrays.copyOf(options, fixedLength);
//		}
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, int nextHdr) {
		BitArrayOutputStream out = new BitArrayOutputStream(12);

		src.write(out);
		dst.write(out);
		new IntField("padding", 8, 0).write(out);
		new IntField("nextHdr", 8, nextHdr).write(out);
		new IntField("payloadLength", 16, payloadLength).write(out);

		return out.toByteArray();
	}

	@Override
	public String getName() {
		return "IPv4 Header";
	}

	@Override
	public void swapIdentifiers() {
		InetAddress tmp = src.getValue();
		src.setValue(dst.getValue());
		dst.setValue(tmp);
	}

	@Override
	public int getHdrIndex() {
		return -4;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { //
				new NotDfShortcut(), //
				new TtlDecShortcut(), //
				new SwapIdentifiersShortcut(), //
		};
	}

	public void notDf() {
		this.df.setValue(!this.df.getValue());
	}

	public void decTtl() {
		this.ttl.setValue(this.ttl.getValue() - 1);
	}

	public Integer getIhl() {
		return ihl.getValue();
	}

}
