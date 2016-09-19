# Floppy
Fast key value storage for java

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
compile 'io.fabianterhorst:Floppy:0.0.1'
```

maven
```xml
<dependency>
  <groupId>io.fabianterhorst</groupId>
  <artifactId>Floppy</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```
