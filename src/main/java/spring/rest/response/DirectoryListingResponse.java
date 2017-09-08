package spring.rest.response;

import java.util.LinkedList;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.springframework.web.util.UriComponentsBuilder;

import spring.directorylisting.DirectoryListingResult;
import spring.directorylisting.DirectoryListingEntry;

/**
 * A class to store all the information associated with a directory listing response
 * @author N. H. Weideman
 */

public class DirectoryListingResponse {

	private final String url;
	public String getUrl() {
		return url;
	}

	private final String fullPath;
	public String getFullPath() {
		return fullPath;
	}

	private final LinkedList<DirectoryListingResponseEntry> directoryResponseEntries;
	public LinkedList<DirectoryListingResponseEntry> getDirectoryEntries() {
		return new LinkedList<DirectoryListingResponseEntry>(directoryResponseEntries);
	}

	private final int numEntries;
	public int getNumEntries() {
		return numEntries;
	}

	private final int numPages;
	public int getNumPages() {
		return numPages;
	}

	private final int pageNumber;
	public int getPageNumber() {
		return pageNumber;
	}

	private final int pageSize;
	public int getPageSize() {
		return pageSize;
	}

	private final String firstLink;
	public String getFirstLink() {
		return firstLink;
	}

	private final String lastLink;
	public String getLastLink() {
		return lastLink;
	}

	private final String prevLink;
	public String getPrevLink() {
		return prevLink;
	}

	private final String nextLink;
	public String getNextLink() {
		return nextLink;
	}
	

	public DirectoryListingResponse(String url,
					String fullPath,
					int numPages,
					int pageNumber,
					int pageSize,
					String firstLink,
					String lastLink,
					String prevLink,
					String nextLink,
					LinkedList<DirectoryListingEntry> directoryEntries) throws IOException {
		this.url = url;
		this.fullPath = fullPath;
		this.numEntries = directoryEntries.size();
		this.numPages = numPages;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.firstLink = firstLink;
		this.lastLink = lastLink;
		this.prevLink = prevLink;
		this.nextLink = nextLink;

		/* Creating the links to the entries */
		this.directoryResponseEntries = new LinkedList<DirectoryListingResponseEntry>();
		for (DirectoryListingEntry directoryListingEntry : directoryEntries) {
			String fileName = directoryListingEntry.getFileName();
			UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);

			String link = "";
			if (directoryListingEntry.isDirectory()) {
				Path dpath =  Paths.get(fullPath, fileName);
				dpath = dpath.toRealPath();
				ucb.replaceQueryParam("dpath", dpath);
				ucb.replaceQueryParam("page", 1);

				link = ucb.toUriString();
			}
			DirectoryListingResponseEntry dlre = new DirectoryListingResponseEntry(link, directoryListingEntry);
			directoryResponseEntries.add(dlre);
		}
		
	}

}
