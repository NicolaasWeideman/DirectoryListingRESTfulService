package spring.rest;

import java.util.concurrent.atomic.AtomicLong;
import java.util.LinkedList;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import spring.datarepresentation.DirectoryListingHtmlResponse;
import spring.directorylisting.DirectoryListingComponent;
import spring.directorylisting.DirectoryListingResult;
import spring.directorylisting.DirectoryListingEntry;
import spring.rest.response.DirectoryListingResponse;
import spring.rest.response.DirectoryListingResponseEntry;
import spring.errors.NoSuchDirectoryException;
import spring.errors.PageException;
import spring.errors.PageSizeException;
import debugging.Debug;

/**
 * The main controller for the REST service
 * @author N. H. Weideman
 */

@RestController
public class DirectoryListingController {

	private final DirectoryListingComponent directoryListingComponent;

	@Autowired
	public DirectoryListingController(DirectoryListingComponent directoryListingComponent) {
		this.directoryListingComponent = directoryListingComponent;
	}	

	/**
	 * Returns an HTML response for requesting a directory.
	 * @param dpath A URL query parameter for the path of the directory to list
	 * @param page A URL query parameter for the page number of the resource list to return
	 * @param psize A URL query parameter for the number of resources to show on the page
	 * @throws NoSuchDirectoryException If a non-existing directory is selected to be displayed
	 * @throws PageException If a non-existing page is selected to be displayed
	 * @throws PageSizeException If an invalid number of resources is selected to be displayed per page
	 * @return An HTML representation of the directory list
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET, headers="Accept=text/HTML", produces="text/HTML")
	public ResponseEntity<String> listHtml(HttpServletRequest request,
					@RequestParam(value="dpath", defaultValue="/") String dpath, 
					@RequestParam(value="page", defaultValue="1") int page, 
					@RequestParam(value="psize", defaultValue="2000") int psize) throws NoSuchDirectoryException, PageException, PageSizeException {
		String url = getFullUrl(request);
		try {
			DirectoryListingResponse directoryListingResponse = createResponse(url, dpath, page, psize);
			
			DirectoryListingHtmlResponse directoryListingHtmlResponse = new DirectoryListingHtmlResponse(directoryListingResponse);
			String htmlBodyString = directoryListingHtmlResponse.toString();
			ResponseEntity<String> responseEntity = createLinkHeader(htmlBodyString, url, directoryListingResponse);
						
			return responseEntity;
		} catch (IOException ioe) {
			Debug.debugStackTrace(ioe);
			throw new NoSuchDirectoryException("Directory at " + dpath + " not found");
		}
	}

	/**
	 * Returns an JSON response for requesting a directory.
	 * @param dpath A URL query parameter for the path of the directory to list
	 * @param page A URL query parameter for the page number of the resource list to return
	 * @param psize A URL query parameter for the number of resources to show on the page
	 * @throws NoSuchDirectoryException If a non-existing directory is selected to be displayed
	 * @throws PageException If a non-existing page is selected to be displayed
	 * @throws PageSizeException If an invalid number of resources is selected to be displayed per page
	 * @return An JSON representation of the directory list
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET, headers="Accept=application/json", produces="application/json")
	public ResponseEntity<DirectoryListingResponse> listJson(HttpServletRequest request,
					@RequestParam(value="dpath", defaultValue="/") String dpath,
					@RequestParam(value="page", defaultValue="1") int page,
					@RequestParam(value="psize", defaultValue="2000") int psize) throws NoSuchDirectoryException, PageException, PageSizeException {
		String url = getFullUrl(request);	
		try {
			DirectoryListingResponse directoryListingResponse = createResponse(url, dpath, page, psize);
			ResponseEntity<DirectoryListingResponse> responseEntity = new ResponseEntity<DirectoryListingResponse>(directoryListingResponse, HttpStatus.OK);
			return responseEntity;
		} catch (IOException ioe) {
			Debug.debugStackTrace(ioe);
			throw new NoSuchDirectoryException("Directory at " + dpath + " not found");
		}
	}

	/* Creates a response entity with the link headers for the first, previous (if applicable), next (if applicable) and last page added  */
	private <T> ResponseEntity<T> createLinkHeader(T body, String url, DirectoryListingResponse directoryListingResponse) {
		String relationLinks = createRelationLinks(directoryListingResponse);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Link", relationLinks);
		ResponseEntity<T> responseEntity = new ResponseEntity<T>(body, headers, HttpStatus.OK);
			
		return responseEntity;
	}

	/* Creates the links to the other pages for the "Link" header */
	private String createRelationLinks(DirectoryListingResponse directoryListingResponse) {
		StringBuilder sb = new StringBuilder();
			
		String firstLink = directoryListingResponse.getFirstLink();
		sb.append(String.format("<%s>; rel=\"first\"", firstLink));

		String lastLink = directoryListingResponse.getLastLink();
		sb.append(String.format(", <%s>; rel=\"last\"", lastLink));

		String prevLink = directoryListingResponse.getPrevLink();
		if (!prevLink.isEmpty()) {
			sb.append(String.format(", <%s>; rel=\"prev\"", prevLink));
		}

		String nextLink = directoryListingResponse.getNextLink();
		if (!nextLink.isEmpty()) {
			sb.append(String.format(", <%s>; rel=\"next\"", nextLink));
		}

		return sb.toString();
	}

