package mx.nic.jool.pktgen;

/**
 * A header field that is not a primitive and which should nevertheless be
 * parseable by {@link FieldScanner#scan(Object)}.
 */
public interface ScannableHeaderField {

	/** Requests modifications of this object to the user and applies them. */
	void readFromStdIn(FieldScanner fieldScanner);

	/** Prints the current state of this object in standard output. */
	void print();

}
