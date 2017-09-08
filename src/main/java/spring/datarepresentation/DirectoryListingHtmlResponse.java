package spring.datarepresentation;

import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import org.springframework.web.util.UriComponentsBuilder;

import spring.rest.response.DirectoryListingResponse;
import spring.rest.response.DirectoryListingResponseEntry;

/**
 * A class for building the HTML for a directory listing response.
 * @author N. H. Weideman
 */
public class DirectoryListingHtmlResponse {
	
	private final DirectoryListingResponse directoryListingResponse;

	public DirectoryListingHtmlResponse(DirectoryListingResponse directoryListingResponse) {
		this.directoryListingResponse = directoryListingResponse;
	}

	/**
	 * Generate the HTML representation of the directory listing response.
	 * @return The HTML representation of the directory listing response.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		String url = directoryListingResponse.getUrl();
		String fullPath = directoryListingResponse.getFullPath();	
		sb.append("<h>Directory listing for: \"" + createLink(url, fullPath) + "\"</h><br>\n");

		int numPages = directoryListingResponse.getNumPages();
		int pageNumber = directoryListingResponse.getPageNumber();
		String relationLinks = createRelationLinks(url, numPages, pageNumber);
		sb.append(relationLinks + "<br>\n");

		HtmlList<String> htmlDirectoryListingList = new HtmlList<String>();
		LinkedList<DirectoryListingResponseEntry> directoryEntries = directoryListingResponse.getDirectoryEntries();
		for (DirectoryListingResponseEntry directoryEntry : directoryEntries) {
			String fileName = directoryEntry.getFileName();
			String directoryEntryNameHtml;
			if (directoryEntry.getIsDirectory()) {
				/* Create a link to the directory. */
				String link = directoryEntry.getLink();
				directoryEntryNameHtml = createLink(link, fileName) + "<br>\n";
			} else {
				directoryEntryNameHtml = fileName + "<br>\n";
			}
			
			/* Adding the file attributes */
			Set<Map.Entry<String, String>> attributes = directoryEntry.getAttributes();
			HtmlList<String> htmlFileAttributesList = new HtmlList<String>();
			for (Map.Entry<String, String> attribute : attributes) {
				String attributeName = attribute.getKey();
				String attributeValue = attribute.getValue();

				htmlFileAttributesList.addItem(createFileAttribute(attributeName, attributeValue));

			}
		
			htmlDirectoryListingList.addItem(directoryEntryNameHtml + htmlFileAttributesList.toString());			
		}

		sb.append(htmlDirectoryListingList);
		sb.append(relationLinks + "<br>\n");

		return sb.toString();
	}

	/* Create the links to the first, previous (if applicable), next (if applicable) and last pages. */
	private String createRelationLinks(String url, int numPages, int pageNumber) {
		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);
		ucb.replaceQueryParam("page", 1);
		String firstLink = createLink(ucb.toUriString(), "first");

		ucb.replaceQueryParam("page", numPages);
		String lastLink = createLink(ucb.toUriString(), "last");

		String prevLink = "";
		if (pageNumber > 1) {
			ucb.replaceQueryParam("page", (pageNumber - 1));
			prevLink = createLink(ucb.toUriString(),"prev");
		}

		String nextLink = "";
		if (pageNumber < numPages) {
			ucb.replaceQueryParam("page", (pageNumber + 1));
			nextLink = createLink(ucb.toUriString(),"next");
		}

		return String.format("%s %s %s %s", firstLink, prevLink, nextLink, lastLink);
	}

	
	/* Create an HTML hyperlink element. */
	private String createLink(String link, String linkText) {
		return String.format("<a href=\"%s\">%s</a>", link, linkText);
	}


	/* Create the representation of a file attribute. */
	private String createFileAttribute(String attributeName, String attribute) {
		return String.format("%s: %s", attributeName, attribute);
	}
}
