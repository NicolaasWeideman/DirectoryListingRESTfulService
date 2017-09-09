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
The project allows for users to obtain a directory listing of a specified path as either HTML, or JSON.
Specify the required format in the "accept" header of the HTTP request.
For example:
* *curl --header 'accept: application/json' localhost:8080/list?dpath=/local_filesystem* returns the directory listing of the root of the local file system (on the host machine) in JSON format
* *curl --header 'accept: text/html' localhost:8080/list?dpath=/local_filesystem* returns the directory listing of the root of the local file system (on the host machine) in HTML format

### HTML ###
Specifying HTML as the required format allows the service to be used through a web browser.
After running the RESTful service, the service can be used by visiting *http://<ip>:8080/list?dpath=<path>* from a web browser, where <ip> is the local IP address of the host machine and <path> is the full path of the directory for which the directory listing is required. 
 
### JSON ###
Specifying JSON as the required format allows the service to be used within another application.
The supplied JSON format can be parsed to obtain the information of the directory the listing was requested for. 

## Features ##

### Stateless ###
The server does not store the state of any client, all information for obtaining specific resources is supplied within the parameters of the request URL.

### Cache ###
When a directory is accessed via the service, the result is stored within a cache.
A [WatchService](https://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchService.html) is also registered for this directory to determine the validity of the cache entry.
If the WatchService detects a modification in the cached directory, it is removed from the cache and the WatchService for the directory in question is cancled.

### Pagination ###
To deal with large directory listings, pagination is implemented.
The user can specify the number of resources returned at a time by adding the *psize* parameter to the URL.
The page itself can be specified with the *page* parameter.

### Discoverability ###
Each subdirectory within a directory listing contains a link to obtain a listing for the directory in question.
The links to the first, last, previous and next pages for a directory listing are also returned.