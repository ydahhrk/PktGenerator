package mx.nic.jool.pktgen.main;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.DestinationOptionExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.Extension6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.HopByHopExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.RoutingExt6Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.TcpHeader;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;

public class PacketGeneratorRandom {

	public static void main(String[] args) throws IOException {
		Fragment fragment = new Fragment();
		Packet packet = new Packet();
		packet.add(fragment);

		ThreadLocalRandom random = ThreadLocalRandom.current();
		boolean ipv6 = random.nextBoolean();

		if (ipv6) {
			fragment.add(new Ipv6Header());
			maybeAddIpv6ExtensionHeaders(fragment, random);
		} else {
			fragment.add(new Ipv4Header());
		}

		switch (random.nextInt(4)) {
		case 0: // TCP
			fragment.add(new TcpHeader());
			break;
		case 1: // UDP
			fragment.add(new UdpHeader());
			break;
		// case 2: // ICMP info
		// fragment.add(ipv6 ? new Icmpv6ErHeader() : new Icmpv4InfoHeader());
		// break;
		case 3: // ICMP error
			if (ipv6) {
				fragment.add(new Icmpv6ErrorHeader());

				Ipv6Header internal = new Ipv6Header();
				internal.swapIdentifiers();
				fragment.add(internal);
				maybeAddIpv6ExtensionHeaders(fragment, random);

			} else {
				fragment.add(new Icmpv4ErrorHeader());

				Ipv4Header internal = new Ipv4Header();
				internal.swapIdentifiers();
				fragment.add(internal);
			}

			switch (random.nextInt(2)) {
			case 0: // TCP
				fragment.add(new TcpHeader());
				break;
			case 1: // UDP
				fragment.add(new UdpHeader());
				break;
			}
			break;
		}

		int payloadLength = random.nextInt(1500 - fragment.getLength());
		fragment.add(new Payload(payloadLength));

		packet.randomize();
		packet.postProcess();
		packet.export("random");
	}

	private static void maybeAddIpv6ExtensionHeaders(Fragment fragment, ThreadLocalRandom random) {
		if (random.nextInt(10) > 3)
			return;

		// The IPv6 spec wants extension headers to always follow a particular
		// order, but since Jool doesn't care for anything other than fragment
		// headers, it shouldn't hurt to include a little extra random kitchen
		// sink for good measure.

		int headers = random.nextInt(6);
		for (int i = 0; i < headers; i++)
			fragment.add(createRandomExtensionHeader(fragment, random));
	}

	private static Extension6Header createRandomExtensionHeader(Fragment fragment, ThreadLocalRandom random) {
		switch (random.nextInt(4)) {
		case 0:
			return new DestinationOptionExt6Header();
		case 1:
			return new FragmentExt6Header();
		case 2:
			return new HopByHopExt6Header();
		case 3:
			return new RoutingExt6Header();
		}

		throw new IllegalArgumentException("The random overflowed.");
	}
}
