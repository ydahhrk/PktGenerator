package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.auto.Util;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

/**
 * An entry in a {@link MainMenu}.
 */
public abstract class MainMenuEntry implements MainMenuPrintable {

	/** Short label used to index this entry. */
	private String shortName;
	/** More descriptive and human-recognizable name of the entry. */
	private String fullName;

	protected MainMenuEntry(String shortName, String fullName) {
		this.shortName = shortName;
		this.fullName = fullName;
	}

	@Override
	public void print(int tabs) {
		Util.printTabs(tabs);
		System.out.print("(");
		System.out.printf("%7s", shortName);
		System.out.print(") ");
		System.out.println(fullName);
	}
	
	public String getShortName() {
		return shortName;
	}

	public abstract void execute(Parser parser, FieldScanner scanner, Fragment frag);

}