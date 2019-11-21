package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.proto.HeaderFactory;

/**
 * https://tools.ietf.org/html/rfc2460#section-4.4
 */
public class RoutingExt6Header extends Extension6Header {

	@HeaderField
	private Integer nextHeader;
	@HeaderField
	private Integer hdrExtLength;
	@HeaderField
	private int routingType;
	@HeaderField
	private Integer segmentsLeft;
	@HeaderField
	private long reserved;
	/**
	 * Must not be null! (see
	 * {@link FieldScanner#read(Object, java.lang.reflect.Field)}.)
	 */
	@HeaderField
	private Inet6AddressList addresses = new Inet6AddressList();

	@Override
	public void postProcess(Packet packet, Fragment fragment) {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}
		if (hdrExtLength == null) {
			hdrExtLength = addresses.getLength() * 2;
		}
		if (segmentsLeft == null) {
			segmentsLeft = addresses.getLength();
		}
	}

	@Override
	public Header createClone() {
		RoutingExt6Header result = new RoutingExt6Header();

		result.nextHeader = nextHeader;
		result.hdrExtLength = hdrExtLength;
		result.routingType = routingType;
		result.segmentsLeft = segmentsLeft;
		result.reserved = reserved;
		result.addresses = addresses;

		return result;
	}

	@Override
	public byte[] toWire() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hdrExtLength);
		PacketUtils.write8BitInt(out, routingType);
		PacketUtils.write8BitInt(out, segmentsLeft);
		PacketUtils.write32BitInt(out, reserved);
		out.write(addresses.toWire());

		return out.toByteArray();
	}

	@Override
	public String getShortName() {
		return "rext";
	}

	@Override
	public String getName() {
		return "Routing Header";
	}

	@Override
	public int getHdrIndex() {
		return 43;
	}

	@Override
	public Header loadFromStream(InputStream in) throws IOException {
		int[] header = PacketUtils.streamToIntArray(in, 8);

		nextHeader = header[0];
		hdrExtLength = header[1];
		routingType = header[2];
		if (routingType != 0)
			throw new IllegalArgumentException("Only type 0 routing headers are supported in load-from-file mode.");
		segmentsLeft = header[3];
		reserved = PacketUtils.joinBytes(header[4], header[5], header[6], header[7]);
		addresses.loadFromStream(in, hdrExtLength);

		return HeaderFactory.forNexthdr(nextHeader);
	}

	@Override
	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		routingType = 0;
		addresses.randomize();
		segmentsLeft = random.nextInt(addresses.getLength());
	}

	@Override
	public void unsetLengths() {
		this.hdrExtLength = null;
	}
}
