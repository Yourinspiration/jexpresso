# JExpresso

## A Java Web Framework inspired by http://expressjs.com/

[![Build Status](https://travis-ci.org/Yourinspiration/jexpresso.svg?branch=master)](https://travis-ci.org/Yourinspiration/jexpresso)

JExpresso implements the fantastic API of the [expressjs framework](http://expressjs.com) in Java. The goal of JExpresso is to provide the same concise and intuitive interface of the expressjs framework in order to get a Java web application up and running with minimal effort and by just using simple POJOs (plain old Java objects).

Like expressjs JExpresso is a minimal not a full stack framework. That means that JExpresso takes care of the routing from HTTP request to your Java object methods. JExpresso provides a simple but powerful support of integration of middleware components in the style of Node.js Connect middleware.

## What means minimal effort?

Minimal effort means for us that you can develop and deploy a Java web application without the need of an application server (like Tomcat, Jetty, JBoss, Glassfish), configuration (like web.xml) and the knowledge of an complex API like the Java Servlet-API. We think it should be as easy as using Node.js and expressjs to develop a Java web application.

## Example

```java
import de.yourinspiration.jexpresso.JExpresso;

public class App {
  
  private void start() {
    final JExpresso app = new JExpresso();
    
    app.get("/hello", (req, res) -> {
      res.send("<h1>Hello World!</h1>");
    });
    
    app.listen(3333, () -> {
      System.out.println("Listening on port 3333");
    });
  }
  
  public static void main(String[] args) {
  	final App app = new App();
  	app.start();
  }
  
}
```

Compare to expressjs

```javascript
var express = require('express');
var app = express();

app.get('/hello', function(req, res){
  res.send('<h1>Hello World!</h1>');
});

app.listen(3333, function() {
    console.log('Listening on port 3333');
});
```

## Maven

Latest stable release:

```xml
<dependency>
  <groupId>de.yourinspiration</groupId>
  <artifactId>jexpresso</artifactId>
  <version>1.4.2</version>
</dependency>
```

## Gradle

```
compile 'de.yourinspiration:jexpresso:1.4.2'
```

## New features in 1.4.0

You can now "override" the default response transformers. That means that you can use your own transformer to
handle a specific content type. This is very cool when your language comes with its own library to transform
objects to JSON, like [groovy](http://groovy.codehaus.org/) or [golo](http://golo-lang.org/).

```
jexpresso.setTransformer(yourCustomerResponseTransformer);
```

