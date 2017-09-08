package spring.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An error for an attempt to access an non-existing page
 * @author N. H. Weideman
 */

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Invalid page number")
public class PageException extends Exception {

	private static final long serialVersionUID = 100L;

	private final String message;

	public PageException(String message) {
		this.message = message;
	}
	

	@Override
	public String getMessage() {
		return message;
	}




}
