package spring.directorylisting;

import java.util.LinkedList;

/**
 * A path for storing directory listing results.
 * @author N. H. Weideman
 */
public class DirectoryListingResult {

	private final String fullPath;
	public String getFullPath() {
		return fullPath;
	}

	private final LinkedList<DirectoryListingEntry> directoryEntries;
	public LinkedList<DirectoryListingEntry> getDirectoryEntries() {
		/* We copy the directory entries into a new linked list to preserve immutability. */
		return new LinkedList<DirectoryListingEntry>(directoryEntries);
	}

	private final int numEntries;

	public DirectoryListingResult(String fullPath, LinkedList<DirectoryListingEntry> directoryEntries) {
		this.fullPath = fullPath;
		this.directoryEntries = new LinkedList<DirectoryListingEntry>(directoryEntries);
		this.numEntries = directoryEntries.size();
	}
}
