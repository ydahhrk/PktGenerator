package mx.nic.jool.pktgen.pojo.shortcut;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.pojo.Header;

public interface Shortcut {

	public String getName();

	public void apply(Header header, FieldScanner scanner);

}
