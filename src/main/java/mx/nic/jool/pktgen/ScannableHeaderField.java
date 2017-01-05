package mx.nic.jool.pktgen;

public interface ScannableHeaderField {

	void readFromStdIn(FieldScanner fieldScanner);

	void print();

}
