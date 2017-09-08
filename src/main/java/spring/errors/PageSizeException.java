package spring.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An error for an attempt to access a resource while giving an invalid page size
 * @author N. H. Weideman
 */

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Invalid size")
public class PageSizeException extends Exception {

	private static final long serialVersionUID = 100L;

	private final String message;
	
	public PageSizeException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}




}
