package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.ScannableHeaderField;
import mx.nic.jool.pktgen.auto.Util;

public class Inet6AddressList implements ScannableHeaderField {

	private List<Inet6Address> addresses = new ArrayList<>();

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		do {
			System.out.println("List so far:");
			if (addresses.isEmpty()) {
				System.out.println("\t[Empty]");
			} else {
				for (int i = 0; i < addresses.size(); i++)
					System.out.printf("\t(%7d) %s\n", i, addresses.get(i));
			}

			System.out.println("Other options:");
			System.out.printf("\t(%7s) %s\n", "add", "Add a new entry");
			System.out.printf("\t(%7s) %s\n", "del", "Delete entry");

			String nextChoice = scanner.readLine("Next", "exit");
			switch (nextChoice) {
			case "exit":
				return;
			case "add":
				addresses.add(scanner.readAddress6("New IPv6 address"));
				break;
			case "del":
				int entry = scanner.readInt("Address index");
				try {
					addresses.remove(entry);
				} catch (IndexOutOfBoundsException e) {
					System.err.println("That's not a valid address index. Let's try again.");
				}
				break;
			default:
				try {
					int number = Integer.parseInt(nextChoice);
					if (0 <= number && number < addresses.size())
						addresses.set(number, scanner.readAddress6("Address"));
					else
						throw new IndexOutOfBoundsException();
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					System.err.println("Sorry; I don't understand you. Please pick one of the options shown.");
				}
				break;
			}
		} while (true);
	}

	public int getLength() {
		return addresses.size();
	}

	public byte[] toWire() {
		@SuppressWarnings("resource")
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (Inet6Address ipv6 : addresses)
			out.write(ipv6.getAddress());

		return out.toByteArray();
	}

	public void loadFromStream(InputStream in, int hdrExtLength) throws IOException {
		addresses = new ArrayList<>(hdrExtLength / 2);
		for (int i = 0; i < addresses.size(); i++) {
			byte[] address = Util.streamToByteArray(in, 16);
			try {
				addresses.add((Inet6Address) InetAddress.getByAddress(address));
			} catch (UnknownHostException | ClassCastException e) {
				throw new RuntimeException("16-byte sequence was not recognized as an IPv6 address.", e);
			}
		}
	}

	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int addressCount = random.nextInt(10);

		addresses = new ArrayList<>(addressCount);
		for (int i = 0; i < addressCount; i++) {
			byte[] address = new byte[16];
			random.nextBytes(address);
			try {
				addresses.add((Inet6Address) InetAddress.getByAddress(address));
			} catch (UnknownHostException | ClassCastException e) {
				throw new RuntimeException("16-byte sequence was not recognized as an IPv6 address.", e);
			}
		}
	}

	@Override
	public void print() {
		if (addresses.isEmpty()) {
			System.out.println("[Empty]");
		} else {
			System.out.println();
			for (Inet6Address address : addresses)
				System.out.printf("\t\t%s\n", address);
		}
	}

}
