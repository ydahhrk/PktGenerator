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
			Integer optionType = scanner.readInteger("Option Type", "finish");
			if (optionType == null)
				return;

			TypeLengthValue tlv = new TypeLengthValue(optionType);
			tlv.readFromStdIn(scanner);
			list.add(tlv);
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
