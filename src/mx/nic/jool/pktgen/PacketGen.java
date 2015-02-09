package mx.nic.jool.pktgen;

public class PacketGen {

	// public static void main(String[] args) throws IOException {
	// FieldScanner scanner = new FieldScanner(new Scanner(System.in));
	// List<PacketContent> packet = new ArrayList<>();
	//
	// /* Build the packet. */
	// Protocol nextProto;
	// do {
	// Integer nextProtoInt = scanner.readProtocol("Next", "exit");
	// if (nextProtoInt == null)
	// break;
	//
	// nextProto = Protocol.fromInt(nextProtoInt);
	// if (nextProto == null) {
	// System.out.println("Invalid; try again.");
	// continue;
	// }
	//
	// PacketContent nextContent = null;
	// switch (nextProto) {
	// case IPV4:
	// nextContent = new Ipv4Header();
	// break;
	// case IPV6:
	// nextContent = new Ipv6Header();
	// break;
	// case UDP:
	// nextContent = new UdpHeader();
	// break;
	// case TCP:
	// nextContent = new TcpHeader();
	// break;
	// case ICMPV4:
	// nextContent = new Icmpv4HeaderInfo();
	// break;
	// case ICMPV6:
	// nextContent = new Icmpv6HeaderInfo();
	// break;
	// case PAYLOAD:
	// nextContent = new Payload();
	// break;
	// case HOP_BY_HOP_EXT6HDR:
	// nextContent = new HopByHopExt6Header();
	// break;
	// case ROUTING_EXT6HDR:
	// nextContent = new RoutingExt6Header();
	// break;
	// case DESTINATION_OPTION_EXT6HDR:
	// nextContent = new DestinationOptionExt6Header();
	// break;
	// case FRAGMENT_EXT6HDR:
	// nextContent = new FragmentExt6Header();
	// break;
	// }
	//
	// nextContent.readFromStdIn(scanner);
	// packet.add(nextContent);
	// } while (true);
	//
	// /* Wrap up */
	// // Stack<PacketContent> currentPayload = new Stack<>();
	// // for (int x = packet.size() - 1; x >= 0; x--) {
	// // PacketContent previous = (x > 0) ? packet.get(x - 1) : null;
	// // PacketContent current = packet.get(x);
	// // current.postProcess(previous, currentPayload);
	// // currentPayload.add(current);
	// // }
	//
	// for (int x = packet.size() - 1; x >= 0; x--) {
	// PacketContent current = packet.get(x);
	// System.out.println(current.getClass().getName());
	// Integer currentIdx = x;
	//
	// // Integer previousIdx = (x - 1) >= 0 ? x - 1 : null;
	// // Integer nextIdx = (x + 1) < packet.size() ? x + 1 : null;
	// current.postProcess(packet, currentIdx);
	// }
	//
	// /* Output */
	// boolean success = false;
	// do {
	// String outputFile = scanner.readLine("Output filename",
	// "output.pkt");
	//
	// try {
	// FileOutputStream out = new FileOutputStream(outputFile);
	// try {
	// for (PacketContent content : packet) {
	// out.write(content.toWire());
	// }
	// success = true;
	// } finally {
	// out.close();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// System.out.println("Try again: ");
	// }
	// } while (!success);
	// }

}
