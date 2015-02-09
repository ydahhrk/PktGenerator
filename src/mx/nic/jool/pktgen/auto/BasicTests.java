package mx.nic.jool.pktgen.auto;

import java.io.IOException;

import mx.nic.jool.pktgen.ChecksumStatus;
import mx.nic.jool.pktgen.CsumBuilder;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv4InfoHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6InfoHeader;
import mx.nic.jool.pktgen.proto.l4.Layer4Header;
import mx.nic.jool.pktgen.proto.l4.TcpHeader;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;


public class BasicTests {
	
	private boolean sender;
	private String fileNamePrefix, fileNameSuffix;
	private boolean df;
	boolean is6to4;
	
	private ChecksumStatus[] csumStatuses = { ChecksumStatus.CORRECT, ChecksumStatus.WRONG };

	public void generateTests() throws IOException {
		Ipv4Header.stateless();
		Ipv6Header.stateless();
		
		this.fileNamePrefix = "result/stateless/sender/";
		generateStatelessSenderIpv6Files();
		this.fileNamePrefix = "result/stateless/receiver/";
		generateStatelessReceiverIpv4Files();
		
		this.fileNamePrefix = "result/stateless/sender/";
		generateStatelessSenderIpv4Files();
		this.fileNamePrefix = "result/stateless/receiver/";
		generateStatelessReceiverIpv6Files();
		
		Ipv4Header.stateful();
		Ipv6Header.stateful();
		
		this.fileNamePrefix = "result/stateful/sender/";
		generateStatefulSenderIpv6Files();
		this.fileNamePrefix = "result/stateful/receiver/";
		generateStatefulReceiverIpv4Files();
		
		this.fileNamePrefix = "result/stateful/sender/";
		generateStatefulSenderIpv4Files();
		this.fileNamePrefix = "result/stateful/receiver/";
		generateStatefulReceiverIpv6Files();
	}
	
	// TODO combine df with no df on ICMP errors?

