package mx.nic.jool.pktgen;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.menu.MainMenu;
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

public class PacketGenerator {

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);

		do {
			System.out.print("Mode (auto, edit or random) [auto]: ");
			String mode = scanner.nextLine().trim();
			if (mode.isEmpty())
				mode = "auto";

			switch (mode) {
			case "auto":
				handleAutoMode(scanner);
				return;
			case "edit":
				System.out.print("File: ");
				String file = scanner.nextLine().trim();
				handleEditMode(scanner, file);
				return;
			case "random":
				handleRandomMode();
				return;
			default:
				System.err.println("I don't understand you; try again.");
			}
		} while (true);
	}

	/**
	 * Modifies <code>frag</code> according to user input.
	 */
	private static void handleMenuMode(Scanner in, Fragment frag) throws IOException {
		MainMenu menu = new MainMenu();
		FieldScanner scanner = new FieldScanner(in);
		menu.handle(scanner, frag);

		/* Wrap up */
		Packet packet = new Packet();
		packet.add(frag);
		packet.postProcess();

		/* Output */
		boolean success = false;
		do {
			String outputFile = scanner.readLine("Output filename", "output");
			try {
				packet.export(outputFile);
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Try again: ");
			}
		} while (!success);
	}

	private static void handleAutoMode(Scanner scanner) throws IOException {
		handleMenuMode(scanner, new Fragment());
	}

	private static void handleEditMode(Scanner scanner, String string) throws IOException {
		handleMenuMode(scanner, Fragment.load(new File(string)));
	}

	private static void handleRandomMode() throws IOException {
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
				internal.swapAddresses();
				fragment.add(internal);
				maybeAddIpv6ExtensionHeaders(fragment, random);

			} else {
				fragment.add(new Icmpv4ErrorHeader());

				Ipv4Header internal = new Ipv4Header();
				internal.swapAddresses();
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
