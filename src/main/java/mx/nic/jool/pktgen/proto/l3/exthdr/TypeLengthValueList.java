package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.ScannableHeaderField;

public class TypeLengthValueList implements ScannableHeaderField {

	private List<TypeLengthValue> list = new ArrayList<>();

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		while (true) {
			do {
				System.out.println("List so far:");
				if (list.isEmpty()) {
					System.out.println("\t[Empty]");
				} else {
					for (int i = 0; i < list.size(); i++)
						System.out.printf("\t(%7d) %s\n", i, list.get(i));
				}

				System.out.println("Other options:");
				System.out.printf("\t(%7s) %s\n", "add", "Add a new entry");
				System.out.printf("\t(%7s) %s\n", "rm", "Remove entry");

				String nextChoice = scanner.readLine("Next", "exit");
				switch (nextChoice) {
				case "exit":
					return;
				case "add":
					list.add(new TypeLengthValue(scanner));
					break;
				case "rm":
					int entry = scanner.readInt("TLV index");
					try {
						list.remove(entry);
					} catch (IndexOutOfBoundsException e) {
						System.err.println("That's not a valid TLV index. Try again maybe.");
					}
					break;
				default:
					try {
						int number = Integer.parseInt(nextChoice);
						TypeLengthValue tlv = list.get(number);
						tlv.loadFromStdIn(scanner);
					} catch (NumberFormatException | IndexOutOfBoundsException e) {
						System.err.println("Sorry; I don't understand you. Please pick one of the options shown.");
					}
					break;
				}
			} while (true);
		}
	}

	public int getLength() {
		int length = 0;
		for (TypeLengthValue tlv : list)
			length += tlv.toWire().length;
		return length;
	}

	public byte[] toWire() {
		@SuppressWarnings("resource")
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (TypeLengthValue tlv : list)
			out.write(tlv.toWire());

		return out.toByteArray();
	}

	public void loadFromStream(InputStream in, int hdrExtLength) throws IOException {
		list.clear();

		int bytesLeft = 8 * hdrExtLength + 6;
		while (bytesLeft > 0) {
			TypeLengthValue tlv = new TypeLengthValue(in);
			list.add(tlv);
			bytesLeft -= tlv.toWire().length;
		}

		if (bytesLeft != 0)
			throw new IOException("Options list does not match the 'header extension length' field.");
	}

	public void randomize() {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		int optionCount = random.nextInt(5);
		list = new ArrayList<>(optionCount);
		for (int i = 0; i < optionCount; i++)
			list.add(TypeLengthValue.createRandom());
	}

	@Override
	public void print() {
		if (list.isEmpty()) {
			System.out.println("[Empty]");
		} else {
			System.out.println();
			for (TypeLengthValue tlv : list)
				System.out.printf("\t\t%s\n", tlv);
		}
	}

}
