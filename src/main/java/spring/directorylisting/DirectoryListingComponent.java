package spring.directorylisting;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import debugging.Debug;

/**
 * A class for obtaining directory listings of the file system.
 * @author N. H. Weideman
 */

@Component
public class DirectoryListingComponent {

	private final String CURRENT_DIRECTORY_NAME = ".";
	private final String PARENT_DIRECTORY_NAME = "..";
	

	private final FileSystem fileSystem;

	private DirectoryListingResultCache cache;


	public DirectoryListingComponent() throws IOException {
		this.fileSystem = FileSystems.getDefault();	
		this.cache = new DirectoryListingResultCache();
	}

	/**
	 * Gets a directory listing for a path
	 * @param path The path to obtain the directory listing for
	 * @return The directory listing result corresponding to the path
	 */
	public DirectoryListingResult getListing(String fullPathStr) throws IOException {
		Path fullPath = fileSystem.getPath(fullPathStr); 
		/* Obtaining the canonical path */
		File fullPathFile = fullPath.toFile().getCanonicalFile();
		fullPathStr = fullPathFile.getCanonicalPath();
		fullPath = new File(fullPathStr).toPath();

		/* Check if the directory has been cached */
		if (cache.contains(fullPathStr)) {
			Debug.debugln("Obtained " + fullPathStr + " from cache.");
			return cache.get(fullPathStr);
		}
	
		LinkedList<DirectoryListingEntry> directoryEntries = new LinkedList<DirectoryListingEntry>();

		/* Adding current directory */
		BasicFileAttributes basicCurrentFileAttributes = Files.readAttributes(fullPath, BasicFileAttributes.class);
		DirectoryListingEntry currentDirectoryEntry = new DirectoryListingEntry(CURRENT_DIRECTORY_NAME, basicCurrentFileAttributes);
		directoryEntries.add(currentDirectoryEntry);

		/* Adding parent directory */
		Path parent = fullPath.getParent();
		if (parent != null) {
			BasicFileAttributes basicParentFileAttributes = Files.readAttributes(parent, BasicFileAttributes.class);
			DirectoryListingEntry parentDirectoryEntry = new DirectoryListingEntry(PARENT_DIRECTORY_NAME, basicParentFileAttributes);
			directoryEntries.add(parentDirectoryEntry);
		}

		/* Adding directory entries. */
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fullPath);	
		for (Path path : directoryStream) {
			File file = path.toFile();
			/* Obtaining the canonical path */
			path = new File(file.getCanonicalPath()).toPath();
			String fileName = file.getName();
			/* Symlinks are broken in Docker, so we do not include them in our results. */
			if (!Files.isSymbolicLink(path)) {
				BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);	
				DirectoryListingEntry directoryListingEntry = new DirectoryListingEntry(fileName, basicFileAttributes);
				directoryEntries.add(directoryListingEntry);
			}
		}
		directoryStream.close();
		DirectoryListingResult directoryListingResult = new DirectoryListingResult(fullPathStr, directoryEntries);

		/* Add the directory to the cache */
		cache.put(fullPathStr, directoryListingResult);
		return directoryListingResult;
	}

}
