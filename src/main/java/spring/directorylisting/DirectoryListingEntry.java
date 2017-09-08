package spring.directorylisting;

import java.nio.file.attribute.BasicFileAttributes;

/**
 * A class to store a directory entry
 * @author N. H. Weideman
 */
public class DirectoryListingEntry {

	private final String fileName;
	public String getFileName() {
		return fileName;
	}

	private final BasicFileAttributes fileAttributes;
	public BasicFileAttributes getFileAttributes() {
		return fileAttributes;
	}

	public boolean isDirectory() {
		return fileAttributes.isDirectory();
	}

	public DirectoryListingEntry(String fileName, BasicFileAttributes fileAttributes) {
		this.fileName = fileName;
		this.fileAttributes = fileAttributes;
	}

}
