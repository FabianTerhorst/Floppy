package io.fabianterhorst.floppy.sample;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fabianterhorst on 21.09.16.
 */

public class Person implements Externalizable {
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(mName);
        out.writeInt(mAge);
        out.writeObject(mPhoneNumbers);
        out.writeObject(mBikes);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mName = (String) in.readObject();
        mAge = in.readInt();
        mPhoneNumbers = (ArrayList<String>) in.readObject();
        mBikes = (String[]) in.readObject();
    }
}
