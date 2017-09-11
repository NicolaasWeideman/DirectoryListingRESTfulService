package spring.directorylisting;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.StandardWatchEventKinds;

import debugging.Debug;

/**
 * A class for caching Directory Listing Results.
 * @author N. H. Weideman
 */
public class DirectoryListingResultCache {

	private final ConcurrentHashMap<String, DirectoryListingResult> cacheMap;
	
	private final FileSystem fileSystem;
	private final WatchService watchService;

	/**
	 * Creates a new cache.
	 * @throws IOException If an I/O error occurs
	 */
	public DirectoryListingResultCache() throws IOException {
		this.cacheMap = new ConcurrentHashMap<String, DirectoryListingResult>();
		this.fileSystem = FileSystems.getDefault();
		this.watchService = fileSystem.newWatchService();

		/* Start a new thread to handle events from the watch service. */
		DirectoryListingResultValidityThread dlrvt = new DirectoryListingResultValidityThread();
		dlrvt.start();
		
	}

	/**
	 * Obtains the directory listing result for the cached path.
	 * @param path The path of the directory listing result
	 * @return The directory listing result if the path has been cached, NULL otherwise
	 */
	public DirectoryListingResult get(String fileStr) {
		return cacheMap.get(fileStr);
	}

	/**
	 * Adds a new entry to the cache.
	 * @param path The path of the directory listing result
	 * @param directoryListingResult The directory listing result to cache
	 * @throws IOException If an I/O error occurs
	 */
	public void put(String fileStr, DirectoryListingResult directoryListingResult) throws IOException {
		Path path = fileSystem.getPath(fileStr);
		/* Obtaining the canonical path */
		path = new File(path.toFile().getCanonicalPath()).toPath();

		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		Debug.debugln("Adding " + fileStr + " to cache.");
		cacheMap.put(fileStr, directoryListingResult);
	}

	/**
	 * Checks if a path has a cached entry
	 * @return True if the path has a cached entry, false otherwise
	 */
	public boolean contains(String fileStr) {
		return cacheMap.containsKey(fileStr);
	}

	/**
	 * A thread to handle events of the watch service.
	 */
	private class DirectoryListingResultValidityThread extends Thread {

		@Override
		public void run() {
			try {
				boolean watchDirectory = true;
				/* If an event occurs in a watched directory, remove it from the cache, as the cached entry has become invalid. */
				while (watchDirectory) {
					WatchKey watchKey = watchService.take();
					for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
 
						Path directory = (Path) watchKey.watchable();
						String directoryStr = directory.toString();
						if (cacheMap.containsKey(directoryStr)) {
							Debug.debugln("Removing " + directoryStr + " from cache.");
							cacheMap.remove(directoryStr);
							Debug.debugln("Canceling " + directory + " from watch service.");
							watchKey.cancel();
						}
					}	
				}
			} catch (InterruptedException ie) {
				
			}
		}

	
	}

}
