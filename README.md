# Floppy
Fast key value storage for java

<a href="http://www.methodscount.com/?lib=io.fabianterhorst%3AFloppy%3A0.0.8"><img src="https://img.shields.io/badge/Methods and size-core: 80 | deps: 2229 | 7 KB-e91e63.svg"/></a>


```java
Floppy.init(getFilesDir().toString());
//Without cache
Disk disk = Floppy.disk();
//With cache
Disk disk = Floppy.memoryDisk();
disk.write("key", object);
disk.read("key");
disk.setOnWriteListener("testKey", new OnWriteListener<String>() {
  @Override
  public void onWrite(String value) {
      //value = test
  }
});
disk.write("testKey", "test");
```
gradle
```groovy
compile 'io.fabianterhorst:Floppy:0.1.0'
```

maven
```xml
<dependency>
  <groupId>io.fabianterhorst</groupId>
  <artifactId>Floppy</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```