	/**
	 * Handles the error of trying to access a non-existing directory
	 * @param e The exception for the error
	 * @return A response entity for the error
	 */
	@ExceptionHandler(NoSuchDirectoryException.class)
	public ResponseEntity<String> noSuchDirectory(NoSuchDirectoryException e) {
		String errorMessage = e.getMessage();

		Debug.debugStackTrace(e);

		return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles the error of trying to access a non-existing page number 
	 * @param e The exception for the error
	 * @return A response entity for the error
	 */
	@ExceptionHandler(PageException.class)
	public ResponseEntity<String> pageError(PageException e) {
		String errorMessage = e.getMessage();

		Debug.debugStackTrace(e);

		return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
	}


	/**
	 * Handles the error of trying to access a page while giving an invalid page size 
	 * @param e The exception for the error
	 * @return A response entity for the error
	 */
	@ExceptionHandler(PageSizeException.class)
	public ResponseEntity<String> sizeError(PageSizeException e) {
		String errorMessage = e.getMessage();

		Debug.debugStackTrace(e);

		return new ResponseEntity<String>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	/* Creates the directory listing response from the request parameters */
	private DirectoryListingResponse createResponse(String url, String dpath, int page, int pageSize) throws IOException, PageException, PageSizeException {
		DirectoryListingResult directoryListingResult = getDirectoryListing(dpath);
		LinkedList<DirectoryListingEntry> directoryListingEntries = directoryListingResult.getDirectoryEntries();
		LinkedList<DirectoryListingEntry> paginatedDirectoryListingEntries = paginate(directoryListingEntries, page, pageSize);
		int totalNumEntries = directoryListingEntries.size();
		int numPages = getNumPages(totalNumEntries, pageSize);
		String firstLink = createFirstLink(url);
		String lastLink = createLastLink(url, numPages);
		String prevLink = createPrevLink(url, page);
		String nextLink = createNextLink(url, page, numPages);
		DirectoryListingResponse directoryListingResponse = new DirectoryListingResponse(url,
						dpath,
						numPages,
						page,
						pageSize,
						firstLink,
						lastLink,
						prevLink,
						nextLink,
						paginatedDirectoryListingEntries);
		return directoryListingResponse;

	}

	/* Creates the URL to the first page */
	private String createFirstLink(String url) {
		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);
		
		ucb.replaceQueryParam("page", 1);
		String firstLink = ucb.toUriString();
		return ucb.toUriString();
	}

	/* Creates the URL to the last page */
	private String createLastLink(String url, int numPages) {
		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);

		ucb.replaceQueryParam("page", numPages);
		String lastLink = ucb.toUriString();
		return lastLink;
	}

	/* Creates the link to the previous page (if applicable) */
	private String createPrevLink(String url, int pageNumber) {
		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);

		String prevLink = "";
		if (pageNumber > 1) {
			ucb.replaceQueryParam("page", (pageNumber - 1));
			prevLink = ucb.toUriString();
		}
		return prevLink;
	}

	/* Creates the link to the next page (if applicable) */
	private String createNextLink(String url, int pageNumber, int numPages) {
		UriComponentsBuilder ucb = UriComponentsBuilder.fromHttpUrl(url);

		String nextLink = "";
		if (pageNumber < numPages) {
			ucb.replaceQueryParam("page", (pageNumber + 1));
			nextLink = ucb.toUriString();
		}
		return nextLink;
	}

	/* A wrapper function for obtaining the directory listing */
	private DirectoryListingResult getDirectoryListing(String dpath) throws IOException {
		return directoryListingComponent.getListing(dpath);
	}

	/* Paginates the directory entries according to the page number and page size */
	private LinkedList<DirectoryListingEntry> paginate(LinkedList<DirectoryListingEntry> directoryListingEntries, int page, int pageSize) throws PageException, PageSizeException {
		if (page < 1) {
			throw new PageException("Page " + page + " does not exist. (Minimum page: 1)");
		}
		if (pageSize <= 0) {
			throw new PageSizeException("Page sizes must have value >= 1.");
		}
		int numEntries = directoryListingEntries.size();
		int numPages = getNumPages(numEntries, pageSize);
		int startIndex = getPaginationLowerIndex(numEntries, page, pageSize);
		int endIndex = getPaginationUpperIndex(numEntries, page, pageSize);
		
		if (page > numPages) {
			throw new PageException("Page " + page + " does not exist. (Maximum page: " + numPages + ")");
		}
		
		
		return new LinkedList<DirectoryListingEntry>(directoryListingEntries.subList(startIndex, endIndex));
	}

	/* Returns the index for the resource to start pagination from */
	private int getPaginationLowerIndex(int numEntries, int page, int pageSize) {
		int startIndex = (page - 1) * pageSize;
		return startIndex;
	}

	/* Returns the index for the resource to stop pagination at */
	private int getPaginationUpperIndex(int numEntries, int page, int pageSize) {
		int endIndex = getPaginationLowerIndex(numEntries, page, pageSize) + pageSize;
		if (endIndex > numEntries) {
			endIndex = numEntries;
		}
		return endIndex;
	}

	/* Returns the total number of pages */
	private int getNumPages(int numEntries, int pageSize) {
		int numPages = (int) Math.ceil((double)numEntries / (double)pageSize);
		return numPages;
	}

	/* Constructs and returns the full URL from a request */
	private String getFullUrl(HttpServletRequest request) {
		String url;
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString == null) {
			url = requestURL.toString();
		} else {
			url = requestURL.append('?').append(queryString).toString();
		}

		return url;
	}
}
