package mx.nic.jool.pktgen.proto.l3.exthdr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.PacketUtils;
import mx.nic.jool.pktgen.pojo.Fragment;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.optionsdata6.OptionDataTypes;
import mx.nic.jool.pktgen.proto.optionsdata6.Pad1;
import mx.nic.jool.pktgen.proto.optionsdata6.PadN;
import mx.nic.jool.pktgen.proto.optionsdata6.TypeLengthValue;

public class HopByHopExt6Header extends Extension6Header {

	private Integer nextHeader;
	private Integer hdrExtLength;
	private List<TypeLengthValue> tlvList;

	@Override
	public void readFromStdIn(FieldScanner scanner) {
		/* The number 2 it's this header is "2 octects long" by default. */
		int octectsLength = 2;
		boolean autoPadding;
		nextHeader = scanner.readProtocol("Next Header");
		hdrExtLength = scanner.readInteger("Header Extension Length");
		tlvList = new ArrayList<>();

		OptionDataTypes optionType;
		do {
			// TODO english
			System.out.println("Tamaño actual de la cabecera: " + octectsLength + " bytes.");
			System.out.println("Se recomienda un tamaño de 8, 16 o 32 bytes.");

			Integer nextOptionInt = scanner.readOptionDataType("Next Option Data Type", "exit");

			if (nextOptionInt == null)
				break;

			optionType = OptionDataTypes.fromInt(nextOptionInt);

			if (optionType == null) {
				System.out.println("Invalid; try again.");
				continue;
			}

			TypeLengthValue typeLengthValue = null;
			switch (optionType) {
			case PAD1:
				typeLengthValue = new Pad1();
				break;
			case PADN:
				typeLengthValue = new PadN();
				break;
			}

			typeLengthValue.readFromStdIn(scanner);
			tlvList.add(typeLengthValue);

			do {
				try {
					octectsLength += typeLengthValue.toWire().length;
					break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (true);

		} while (true);

		boolean validLength = (octectsLength == 8) || (octectsLength == 16) || (octectsLength == 32);

		if (!validLength) {
			System.err.println("The length of this header is invalid.");
			System.err.println("Do you want to auto-fix it?");
			autoPadding = scanner.readBoolean("autoPadding", false);
			if (autoPadding) {
				autoPadding(octectsLength);
			} else {
				System.err.println("Don't know what could happen if you send this dpkt.");
			}
		}

	}

	private void autoPadding(int actualHdrLength) {
		int pad;
		int remainder = actualHdrLength % 8;
		if (remainder != 0) {
			pad = 8 - remainder;
			if (pad == 1) {
				addAutoPad1();
			} else {
				addAutoPadN(pad);
			}
		}

		// if (actualHdrLength < 8) {
		// pad = 8 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else if (actualHdrLength < 16) {
		// pad = 16 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else if (actualHdrLength < 32) {
		// pad = 32 - actualHdrLength;
		// if (pad == 1) {
		// addAutoPad1();
		// } else {
		// addAutoPadN(pad);
		// }
		// } else {
		// System.err.println("Warning: The number of bytes exceed the RFC 2460
		// recommendations. \n"
		// + "I won't add padding.");
		// }
	}

	private void addAutoPadN(int numberOfPadding) {
		TypeLengthValue padN;
		/*
		 * The number 2 it's because PadN is "2 octects long" by default
		 * (padding options).
		 */
		padN = new PadN(numberOfPadding - 2);
		tlvList.add(padN);
	}

	private void addAutoPad1() {
		TypeLengthValue pad1;
		pad1 = new Pad1();
		tlvList.add(pad1);
	}

	@Override
	public void postProcess(Packet packet, Fragment fragment) throws IOException {
		if (nextHeader == null) {
			nextHeader = fragment.getNextHdr(packet, this);
		}

		if (hdrExtLength == null) {
			/* The number 2 it's this header is "2 octects long" by default. */
			int length = 2;
			for (TypeLengthValue content : tlvList) {
				length += content.toWire().length;
			}
			hdrExtLength = (length / 8) > 0 ? (length / 8) - 1 : 0;

			if (length % 8 != 0) {
				System.err.println("Warning: I'm a \"Hop by Hop\" header and my length isn't a multiple of 8. ");
			}
			/*
			 * TODO: verificar que el tamaño de esta cabecera sea de 32 octetos,
			 * 16, u 8 octetos. en caso de que no cumpla con alguno de esos
			 * tamaños de octetos retornar un warning o ajustarlo con "paddings"
			 */
		}

		PacketContent previous = fragment.getPrevious(this);
		if ((previous == null) || !(previous instanceof Ipv6Header)) {
			System.err.println("Warning: I'm a \"Hop by Hop\" header and my previous content isn't an IPv6 header.");
			System.err.println("(RFC 2460 # 4.1)");
		}

	}

	@Override
	public PacketContent createClone() {
		HopByHopExt6Header result = new HopByHopExt6Header();

		result.nextHeader = nextHeader;
		result.hdrExtLength = hdrExtLength;
		result.tlvList = tlvList;

		return result;
	}

	@Override
	public byte[] toWire() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PacketUtils.write8BitInt(out, nextHeader);
		PacketUtils.write8BitInt(out, hdrExtLength);
		for (TypeLengthValue content : tlvList) {
			out.write(content.toWire());
		}

		return out.toByteArray();
	}

	@Override
	public String getShortName() {
		return "hhext";
	}

	@Override
	public void modifyFromStdIn(FieldScanner scanner) {
		readFromStdIn(scanner);
	}

	@Override
	public int getHdrIndex() {
		return 0;
	}

	@Override
	public PacketContent loadFromStream(InputStream in) throws IOException {
		// int[] header = Util.streamToArray(in, LENGTH);
		//
		// nextHeader = header[0];
		// hdrExtLength = header[1];
		// op
		//
		//
		// return PacketContentFactory.forNexthdr(nextHeader);
		throw new IllegalArgumentException("Sorry; HopByHop headers are not supported in load-from-file mode yet.");
	}

	@Override
	public void randomize() {
		throw new IllegalArgumentException("Sorry; HopByHop headers are not supported in random mode yet.");
	}

	@Override
	public void unsetLengths() {
		this.hdrExtLength = null;
	}
}
