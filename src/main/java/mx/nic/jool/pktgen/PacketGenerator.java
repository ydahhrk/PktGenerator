package mx.nic.jool.pktgen;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.menu.MainMenu;
import mx.nic.jool.pktgen.parser.AutoParser;
import mx.nic.jool.pktgen.parser.ManualParser;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.TcpHeader;
import mx.nic.jool.pktgen.proto.optionsdata4.EndOptionList;
import mx.nic.jool.pktgen.proto.optionsdata4.Ipv4OptionHeader;
import mx.nic.jool.pktgen.proto.optionsdata4.NoOperation;

public class PacketGenerator {

	public static void main(String[] args) throws IOException {
		String mode = (args.length > 0) ? args[0] : "auto";

		switch (mode) {
		case "auto":
			handleAutoMode();
			break;
		case "manual":
			handleManualMode();
			break;
		case "edit":
			if (args.length < 2) {
				System.err.println("I need a packet file as second argument.");
				return;
			}
			handleEditMode(args[1]);
			break;
		case "random":
			handleRandomMode();
			break;
		default:
			System.err.println("Sorry; unknown operation mode. Try 'auto', 'manual', 'edit' or 'random'.");
			break;
		}
	}

	private static void handleMenuMode(Parser parser, Fragment frag) throws IOException {
		Packet packet = new Packet();

		MainMenu menu = new MainMenu();
		try (FieldScanner scanner = new FieldScanner(new Scanner(System.in))) {
			menu.handle(parser, scanner, frag);

			/* Wrap up */
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
	}

	private static void handleAutoMode() throws IOException {
		handleMenuMode(new AutoParser(), new Fragment());
	}

	private static void handleManualMode() throws IOException {
		handleMenuMode(new ManualParser(), new Fragment());
	}

	private static void handleEditMode(String string) throws IOException {
		handleMenuMode(new AutoParser(), Fragment.load(new File(string)));
	}

	private static void handleRandomMode() throws IOException {
		Packet packet = new Packet();
		Fragment fragment = new Fragment();

		packet.add(fragment);
		int payloadMaxLength = 1500;

		fragment.add(new Ipv4Header());
		payloadMaxLength -= Ipv4Header.LENGTH;
		payloadMaxLength -= maybeAddIpv4Options(fragment);

		fragment.add(new Icmpv4ErrorHeader());
		payloadMaxLength -= Icmpv4ErrorHeader.LENGTH;

		Ipv4Header internal = new Ipv4Header();
		internal.swapAddresses();
		fragment.add(internal);
		payloadMaxLength -= Ipv4Header.LENGTH;
		payloadMaxLength -= maybeAddIpv4Options(fragment);

		fragment.add(new TcpHeader());
		payloadMaxLength -= TcpHeader.LENGTH;

		Payload payload = new Payload();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		payload.setBytes(new byte[random.nextInt(payloadMaxLength)]);
		fragment.add(payload);

		packet.randomize();
		packet.postProcess();
		packet.export("random");
	}

	private static int maybeAddIpv4Options(Fragment fragment) {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (random.nextInt(10) > 4)
			return 0;

		int length = random.nextInt(10);
		for (int ihl = 0; ihl < length; ihl++) {
			fragment.add(createRandomIpv4Option(fragment, random));
			fragment.add(createRandomIpv4Option(fragment, random));
			fragment.add(createRandomIpv4Option(fragment, random));
			fragment.add(createRandomIpv4Option(fragment, random));
		}

		return 4 * length;
	}

	private static Ipv4OptionHeader createRandomIpv4Option(Fragment fragment, ThreadLocalRandom random) {
		return (random.nextInt(2) == 0) ? new NoOperation() : new EndOptionList();
	}
}
