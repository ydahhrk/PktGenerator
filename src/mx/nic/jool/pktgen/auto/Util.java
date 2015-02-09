package mx.nic.jool.pktgen.auto;

import java.io.IOException;

import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;


public class Util {

	public static void writePacket(String fileName, PacketContent... content) throws IOException {
		writePacket(fileName, new Packet(content));
	}
	
	public static void writePacket(String fileName, Packet packet) throws IOException {
		packet.postProcess();
		packet.export(fileName);
	}

	public static Ipv4Header hdr4(boolean sender, boolean df, int id) {
		Ipv4Header result = new Ipv4Header();
		
		result.setIdentification(id);
		result.setDf(df);
		
		if (!sender) {
			result.setTtl(63);
			result.swapAddresses();
		}
		
		return result;
	}
	
	public static Ipv4Header hdr4Inner(boolean sender, boolean df, int id) {
		Ipv4Header result = hdr4(sender, df, id);
		result.setIdentification(id);
		result.setDf(df);
		result.setTtl(63);
		result.swapAddresses();
		return result;
	}
	
	public static Ipv6Header hdr6(boolean sender) {
		Ipv6Header result = new Ipv6Header();
		
		if (!sender) {
			result.setHopLimit(63);
			result.swapAddresses();
		}

		return result;
	}
	
	public static Ipv6Header hdr6Inner(boolean sender) {
		Ipv6Header result = hdr6(sender);
		result.setHopLimit(63);
		result.swapAddresses();
		return result;
	}
	
	public static FragmentExt6Header hdrFrag(long id) {
		FragmentExt6Header result = new FragmentExt6Header();
		result.setIdentification(id);
		return result;
	}
	
	public static UdpHeader hdrUdp(boolean sixToFour) {
		UdpHeader result = new UdpHeader();
		if (!sixToFour) {
			result.swapPorts();
		}
		return result;
	}
	
	public static UdpHeader hdrUdpInner(boolean sixToFour) {
		UdpHeader result = new UdpHeader();
		if (sixToFour) {
			result.swapPorts();
		}
		return result;
	}
	
}
