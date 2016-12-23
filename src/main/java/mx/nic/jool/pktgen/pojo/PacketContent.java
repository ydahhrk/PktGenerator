package mx.nic.jool.pktgen.pojo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.annotation.HeaderField;
import mx.nic.jool.pktgen.enums.Layer;

public abstract class PacketContent {

	/**
	 * Reads this header from <code>scanner</code> by requesting each of its
	 * fields to the user one by one.
	 * 
	 * "Manual" mode.
	 */
	public abstract void readFromStdIn(FieldScanner scanner);

	/**
	 * Reads this header from <code>scanner</code> by printing it in standard
	 * output and asking the user which fields need to be modified.
	 * 
	 * "Auto" mode.
	 */
	public void modifyFromStdIn(FieldScanner scanner) {
		String fieldToModify;
		int annotationsLength = 0;
		Field[] fields = this.getClass().getDeclaredFields();

		if (fields.length == 0) {
			System.err.println("Nothing to change.");
			return;
		}

		do {
			annotationsLength = showFieldValues(fields, this);

			if (annotationsLength == 0) {
				System.err.println("Nothing to change.");
				return;
			}

			fieldToModify = scanner.readLine("Field", "exit");
			if (fieldToModify.equalsIgnoreCase("exit"))
				break;

			if (fieldToModify == null || fieldToModify.isEmpty())
				continue;

			for (Field field : fields) {
				if (!field.getName().equalsIgnoreCase(fieldToModify))
					continue;

				HeaderField annotation = field.getAnnotation(HeaderField.class);
				if (annotation == null)
					continue; /* We don't care about this field. */

				try {
					field.set(this, scanner.read(this, field));
				} catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
					throw new IllegalArgumentException("Strange & unlikely error condition; see below.", e);
				}
			}
		} while (true);
	}

	private int showFieldValues(Field[] fields, PacketContent obj) {
		int annotationsLength = 0;

		System.out.print(obj.getClass().getSimpleName());
		System.out.println(" Fields:");
		for (Field field : fields) {
			field.setAccessible(true);
			HeaderField annotation = field.getAnnotation(HeaderField.class);
			if (annotation == null)
				continue; /* No nos interesa este campo. */

			annotationsLength++;

			System.out.print("\t(");
			System.out.printf("%15s", field.getName());
			System.out.print(") ");

			Object fieldValue;
			try {
				fieldValue = field.get(obj);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Strange & unlikely error condition; see below.", e);
			}

			System.out.print(": ");
			System.out.println((fieldValue != null ? fieldValue : "(auto)"));
		}

		return annotationsLength;
	}

	/**
	 * Assigns random values to this header's fields, but tries hard to make it
	 * so the header is still reasonably valid.
	 * 
	 * "Random" mode.
	 */
	public abstract void randomize();

	/**
	 * Serializes this header into its binary representation, exactly as it
	 * would be represented in a network packet.
	 */
	public abstract byte[] toWire() throws IOException;

	/**
	 * Loads this header from its {@link #toWire()} representation, being read
	 * from <code>in</code>.
	 */
	public abstract PacketContent loadFromStream(InputStream in) throws IOException;

	/**
	 * Returns a short string uniquely identifing this header's protocol.
	 */
	public abstract String getShortName();

	/**
	 * Returns the identifier the IANA assigned to this header type.
	 * 
	 * The previous header identifies this header's type by having this number
	 * in its "nexthdr" (or, in IPv4's case, "protocol") field.
	 * 
	 * If the header type is not supposed to be previously nexthdr'd, this will
	 * return a negative number.
	 */
	public abstract int getHdrIndex();

	/**
	 * Returns the layer in the IP/TCP protocol stack this header belongs to.
	 */
	public abstract Layer getLayer();

	/**
	 * Automatically assigns a value to any fields the user left unset ("auto").
	 */
	public abstract void postProcess(Packet packet, Fragment fragment) throws IOException;

	/**
	 * Builds and returns a deep copy of this header.
	 * 
	 * TODO Review the deepness of the copy implementations.
	 */
	public abstract PacketContent createClone();

	public abstract void unsetChecksum();
	
	public abstract void unsetLengths();

}
