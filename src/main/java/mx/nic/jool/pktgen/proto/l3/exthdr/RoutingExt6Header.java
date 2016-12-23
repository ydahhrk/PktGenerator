package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;

public class RoutingExt6Header extends Extension6Header {

	private Integer nextHeader;
	private Integer hdrExtLength;
	private int routingType;
	private Integer segmentsLeft;
	private long reserved;
	private List<Inet6Address> ipv6List;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		boolean newIn6Addr;

		nextHeader = scanner.readProtocol("Next Header");
		hdrExtLength = scanner.readInteger("Header Extension Length");
		routingType = scanner.readInt("Routing Type", 0);
		segmentsLeft = scanner.readInteger("Segments Left");

		reserved = scanner.readLong("Reserved", 0);

		ipv6List = new ArrayList<Inet6Address>();

		do {
			newIn6Addr = scanner.readBoolean("Add an IPv6 Address", false);
			if (!newIn6Addr)
				break;
			ipv6List.add(scanner.readAddress6("IPv6 Address"));
		} while (newIn6Addr);

	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}
		if (hdrExtLength == null) {
			hdrExtLength = ipv6List.size() * 2;
		}
		if (segmentsLeft == null) {
			segmentsLeft = ipv6List.size();
		}
	}

	@Override
	public PacketContent createClone() {
		RoutingExt6Header result = new RoutingExt6Header();

		result.nextHeader = nextHeader;
		result.hdrExtLength = hdrExtLength;
		result.routingType = routingType;
		result.segmentsLeft = segmentsLeft;
		result.reserved = reserved;
		/* TODO deep copy? others do this as well. */
		result.ipv6List = ipv6List;

		return result;
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hdrExtLength);
		PacketUtils.write8BitInt(out, routingType);
		PacketUtils.write8BitInt(out, segmentsLeft);

		PacketUtils.write32BitInt(out, reserved);

		for (Inet6Address ipv6 : ipv6List) {
			out.write(ipv6.getAddress());
		}

		return out.toByteArray();
	}

	@Override
	public String getShortName() {
		return "rext";
	}

	@Override
	public int getHdrIndex() {
		return 43;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		throw new IllegalArgumentException("Sorry; Routing headers are not supported in load-from-file mode yet.");
	}

	@Override
	public void randomize() {
		throw new IllegalArgumentException("Sorry; Routing headers are not supported in random mode yet.");
	}

	@Override
	public void unsetLengths() {
		this.hdrExtLength = null;
	}
}