	private void generateStatelessSenderIpv6Files() throws IOException {
		this.sender = true;
		this.is6to4 = true;
		final int id = 0x87654321;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new TcpHeader(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerSender(), hdrUdpInnerSender(), payload());
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr6(), hdrFrag(id), hdrUdp(), payload());
			writePacket(hdr6(), hdrFrag(id), new TcpHeader(), payload());
			writePacket(hdr6(), hdrFrag(id), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), hdrFrag(id), new Icmpv6ErrorHeader(), hdr6InnerSender(), hdrFrag(id), hdrUdpInnerSender(), payload());
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket6(hdr6(), hdrFrag(id), hdrUdp());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new TcpHeader());
		}
	}
	
	private void generateStatelessReceiverIpv4Files() throws IOException {
		this.sender = false;
		this.is6to4 = true;
		final int idA = 0x0;
		final int idB = 0x4321;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr4(idA), hdrUdp(), payload());
			writePacket(hdr4(idA), new TcpHeader(), payload());
			writePacket(hdr4(idA), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4ReceiverError(idA), new Icmpv4ErrorHeader(), hdr4InnerReceiver(idA, 1308), hdrUdpInnerReceiver4(/* 1288 */), new Payload(520));
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr4(idB), hdrUdp(), payload());
			writePacket(hdr4(idB), new TcpHeader(), payload());
			writePacket(hdr4(idB), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4ReceiverError(idB), new Icmpv4ErrorHeader(), hdr4InnerReceiver(idB, 1228), hdrUdpInnerReceiver4(/* 1208 */), new Payload(520));

			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket4(hdr4(idB), hdrUdp());
			writeFragmentedPacket4(hdr4(idB), new TcpHeader());
		}
	}
	
	private void generateStatelessSenderIpv4Files() throws IOException {
		this.sender = true;
		this.is6to4 = false;
		final int idA = 0x1234;
		final int idB = 0x1234;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr4(idA), hdrUdp(), payload());
			writePacket(hdr4(idA), new TcpHeader(), payload());
			writePacket(hdr4(idA), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idA), new Icmpv4ErrorHeader(), hdr4InnerSender(idA), hdrUdpInnerSender(), payload());
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr4(idB), hdrUdp(), payload());
			writePacket(hdr4(idB), new TcpHeader(), payload());
			writePacket(hdr4(idB), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idB), new Icmpv4ErrorHeader(), hdr4InnerSender(idB), hdrUdpInnerSender(), payload());
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket4(hdr4(idB), hdrUdp());
			writeFragmentedPacket4(hdr4(idB), new TcpHeader());
		}
	}
	
	private void generateStatelessReceiverIpv6Files() throws IOException {
		this.sender = false;
		this.is6to4 = false;
		final int id = 0x00001234;

		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new TcpHeader(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerReceiver(1288), hdrUdpInnerReceiver6(/* 1288 */), new Payload(1184));
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new TcpHeader(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerReceiver(1208), hdrUdpInnerReceiver6(/* 1208 */), new Payload(1184));
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket6(hdr6(), hdrFrag(id), hdrUdp());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new TcpHeader());
		}
	}
	
	private void generateStatefulSenderIpv6Files() throws IOException {
		this.sender = true;
		this.is6to4 = true;
		final int id = 0x87654321;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;

			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new TcpHeader(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerSender(), hdrUdpInnerSender(), payload());

			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;

			writePacket(hdr6(), hdrFrag(id), hdrUdp(), payload());
			writePacket(hdr6(), hdrFrag(id), new TcpHeader(), payload());
			writePacket(hdr6(), hdrFrag(id), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), hdrFrag(id), new Icmpv6ErrorHeader(), hdr6InnerSender(), hdrFrag(id), hdrUdpInnerSender(), payload());

			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";

			writeFragmentedPacket6(hdr6(), hdrFrag(id), hdrUdp());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new TcpHeader());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new Icmpv6InfoHeader());
		}
	}
	
	private void generateStatefulReceiverIpv4Files() throws IOException {
		this.sender = false;
		this.is6to4 = true;
		final int idA = 0x0;
		final int idB = 0x4321;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;

			writePacket(hdr4(idA), hdrUdp(), payload());
			writePacket(hdr4(idA), new TcpHeader(), payload());
			writePacket(hdr4(idA), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idA, false), new Icmpv4ErrorHeader(), hdr4InnerReceiver(idA, false, 1308), hdrUdpInnerReceiver4(), new Payload(520));

			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;

			writePacket(hdr4(idB), hdrUdp(), payload());
			writePacket(hdr4(idB), new TcpHeader(), payload());
			writePacket(hdr4(idB), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idB), new Icmpv4ErrorHeader(), hdr4InnerReceiver(idB, 1228), hdrUdpInnerReceiver4(), new Payload(520));

			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";

			writeFragmentedPacket4(hdr4(idB), hdrUdp());
			writeFragmentedPacket4(hdr4(idB), new TcpHeader());
			writeFragmentedPacket4(hdr4(idB), new Icmpv4InfoHeader());
		}
	}
	
	private void generateStatefulSenderIpv4Files() throws IOException {
		this.sender = true;
		this.is6to4 = false;
		final int idA = 0x1234;
		final int idB = 0x1234;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr4(idA), hdrUdp(), payload());
			writePacket(hdr4(idA), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idA), new Icmpv4ErrorHeader(), hdr4InnerSender(idA), hdrUdpInnerSender(), payload());
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr4(idB), hdrUdp(), payload());
			writePacket(hdr4(idB), new Icmpv4InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr4(idB), new Icmpv4ErrorHeader(), hdr4InnerSender(idB), hdrUdpInnerSender(), payload());

			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket4(hdr4(idB), hdrUdp());
			writeFragmentedPacket4(hdr4(idB), new TcpHeader());
			writeFragmentedPacket4(hdr4(idB), new Icmpv4InfoHeader());
		}
	}

	private void generateStatefulReceiverIpv6Files() throws IOException {
		this.sender = false;
		this.is6to4 = false;
		final int id = 0x00001234;
		
		for (ChecksumStatus csumStatus : csumStatuses) {
			CsumBuilder.checksumStatus = csumStatus;
			
			fileNameSuffix = csumStatus.getShortName() + "-df-nofrag";
			df = true;
			
			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerReceiver(1288), hdrUdpInnerReceiver6(), new Payload(1184));
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-nofrag";
			df = false;
			
			writePacket(hdr6(), hdrUdp(), payload());
			writePacket(hdr6(), new Icmpv6InfoHeader(), payload());
			if (csumStatus != ChecksumStatus.WRONG)
				writePacket(hdr6(), new Icmpv6ErrorHeader(), hdr6InnerReceiver(1208), hdrUdpInnerReceiver6(), new Payload(1184));
			
			fileNameSuffix = csumStatus.getShortName() + "-nodf-frag";
			
			writeFragmentedPacket6(hdr6(), hdrFrag(id), hdrUdp());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new TcpHeader());
			writeFragmentedPacket6(hdr6(), hdrFrag(id), new Icmpv6InfoHeader());
		}
	}
	
	private Ipv4Header hdr4(int id) {
		return Util.hdr4(sender, df, id);
	}
	
	private Ipv4Header hdr4(int id, boolean df) {
		return Util.hdr4(sender, df, id);
	}
	
	private Ipv4Header hdr4InnerSender(int id) {
		return Util.hdr4Inner(true, df, id);
	}
	
	private Ipv4Header hdr4InnerReceiver(int id, int totalLength) {
		Ipv4Header result = Util.hdr4Inner(false, df, id);
		result.setTotalLength(totalLength);
		return result;
	}
	
	private Ipv4Header hdr4InnerReceiver(int id, boolean df, int totalLength) {
		Ipv4Header result = Util.hdr4Inner(false, df, id);
		result.setTotalLength(totalLength);
		return result;
	}
	
	private Ipv4Header hdr4ReceiverError(int id) {
		return Util.hdr4(sender, false, id);
	}
	
	private Ipv6Header hdr6() {
		return Util.hdr6(sender);
	}
	
	private Ipv6Header hdr6InnerSender() {
		return Util.hdr6Inner(sender);
	}
	
	private Ipv6Header hdr6InnerReceiver(int payloadLength) {
		Ipv6Header result = Util.hdr6Inner(sender);
		result.setPayloadLength(payloadLength);
		return result;
	}
	
	private FragmentExt6Header hdrFrag(long id) {
		return Util.hdrFrag(id);
	}
	
	private UdpHeader hdrUdp() {
		return Util.hdrUdp(is6to4);
	}
	
	private UdpHeader hdrUdpInnerSender() {
		return Util.hdrUdpInner(is6to4);
	}
	
	private UdpHeader hdrUdpInnerReceiver4() throws IOException {
		// The point of this mess is to autocompute both the length and the checksum.
		UdpHeader result = hdrUdpInnerSender();
		Packet packet = new Packet(Util.hdr4Inner(true, df, 0), result, payload());
		packet.postProcess();
		return result;
	}
	
	private UdpHeader hdrUdpInnerReceiver6() throws IOException {
		UdpHeader result = hdrUdpInnerSender();
		Packet packet = new Packet(Util.hdr6(true), result, payload());
		packet.postProcess();
		return result;
	}
	
	private Payload payload() {
		return new Payload(df ? 1280 : 1200);
	}
	
	private void writePacket(PacketContent... content) throws IOException {
		writePacket(new Packet(content));
	}
	
	private void writePacket(Packet packet) throws IOException {
		String l3HdrName = null;
		String l4HdrName = null;
		
		for (PacketContent content : packet.get(0)) {
			if (l3HdrName == null && content.getProtocol().getLayer() == 3)
				l3HdrName = content.getShortName();
			if (l4HdrName == null && content.getProtocol().getLayer() == 4)
				l4HdrName = content.getShortName();
		}

		StringBuilder fileName = new StringBuilder().append(fileNamePrefix);
		fileName.append(l3HdrName).append("-");
		fileName.append(l4HdrName).append("-");
		fileName.append(fileNameSuffix);
		
		Util.writePacket(fileName.toString(), packet);
	}
	
	private void writeFragmentedPacket4(Ipv4Header hdr4, Layer4Header l4Hdr) throws IOException {
		Packet packet = new Packet();
		
		packet.add(hdr4, l4Hdr, new Payload(1200));
		packet.add(hdr4.createClone(), new Payload(1200));
		packet.add(hdr4.createClone(), new Payload(1200));
		
		writePacket(packet);
	}
	
	private void writeFragmentedPacket6(Ipv6Header hdr6, FragmentExt6Header fragHdr, Layer4Header l4Hdr) throws IOException {
		Packet packet = new Packet();
		int payload1Length = (l4Hdr instanceof TcpHeader) ? 1196 : 1200;
		
		packet.add(hdr6, fragHdr, l4Hdr, new Payload(payload1Length));
		packet.add(hdr6.createClone(), fragHdr.createClone(), new Payload(1200));
		packet.add(hdr6.createClone(), fragHdr.createClone(), new Payload(1200));
		
		writePacket(packet);
	}

}
