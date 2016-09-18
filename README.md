# Floppy
Fast key value storage for java

```java
Floppy.init(getFilesDir().toString());

Disk disk = Floppy.disk();
List<Integer> integers = new ArrayList<>();
disk.write("key", object);
disk.read("key");
```
