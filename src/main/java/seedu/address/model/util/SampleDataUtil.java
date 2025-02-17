package seedu.address.model.util;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.IsCalled;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.interests.InterestsList;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender("M"), new Age("32"),
                    new InterestsList()),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender("F"), new Age("11"),
                    new InterestsList()),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender(null), new Age(null),
                    new InterestsList()),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender(null), new Age(null),
                    new InterestsList()),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender(null), new Age(null),
                    new InterestsList()),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                    new IsCalled("FALSE"), new Address(null), new Gender(null), new Age(null),
                    new InterestsList())
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

}
