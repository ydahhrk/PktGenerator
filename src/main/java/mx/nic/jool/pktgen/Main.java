package mx.nic.jool.pktgen;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.nic.jool.pktgen.pojo.Header;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.Payload;
import mx.nic.jool.pktgen.pojo.shortcut.Shortcut;
import mx.nic.jool.pktgen.proto.l3.FragmentHeader;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l4.Icmpv4ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv4InfoHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6ErrorHeader;
import mx.nic.jool.pktgen.proto.l4.Icmpv6InfoHeader;
import mx.nic.jool.pktgen.proto.l4.TcpHeader;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;
import mx.nic.jool.pktgen.type.Field;

public class Main {

	private static final Pattern NAME_PATTERN = Pattern.compile("packet ([^:]+)(: .*)?");
	private static final Pattern BASIC_PATTERN = Pattern.compile( //
			"([0-9]+)\\s+(IPv6|IPv4|TCP|UDP|ICMPv6|Ping6|ICMPv4|Ping4|Payload|Padding|Fragment)([^#]*)(# .*)?" //
	);

	public static void main(String[] args) throws IOException, HeadlessException, UnsupportedFlavorException {
		String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

		Scanner scanner = new Scanner(data);
		while (scanner.hasNext())
			readPacket(scanner);
	}

	private static void readPacket(Scanner scanner) throws IOException {
		Packet packet = null;

		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("#"))
				continue;
			if (line.isEmpty())
				return;

			Matcher matcher = NAME_PATTERN.matcher(line);
			if (!matcher.matches()) {
				System.err.printf("Weird string when trying to parse packet name: '%s'\n", line);
				return;
			}

			String name = matcher.group(1);
			System.out.printf("Got packet name: %s\n", name);
			packet = new Packet(name);
			break;
		}

		if (packet == null)
			return;

		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if (line.startsWith("#"))
				continue;
			if (line.isEmpty())
				break;

			Matcher matcher = BASIC_PATTERN.matcher(line);
			if (!matcher.matches()) {
				System.err.printf("Weird string when trying to parse packet header: '%s'\n", line);
				return;
			}

			int length = Integer.valueOf(matcher.group(1));
			String headerType = matcher.group(2);
			String arguments = matcher.group(3);

			switch (headerType) {
			case "IPv6":
				validateLength(40, length, headerType);
				packet.add(handleHeader(new Ipv6Header(), arguments));
				break;
			case "IPv4":
				Ipv4Header hdr = handleHeader(new Ipv4Header(), arguments);
				Integer ihl = hdr.getIhl();
				validateLength((ihl != null) ? (4 * ihl) : 20, length, headerType);
				packet.add(hdr);
				break;
			case "TCP":
				validateLength(20, length, headerType);
				packet.add(handleHeader(new TcpHeader(), arguments));
				break;
			case "UDP":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new UdpHeader(), arguments));
				break;
			case "ICMPv6":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new Icmpv6ErrorHeader(), arguments));
				break;
			case "Ping6":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new Icmpv6InfoHeader(), arguments));
				break;
			case "ICMPv4":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new Icmpv4ErrorHeader(), arguments));
				break;
			case "Ping4":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new Icmpv4InfoHeader(), arguments));
				break;
			case "Payload":
				packet.add(handleHeader(Payload.monotonic(length), arguments));
				break;
			case "Padding":
				packet.add(Payload.zeroes(length));
				break;
			case "Fragment":
				validateLength(8, length, headerType);
				packet.add(handleHeader(new FragmentHeader(), arguments));
				break;
			}
		}

		packet.export();
	}

	private static void validateLength(int expected, int length, String what) {
		if (expected != length)
			System.err.printf("Warning: %s's length %d != %d.\n", what, length, expected);
	}

	private static <T extends Header> T handleHeader(T header, String arguments) {
		Field[] fields = header.getFields();
		Shortcut[] shortcuts = header.getShortcuts();

		String[] args = arguments.split(" ");
		for (String arg : args) {
			if (arg.isBlank())
				continue;

			String name;
			String value;

			int indexOfColon = arg.indexOf(':');
			if (indexOfColon > -1) {
				name = arg.substring(0, indexOfColon).trim();
				value = arg.substring(indexOfColon + 1).trim();
			} else {
				name = arg.trim();
				value = null;
			}

			boolean found = false;
			for (Field field : fields) {
				if (field.getName().equalsIgnoreCase(name)) {
					field.parse(value);
					found = true;
				}
			}
			for (Shortcut shortcut : shortcuts) {
				if (shortcut.getName().equalsIgnoreCase(name)) {
					shortcut.apply(header, value);
					found = true;
				}
			}
			if (!found)
				throw new IllegalArgumentException(header.getName() + "s have no field called '" + name + "'.");
		}

		return header;
	}

}
