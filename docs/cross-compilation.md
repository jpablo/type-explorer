
# Cross Compilation JVM / JS

For basic sbt instructions on multiple modules check:

https://www.scala-sbt.org/1.x/docs/Multi-Project.html


For cross compilation specific details check:

https://www.scala-js.org/doc/project/cross-build.html

and 

https://github.com/portable-scala/sbt-crossproject


In particular the layout of source and target folders is defined by

```
...
    .crossType(CrossType.Pure)
    .in(file("shared"))
```

Check build.sbt and the above links for more info
