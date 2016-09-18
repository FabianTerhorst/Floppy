# Floppy
Fast key value storage for java

```java
Floppy.init(getFilesDir().toString());

Disk disk = Floppy.disk();
disk.write("key", object);
disk.read("key");
```
