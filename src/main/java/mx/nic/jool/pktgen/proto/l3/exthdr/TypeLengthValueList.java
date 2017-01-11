package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import mx.nic.jool.pktgen.ByteArrayOutputStream;
import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.ScannableHeaderField;

/**
 * A bunch of {@link TypeLengthValue} options, listed in some header.
 */
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
					TypeLengthValue newTlv = new TypeLengthValue();
					scanner.scan(newTlv);
					list.add(newTlv);
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
						int tlvIndex = Integer.parseInt(nextChoice);
						TypeLengthValue tlv = list.get(tlvIndex);
						scanner.scan(tlv);
					} catch (NumberFormatException | IndexOutOfBoundsException e) {
						System.err.println("Sorry; I don't understand you. Please pick one of the options shown.");
					}
					break;
				}
			} while (true);
		}
	}

	/**
	 * Returns the number of bytes this list lengths when you've
	 * {@link #toWire()}'d it, not the number of TLVs in this list.
	 */
	public int getLength() {
		int length = 0;
		for (TypeLengthValue tlv : list)
			length += tlv.toWire().length;
		return length;
	}

	/**
	 * Serializes this TLV list into its binary representation, exactly as it
	 * would be represented in a network packet.
	 */
	public byte[] toWire() {
		@SuppressWarnings("resource")
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (TypeLengthValue tlv : list)
			out.write(tlv.toWire());

		return out.toByteArray();
	}

	/**
	 * Loads this TLV list from its {@link #toWire()} representation, being read
	 * from <code>in</code>.
	 */
	public void loadFromStream(InputStream in, int hdrExtLength) throws IOException {
		list.clear();

		int bytesLeft = 8 * hdrExtLength + 6;
		while (bytesLeft > 0) {
			TypeLengthValue tlv = TypeLengthValue.createFromInputStream(in);
			list.add(tlv);
			bytesLeft -= tlv.toWire().length;
		}

		if (bytesLeft != 0)
			throw new IOException("Options list does not match the 'header extension length' field.");
	}

	/**
	 * Reshapes this list to a random size and assigns random TLVs to its slots.
	 */
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
