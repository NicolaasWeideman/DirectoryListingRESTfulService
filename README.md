# Directory Listing RESTful Service #

This project was created for the [coding challenge](https://github.com/entersekt/compsci_open-day_challenge_2017) hosted by [Entersekt](https://www.entersekt.com/).

## Project Description ##
This project is the implementation of a small cross-platform program that exposes a RESTful interface on port 8080 and allows a client application to obtain the full directory listing of a given directory path on the local filesystem.

## Installation Requirements ##
This project requires the following tools to be installed in order to be built and used.

* [Java](https://www.java.com/en/)
* [Gradle](https://gradle.org/)
* [Docker](https://www.docker.com/)

## Building the Project ##
Complete the following steps to build the project.

1. Build the gradle wrapper  
`gradle wrapper`
1. Use the wrapper to compile the project  
`./gradlew build`
1. Build the docker container  
`sudo docker build -t directory-listing-rest-service .`

## Running the Project ##
The project can either be run with Java, or Docker.

* Running with Java  
`java -jar ./build/libs/DirectoryListingREST-0.1.0.jar`

* Running with Docker  
`sudo docker run -v /:/local_filesystem -p 8080:8080 -it --rm directory-listing-rest-service`  
When running the project with Docker, it is required to mount the filesystem of the host in the docker container.
This is achieved with the `-v /:/local_filesystem` flag.
Specifically, it states that the directory `/` (the root directory) should be mounted at `/local_filesystem` in the docker container.
When using the restful service, the local file system can then be found in the directory `/local_filesystem`.
This also allows the user to allow only a subset of the local filesystem to be accessed.
For example using the flag `-v /home:/local_filesystem/home` will mount only the `/home` directory and therefore only this directory can be accessed.
It is also required that the port of the docker container is mapped to a port on the host.
Using the `-p 8080:8080` flag allows the RESTful service to be accessed from the outside.

## Consuming the REST Service ##
The project allows users to obtain a directory listing of a specified path.
This is achieved by connecting to the host on port 8080.
For the rest of this documentation we assume we are connecting to localhost, but any local IP address can also be used.
The directory listing can be obtained as either HTML, or JSON.
To distinguish between these formats, specify the required format in the "accept" header of the HTTP request.
For example:

For obtaining the directory listing in JSON format:

* `curl --header 'accept: application/json' 'localhost:8080/list?dpath=/local_filesystem'`

For obtaining the directory listing in HTML format (or use a web browser):

* `curl --header 'accept: text/html' 'localhost:8080/list?dpath=/local_filesystem'`

### HTML ###
If the directory listing is required in HTML format, the service can be used through a web browser.
After running the RESTful service, the service can be used by visiting `http://localhost:8080/list?dpath=/local_filesystem/<path>` from a web browser, where `<path>` is the full path of the directory for which the directory listing is required. 
 
### JSON ###
Specifying JSON as the required format allows the service to be used from another application.
The supplied JSON format can be parsed to obtain the information of the directory the listing was requested for. 

## Features ##

### URL Parameters ###

* dpath --- The full path of the directory for which the listing is required (this includes the directory at which the local filesystem is mounted in the docker container)
* page --- The page number of the directory listing (see pagination)
* psize --- The number of entries per page

### Stateless ###
The server does not store the state of any client, all information for obtaining specific resources is supplied within the parameters of the request URL.

### Cache ###
When a directory is accessed via the service, the result is stored within a cache.
This allows the result to be obtained from this cache when accessing the same directory again.
A [WatchService](https://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchService.html) is also registered for this directory to determine the validity of the cache entry.
If the WatchService detects a modification in a cached directory, it is removed from the cache and the WatchService for the directory in question is canceled.

### Pagination ###
To deal with large directory listings, pagination is implemented.
The user can specify the number of resources returned at a time by adding the `psize` parameter to the URL.
The page itself can be specified with the `page` parameter.
For example the request  
`http://localhost:8080/list?dpath=/local_filesystem/tmp&page=2&psize=2`  
returns the second page of the directory listing of the `tmp` directory on the local filesystem, when using two entries per page.

### Discoverability ###
Each subdirectory within a directory listing contains a link to obtain a listing for the subdirectory in question.
The links to the first, last, previous and next pages for a directory listing are returned both in the HTML/JSON and as a "link" header in the HTTP request.

## Notes ##

### Symbolic Links ###
A [symbolic link](https://en.wikipedia.org/wiki/Symbolic_link) is a file that contains a reference to another file.
As this reference is defined from within the local filesystem, it may become invalid when the local filesystem is mounted with Docker.
Moreover, allowing symbolic links may allow a user to attempt to access a directory that was not mounted (see Running with Docker).
For this reason, symbolic links are not supported in this project.
