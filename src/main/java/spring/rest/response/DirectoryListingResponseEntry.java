package spring.rest.response;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.nio.file.attribute.BasicFileAttributes;

import spring.directorylisting.DirectoryListingEntry;

/**
 * A class to store all the information associated with a response entry
 * @author N. H. Weideman
 */

public class DirectoryListingResponseEntry {

	private final String fileName;
	public String getFileName() {
		return fileName;
	}

	private final String link;
	public String getLink() {
		return link;
	}


	private final String creationTimeAttributeName = "Creation Time";
	private final String creationTime;

	private final String fileKeyAttributeName = "File Key";
	private final String fileKey;

	private final String isDirectoryAttributeName = "Is Directory";
	private final boolean isDirectory;
	public boolean getIsDirectory() {
		return isDirectory;
	}

	private final String isOtherAttributeName = "Is Other";
	private final boolean isOther;

	private final String isRegularFileAttributeName = "Is Regular File";
	private final boolean isRegularFile;

	private final String isSymbolicLinkAttributeName = "Is Symbolic Link";
	private final boolean isSymbolicLink;

	private final String lastAccessTimeAttributeName = "Last Access Time";
	private final String lastAccessTime;


	private final String lastModifiedTimeAttributeName = "Last Modified Time";
	private final String lastModifiedTime;

	private final String sizeAttributeName = "Size";
	private final long size;

	private final Map<String,String> attributes;
		public Set<Map.Entry<String, String>> getAttributes() {
		return attributes.entrySet();
	}


	public DirectoryListingResponseEntry(String link, DirectoryListingEntry directoryListingEntry) {
		String fileName = directoryListingEntry.getFileName();
		BasicFileAttributes basicFileAttributes = directoryListingEntry.getFileAttributes();
		this.fileName = fileName;
		this.creationTime = basicFileAttributes.creationTime().toString();
		this.fileKey = basicFileAttributes.fileKey().toString();
		this.isDirectory = basicFileAttributes.isDirectory();
		this.isOther = basicFileAttributes.isOther();
		this.isRegularFile = basicFileAttributes.isRegularFile();
		this.isSymbolicLink = basicFileAttributes.isSymbolicLink();
		this.lastAccessTime = basicFileAttributes.lastAccessTime().toString();
		this.lastModifiedTime = basicFileAttributes.lastModifiedTime().toString();
		this.size = basicFileAttributes.size();
		this.link = link;

		attributes = new HashMap<String, String>();
		attributes.put(creationTimeAttributeName, creationTime);
		attributes.put(fileKeyAttributeName, fileKey);
		attributes.put(isDirectoryAttributeName, "" + isDirectory);
		attributes.put(isOtherAttributeName, "" + isOther);
		attributes.put(isRegularFileAttributeName, "" + isRegularFile);
		attributes.put(isSymbolicLinkAttributeName, "" + isSymbolicLink);
		attributes.put(lastAccessTimeAttributeName, lastAccessTime);
		attributes.put(lastModifiedTimeAttributeName, lastModifiedTime);
		attributes.put(sizeAttributeName, "" + size);

	}


}
