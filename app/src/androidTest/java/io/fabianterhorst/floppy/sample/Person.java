package io.fabianterhorst.floppy.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * Created by fabianterhorst on 12.06.17.
 */

public class Person {
    private String mName;
    private int mAge;
    private List<String> mPhoneNumbers;
    private String[] mBikes;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public List<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        mPhoneNumbers = phoneNumbers;
    }

    public String[] getBikes() {
        return mBikes;
    }

    public void setBikes(String[] bikes) {
        mBikes = bikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Person.class != o.getClass()) return false;
        Person person = (Person) o;
        return mAge == person.mAge && Arrays.equals(mBikes, person.mBikes) && (mName != null ? mName.equals(person.mName) : person.mName == null && (mPhoneNumbers != null ? mPhoneNumbers.equals(person.mPhoneNumbers) : person.mPhoneNumbers == null));
    }

    @Override
    public int hashCode() {
        int result = mName != null ? mName.hashCode() : 0;
        result = 31 * result + mAge;
        result = 31 * result + (mPhoneNumbers != null ? mPhoneNumbers.hashCode() : 0);
        result = 31 * result + (mBikes != null ? Arrays.hashCode(mBikes) : 0);
        return result;
    }

    public void readObject(BufferedSource source) throws IOException {
        mName = source.readUtf8(source.readInt());
        mAge = source.readInt();
        mPhoneNumbers = new ArrayList<>();
        for (int i = 0, length = source.readInt(); i < length; i++) {
            mPhoneNumbers.add(source.readUtf8(source.readInt()));
        }
        mBikes = new String[source.readInt()];
        for (int i = 0; i < mBikes.length; i++) {
            mBikes[i] = source.readUtf8(source.readInt());
        }
    }

    public void writeObject(BufferedSink sink) throws IOException {
        sink.writeInt(mName.length());
        sink.writeUtf8(mName);
        sink.writeInt(mAge);
        sink.writeInt(mPhoneNumbers.size());
        for (String string : mPhoneNumbers) {
            sink.writeInt(string.length());
            sink.writeUtf8(string);
        }
        sink.writeInt(mBikes.length);
        for (String string : mBikes) {
            sink.writeInt(string.length());
            sink.writeUtf8(string);
        }
    }
}
