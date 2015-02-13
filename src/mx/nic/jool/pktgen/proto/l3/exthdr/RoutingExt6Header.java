package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.util.ArrayList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.Protocol;

public class RoutingExt6Header implements Extension6Header {

	private Integer nextHeader;
	private Integer hdrExtLength;
	private int routingType;
	private Integer segmentsLeft;

	private long reserved;

	private List<Inet6Address> ipv6List;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		boolean newIn6Addr;

		nextHeader = scanner.readProtocol("Next Header", "auto");
		hdrExtLength = scanner.readInteger("Header Extension Length", "auto");
		routingType = scanner.readInt("Routing Type", 0);
		segmentsLeft = scanner.readInteger("Segments Left", "auto");

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
	public void postProcess(Packet packet, Fragment fragment)
			throws IOException {
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
		 // TODO
		throw new UnsupportedOperationException("Not implemented yet.");
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
	public Protocol getProtocol() {
		return Protocol.ROUTING_EXT6HDR;
	}

	@Override
	public String getShortName() {
		return "rext";
	}

	@Override
	public void modifyHdrFromStdIn(FieldScanner scanner) {
		Util.modifyFieldValues(this, scanner);
	}

}
