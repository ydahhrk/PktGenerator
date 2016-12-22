package mx.nic.jool.pktgen.auto;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;

public class FragTests {

	public void generateTests(String prefix) throws IOException {
		minMtu6Test(prefix);
		icmpErrorTest(prefix);
	}

	private void minMtu6Test(String prefix) throws IOException {
		Util.writePacket(prefix + "/sender/frag-minmtu6-big", //
				hdr4(true, false, 0x1234), //
				new UdpHeader(), //
				new Payload(1400));

		Packet result = new Packet();
		result.add(new Fragment( //
				hdr6(false), // 40
				hdrFrag(0x00001234), // + 8
				new UdpHeader(), // + 8
				new Payload(1224))); // + 1224 = 1280
		result.add(new Fragment( //
				hdr6(false), //
				hdrFrag(0x00001234), //
				new Payload(176, 0xc8))); // 1400 - 1224 = 176
		Util.writePacket(prefix + "/receiver/frag-minmtu6-big", result);
	}

	/**
	 * Tests Jool truncates ICMP errors if they're too big.
	 * 
	 * From RFC 1812: the ICMP datagram SHOULD contain as much of the original
	 * datagram as possible without the length of the ICMP datagram exceeding
	 * 576 bytes.
	 * 
	 * From RFC 4443: As much of invoking packet as possible without the ICMPv6
	 * packet exceeding the minimum IPv6 MTU [ie. 1280 bytes]
	 */
	private void icmpErrorTest(String prefix) throws IOException {
		Util.writePacket(prefix + "/sender/frag-icmp4", //
				hdr4(true, true, 0), //
				new Icmpv4ErrorHeader(), //
				new Ipv4Header(), //
				new UdpHeader(), //
				new Payload(1300));

		Ipv6Header innerHdr6 = new Ipv6Header();
		innerHdr6.setPayloadLength(1308);
		innerHdr6.swapAddresses();
		UdpHeader hdrUdp = new UdpHeader();
		hdrUdp.setLength(1308);
		hdrUdp.setChecksum(0x3022);

		Util.writePacket(prefix + "/receiver/frag-icmp4", //
				hdr6(false), //
				new Icmpv6ErrorHeader(), //
				innerHdr6, //
				hdrUdp, //
				new Payload(1184)); // 40 + 8 + 40 + 8 + 1184 = 1280

		// ---------------------------------------------------------------

		Util.writePacket(prefix + "/sender/frag-icmp6", //
				hdr6(true), //
				new Icmpv6ErrorHeader(), //
				new Ipv6Header(), //
				new UdpHeader(), //
				new Payload(1300));

		Ipv4Header hdr4Inner = new Ipv4Header();
		hdr4Inner.swapAddresses();
		hdr4Inner.setTotalLength(1328);

		Util.writePacket(prefix + "/receiver/frag-icmp6", //
				hdr4(false, true, 0), //
				new Icmpv4ErrorHeader(), //
				hdr4Inner, //
				hdrUdp, //
				new Payload(520)); // 20 + 8 + 20 + 8 + 520 = 576
	}

	private static Ipv4Header hdr4(boolean sender, boolean df, int id) {
		Ipv4Header result = new Ipv4Header();

		result.setIdentification(id);
		result.setDf(df);

		if (!sender) {
			result.setTtl(63);
			result.swapAddresses();
		}

		return result;
	}

	// private static Ipv4Header hdr4Inner(boolean sender, boolean df, int id) {
	// Ipv4Header result = hdr4(sender, df, id);
	// result.setIdentification(id);
	// result.setDf(df);
	// result.setTtl(63);
	// result.swapAddresses();
	// return result;
	// }

	private static Ipv6Header hdr6(boolean sender) {
		Ipv6Header result = new Ipv6Header();

		if (!sender) {
			result.setHopLimit(63);
			result.swapAddresses();
		}

		return result;
	}

	// private static Ipv6Header hdr6Inner(boolean sender) {
	// Ipv6Header result = hdr6(sender);
	// result.setHopLimit(63);
	// result.swapAddresses();
	// return result;
	// }

	private static FragmentExt6Header hdrFrag(long id) {
		FragmentExt6Header result = new FragmentExt6Header();
		result.setIdentification(id);
		return result;
	}

	// private static UdpHeader hdrUdp(boolean sixToFour) {
	// UdpHeader result = new UdpHeader();
	// if (!sixToFour) {
	// result.swapPorts();
	// }
	// return result;
	// }
	//
	// private static UdpHeader hdrUdpInner(boolean sixToFour) {
	// UdpHeader result = new UdpHeader();
	// if (sixToFour) {
	// result.swapPorts();
	// }
	// return result;
	// }

}
