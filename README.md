# PuccJ

PuccJ is a Java agent that will completely change your way to develop Java applications.
No matters if you is a web application, a desktop application, a game.
With PuccJ you can forget the redeploy pain.
Just save a Java file and PuccJ will compile and reload it for you while your application is running.
Take a look on the video on http://www.puccj.com to see PuccJ in action.
You can save a lot of time and money.
PuccJ is completely free and open source.


## Version
0.1 - Beta Version

PuccJ requires a lot complex work and testing to consider it a release version. Contribute if you like


## Usage

PuccJ is very simple to use.
It's a Java agent, so, start your java program with option `-javaagent:{puccjroot}/puccj.jar`

The only thing that you need to write is a *puccj.properties* file that describes your projects, like this:

```
puccj.projects = myapp

puccj.myapp.packages = com.puccj.myapp
puccj.myapp.sourcesDir = /myapp/src/main/java
```

PuccJ allows multiple project management using a comma to separate each project name `(es: puccj.properties = app1, app2, ...)`

By default, puccj.properties must be located in project resources directory, but if is necessary, is possible to specify a custom path as javaagent parameter:
`java -javaagent:puccj.jar?/path/to/puccj.properties`

For more informations, please visit http://www.puccj.com

Enjoy!!!