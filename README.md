# Directory Listing RESTful Service #

## Project Description ##
A small cross-platform program that exposes a RESTful interface on port 8080 and allows a client application to obtain the full directory listing of a given directory path on the local filesystem.

## Installation Requirements ##
This project requires the following tools to be installed in order to be built.

* [Gradle](https://gradle.org/)
* [Docker](https://www.docker.com/)

## Setup ##
Build the gradle wrapper

`gradle wrapper`

Use the wrapper to build your project

`./gradlew build`

Create the docker container

`sudo docker build -t directory-listing-rest-service .`

## Running ##
With Java

`java -jar ./build/libs/DirectoryListingREST-0.1.0.jar`

With Docker

`sudo docker run -v /:/local_filesystem -p 8080:8080 -it --rm directory-listing-rest-service`

## Usage ##
The project allows for users to obtain a directory listing of a specified path.
This is achieved by connecting to the host on port 8080.
For the rest of this documentation we assume we are connecting to localhost, but any local IP address can also be used.
The directory listing can be obtained as either HTML, or JSON.
To distinguish between these formats, specify the required format in the "accept" header of the HTTP request.
For example:

For obtaining the directory listing in JSON format:

* `curl --header 'accept: application/json' localhost:8080/list?dpath=/local_filesystem`

For obtaining the directory listing in HTML format:

* `curl --header 'accept: text/html' localhost:8080/list?dpath=/local_filesystem`

### HTML ###
Specifying HTML as the required format allows the service to be used through a web browser.
After running the RESTful service, the service can be used by visiting *http://localhost:8080/list?dpath=<path>* from a web browser, where <path> is the full path of the directory for which the directory listing is required. 
 
### JSON ###
Specifying JSON as the required format allows the service to be used from another application.
The supplied JSON format can be parsed to obtain the information of the directory the listing was requested for. 

## Features ##

### URL Parameters ###

* dpath --- The full path of the directory for which the listing is required
* page --- The page number of the directory listing (see pagination)
* psize --- The number of entries per page

### Stateless ###
The server does not store the state of any client, all information for obtaining specific resources is supplied within the parameters of the request URL.

### Cache ###
When a directory is accessed via the service, the result is stored within a cache.
A [WatchService](https://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchService.html) is also registered for this directory to determine the validity of the cache entry.
If the WatchService detects a modification in a cached directory, it is removed from the cache and the WatchService for the directory in question is cancled.

### Pagination ###
To deal with large directory listings, pagination is implemented.
The user can specify the number of resources returned at a time by adding the *psize* parameter to the URL.
The page itself can be specified with the *page* parameter.

### Discoverability ###
Each subdirectory within a directory listing contains a link to obtain a listing for the directory in question.
The links to the first, last, previous and next pages for a directory listing are returned both in the HTML/JSON and as a "link" header in the HTTP request.