package mx.nic.jool.pktgen.menu;

import java.util.LinkedList;
import java.util.List;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.parser.Parser;
import mx.nic.jool.pktgen.pojo.Fragment;

public class MenuSection extends MenuEntry {

	private List<MenuEntry> entries = new LinkedList<>();

	public MenuSection(String name) {
		super(name, null);
	}

	public void add(MenuEntry entry) {
		entries.add(entry);
	}

	@Override
	public void print(int tabs) {
		for (int i = 0; i < tabs; i++)
			System.out.print("\t");

		System.out.println(getShortName() + ":");
		for (MenuEntry entry : entries)
			entry.print(tabs + 1);
	}

	@Override
	public void execute(Parser parser, FieldScanner scanner, Fragment frag) {
		throw new IllegalArgumentException("Menu sections cannot be executed.");
	}

}
