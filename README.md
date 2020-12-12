# demonstration of strange 500 issue

To reproduce the error
1. Run the micronaut application contained within the project
2. Navigate to [http://localhost:8080/api/apple](http://localhost:8080/api/apple) in a webbrowser
3. Open the developer console and go to the "Network" tab to see requests


The issue is that at least one of the HTTP requests comes back with a 500 status code. This is related to the use of
a `ClientCredentialsHttpClientFilter`. On the server, an exception is thrown by the filter.

