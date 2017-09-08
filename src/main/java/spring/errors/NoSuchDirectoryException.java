package spring.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * An error for an attempt to access an non-existing directory 
 * @author N. H. Weideman
 */

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Resource not found")
public class NoSuchDirectoryException extends Exception {

	private static final long serialVersionUID = 100L;

	private final String message;

	public NoSuchDirectoryException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
