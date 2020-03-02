# Local testing

Run using the [Jetty Maven plugin](http://www.eclipse.org/jetty/documentation/9.4.x/jetty-maven-plugin.html).
```
mvn jetty:run
```
You can then direct your browser to [http://localhost:8080/hub.jsp]([http://localhost:8080/hub.jsp])


# App Engine Deploy

## Setup

 - [Install](https://cloud.google.com/sdk/) and initialize GCloud SDK. This will
 ```
    gcloud init
 ```
-  If this is your first time creating an app engine application
  ```
    gcloud appengine create
  ```

## Deploy

in `pom.xml` change `<deploy.projectId>` and 


```
mvn clean package appengine:deploy
```
