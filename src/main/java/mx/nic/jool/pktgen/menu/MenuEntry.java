package mx.nic.jool.pktgen.menu;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

public abstract class MenuEntry {

	private String shortName;
	private String fullName;

	protected MenuEntry(String shortName, String fullName) {
		this.shortName = shortName;
		this.fullName = fullName;
	}

	public void print(int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("\t");
		
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
