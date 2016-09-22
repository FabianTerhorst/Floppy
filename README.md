# Floppy
Fast key value storage for Java with much support for Android

<a href="http://www.methodscount.com/?lib=io.fabianterhorst%3AFloppy%3A0.0.8"><img src="https://img.shields.io/badge/Methods and size-core: 80 | deps: 2229 | 7 KB-e91e63.svg"/></a>


```java
Floppy.init(getFilesDir().toString());
//Without cache
Disk disk = Floppy.disk();
//With cache
Disk disk = Floppy.memoryDisk();
//Without cache, with custom name
Disk disk = Floppy.disk("custom disk");
//With cache, with custom name
Disk disk = Floppy.memoryDisk("custom memory disk");
disk.write("key", object);
disk.read("key");
disk.delete("key");
disk.deleteAll();
disk.setOnWriteListener("testKey", new OnWriteListener<String>() {
  @Override
  public void onWrite(String value) {
      //value = test
  }
});
disk.write("testKey", "test");
```
##Array support
The array support is internally a memory disk
```java
//Without custom name
Disk disk = Floppy.arrayDisk();
//With custom name
Disk disk = Floppy.arrayDisk("custom array disk");
disk.addOnChangeListener("items", new OnChangeListener<Item>() {
  @Override
  public void onChange(Item item) {
    //item changed
  }
});
Item item = new Item();
disk.addItem("items", item);
int index = 5;
disk.removeItem("items", index);
//To find the right item in the array (is used in change and remove item)
disk.addOnEqualListener("items", new OnEqualListener<Item>() {
  @Override
  public boolean isEqual(Item currentItem, Item newItem) {
    return currentItem.getId() == newItem.getId();
  }
});
disk.removeItem("items", item);
//This doesn´t need addOnEqualListener
disk.changeItem("items", new OnFindListener<Item>() {
    @Override
    public boolean isObject(Item item) {
      return item.getId() == 4;
    }
  }, new OnReadListener<Item>() {
    @Override
    public Object onRead(Item item) {
      item.setName("item4´s new Name")
      return item;
    }
});
```

gradle
```groovy
compile 'io.fabianterhorst:Floppy:0.2.0'
```

maven
```xml
<dependency>
  <groupId>io.fabianterhorst</groupId>
  <artifactId>Floppy</artifactId>
  <version>0.2.0</version>
  <type>pom</type>
</dependency>
```
