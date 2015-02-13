package mx.nic.jool.pktgen;

import java.io.IOException;
import java.util.Scanner;

import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.Protocol;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.DestinationOptionExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.HopByHopExt6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.RoutingExt6Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4InfoHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6InfoHeader;
import mx.nic.jool.pktgen.proto.l4.TcpHeader;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;

public class PacketGen {

	public static void main(String[] args) throws IOException {
		FieldScanner scanner = new FieldScanner(new Scanner(System.in));
		Packet packet = new Packet();
		Fragment frag = new Fragment();
		boolean auto = true;
		
		if (args.length != 0 && args[0].equalsIgnoreCase("manual"))
			auto = false;
		
		/* Build the packet. */
		Protocol nextProto;
		do {
			Integer nextProtoInt = scanner.readProtocol("Next", "exit");
			if (nextProtoInt == null)
				break;

			nextProto = Protocol.fromInt(nextProtoInt);
			if (nextProto == null) {
				System.out.println("Invalid; try again.");
				continue;
			}

			PacketContent nextContent = null;
			switch (nextProto) {
			case IPV4:
				nextContent = new Ipv4Header();
				break;
			case IPV6:
				nextContent = new Ipv6Header();
				break;
			case UDP:
				nextContent = new UdpHeader();
				break;
			case TCP:
				nextContent = new TcpHeader();
				break;
			case ICMPV4:
				nextContent = new Icmpv4InfoHeader();
				break;
			case ICMPV6:
				nextContent = new Icmpv6InfoHeader();
				break;
			case PAYLOAD:
				nextContent = new Payload();
				break;
			case HOP_BY_HOP_EXT6HDR:
				nextContent = new HopByHopExt6Header();
				break;
			case ROUTING_EXT6HDR:
				nextContent = new RoutingExt6Header();
				break;
			case DESTINATION_OPTION_EXT6HDR:
				nextContent = new DestinationOptionExt6Header();
				break;
			case FRAGMENT_EXT6HDR:
				nextContent = new FragmentExt6Header();
				break;
			}

		if (auto)
			nextContent.modifyHdrFromStdIn(scanner);
		else
			nextContent.readFromStdIn(scanner);
		
			frag.add(nextContent);
		} while (true);

		/* Wrap up */
		packet.add(frag);

		packet.postProcess();
		

		/* Output */
		boolean success = false;
		do {
			String outputFile = scanner.readLine("Output filename",
					"output");
			try {
				packet.export(outputFile);
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Try again: ");
			}
		} while (!success);
	}

}
