package io.fabianterhorst.floppy.sample;

import java.util.Arrays;

/**
 * Created by fabianterhorst on 12.06.17.
 */

public class PersonArg extends Person {
    public PersonArg(String name) {
        super();
        setName("changed" + name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || PersonArg.class != o.getClass()) return false;

        PersonArg person = (PersonArg) o;

        if (getAge() != person.getAge()) return false;
        if (!Arrays.equals(getBikes(), person.getBikes())) return false;
        if (getName() != null ? !getName().equals(person.getName()) : person.getName() != null)
            return false;
        //noinspection RedundantIfStatement
        if (getPhoneNumbers() != null
                ? !getPhoneNumbers().equals(person.getPhoneNumbers())
                : person.getPhoneNumbers() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
