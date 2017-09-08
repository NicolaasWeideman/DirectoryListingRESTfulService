package spring.datarepresentation;

import java.util.LinkedList;

/**
 * A class for creating an HTML list element.
 * @author N. H. Weideman
 * @param <T> The type of Object to add to the list.
 */
public class HtmlList<T> {

	private final LinkedList<T> list;

	public HtmlList() {
		list = new LinkedList<T>();
	}

	
	/**
	 * Add an item to the HTML list.
	 * @param item The item to add.
	 */
	public void addItem(T item) {
		list.add(item);
	}

	/**
	 * Convert the list to it's HTML representation.
	 * @return The HTML representation of the list.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<ul><br>\n");

		for (T item : list) {
			sb.append("<li>" + item + "</li>\n");
		}

		sb.append("</ul><br>\n");

		return sb.toString();
	}
}
