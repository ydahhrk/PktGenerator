package mx.nic.jool.pktgen.proto.l3;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Properties;

import mx.nic.jool.pktgen.BitArrayOutputStream;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.pojo.shortcut.SwapIdentifiersShortcut;
import mx.nic.jool.pktgen.pojo.shortcut.TtlDecShortcut;
import mx.nic.jool.pktgen.type.Field;
import mx.nic.jool.pktgen.type.IntField;
import mx.nic.jool.pktgen.type.IpAddrField;

/**
 * https://tools.ietf.org/html/rfc2460#section-3
 */
public class Ipv6Header extends Layer3Header {

	private static final Inet6Address DEFAULT_SRC;
	private static final Inet6Address DEFAULT_DST;

	static {
		try {
			Properties properties = new Properties();
			try (FileInputStream fis = new FileInputStream("address.properties")) {
				properties.load(fis);
			}
			DEFAULT_SRC = (Inet6Address) InetAddress.getByName(properties.getProperty("ipv6.source"));
			DEFAULT_DST = (Inet6Address) InetAddress.getByName(properties.getProperty("ipv6.destination"));
		} catch (IOException e) {
			throw new IllegalArgumentException("There's something wrong with address.properties.");
		}
	}

	private IntField version = new IntField("version", 4, 6);
	private IntField trafficClass = new IntField("trafficClass", 8, 0);
	private IntField flowLabel = new IntField("flowLabel", 20, 0);
	private IntField payloadLength = new IntField("payloadLength", 16, null);
	private IntField nextHeader = new IntField("nextHeader", 8, null);
	private IntField hopLimit = new IntField("hopLimit", 8, 64);
	private IpAddrField src = new IpAddrField("src", DEFAULT_SRC);
	private IpAddrField dst = new IpAddrField("dst", DEFAULT_DST);

	private Field[] fields = new Field[] { //
			version, trafficClass, flowLabel, //
			payloadLength, nextHeader, hopLimit, //
			src, //
			dst //
	};

	@Override
	public Field[] getFields() {
		return fields;
	}

	@Override
	public void postProcess() {
		if (payloadLength.getValue() == null) {
			int payloadLength = 0;
			for (Header header = getNext(); header != null; header = header.getNext())
				payloadLength += header.getLength();
			this.payloadLength.setValue(payloadLength);
		}

		if (nextHeader.getValue() == null) {
			nextHeader.setValue(getNextHdr());
		}
	}

	@Override
	public byte[] getPseudoHeader(int payloadLength, int nextHdr) {
		BitArrayOutputStream out = new BitArrayOutputStream(40);

		src.write(out);
		dst.write(out);
		new IntField("payloadLength", 32, payloadLength).write(out);
		new IntField("padding", 24, 0).write(out);
		new IntField("nextHdr", 8, nextHdr).write(out);

		return out.toByteArray();
	}

	@Override
	public String getName() {
		return "IPv6 Header";
	}

	@Override
	public void swapIdentifiers() {
		InetAddress tmp = src.getValue();
		src.setValue(dst.getValue());
		dst.setValue(tmp);
	}

	@Override
	public int getHdrIndex() {
		return -6;
	}

	@Override
	public Shortcut[] getShortcuts() {
		return new Shortcut[] { //
				new TtlDecShortcut(), //
				new SwapIdentifiersShortcut(), //
		};
	}

	public void decTtl() {
		this.hopLimit.setValue(this.hopLimit.getValue() - 1);
	}
}
