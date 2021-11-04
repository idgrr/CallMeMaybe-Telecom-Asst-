package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CALLED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTEREST;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Age;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.IsCalled;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.interests.Interest;
import seedu.address.model.person.interests.InterestsList;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_CALLED + "CALLED]"
            + "[" + PREFIX_GENDER + "GENDER] "
            + "[" + PREFIX_AGE + "AGE] "
            + "[" + PREFIX_INTEREST + "(optional index) INTEREST]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_GENDER + "M "
            + PREFIX_CALLED + "false"
            + PREFIX_INTEREST + "[1] software engineering";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_INVALID_INTERESTS_INDEX = "The specified interestsList index is invalid.";
    public static final String MESSAGE_DUPLICATE_INTEREST = "The specified interest already exists in the list.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.getName().equals(editedPerson.getName()) || !personToEdit.getPhone().equals(
                editedPerson.getPhone()) || !personToEdit.getEmail().equals(editedPerson.getEmail())) {

            if (model.hasPerson(editedPerson)) {
                throw new CommandException(MESSAGE_DUPLICATE_PERSON);
            } else {
                model.setPerson(personToEdit, editedPerson);
                model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            }
        } else {
            model.setPerson(personToEdit, editedPerson);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        }

        DisplayCommand displayCommand = new DisplayCommand(Index.fromOneBased(index.getOneBased()));
        displayCommand.execute(model);

        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        IsCalled updatedIsCalled = editPersonDescriptor.getIsCalled().orElse(personToEdit.getIsCalled());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Gender updatedGender = editPersonDescriptor.getGender().orElse(personToEdit.getGender());
        Age updatedAge = editPersonDescriptor.getAge().orElse(personToEdit.getAge());

        InterestsList newInterests = editPersonDescriptor.getInterests().orElse(null);
        if (newInterests != null) {
            this.editInterestList(newInterests, personToEdit.getInterests());
        }
        InterestsList updatedInterests = personToEdit.getInterests();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedIsCalled, updatedAddress,
                updatedGender, updatedAge, updatedInterests);
    }

    /**
     * Edits the {@code InterestsList} attribute of {@code personToEdit} based on user input command.
     */
    public void editInterestList(InterestsList newList, InterestsList currentList) throws CommandException {
        for (Interest i : newList.getAllInterests()) {
            String s = i.toString();
            this.editSpecifiedInterest(s, currentList);
        }
    }

    private void editSpecifiedInterest(String s, InterestsList currentList) throws CommandException {
        if (s.substring(0, 1).equals("(")) {
            String pos = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
            int index = Integer.parseInt(pos) - 1;

            if (index < currentList.size()) {
                Interest interest = new Interest(s.substring(s.indexOf(")") + 2).trim());
                checkDuplicate(currentList, interest);
                currentList.setInterest(interest, index);
            } else {
                throw new CommandException(MESSAGE_INVALID_INTERESTS_INDEX);
            }

        } else {
            Interest interest = new Interest(s);
            checkDuplicate(currentList, interest);
            currentList.addInterest(interest);
        }
    }

    private void checkDuplicate(InterestsList lst, Interest interest) throws CommandException {
        if (lst.checkDuplicate(interest)) {
            throw new CommandException(MESSAGE_DUPLICATE_INTEREST);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editPersonDescriptor.equals(e.editPersonDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private IsCalled isCalled;
        private Address address;
        private Gender gender;
        private Age age;
        private InterestsList interests;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setIsCalled(toCopy.isCalled);
            setAddress(toCopy.address);
            setGender(toCopy.gender);
            setAge(toCopy.age);
            setInterests(toCopy.interests);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, isCalled, address,
                    gender, age, interests);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public Optional<IsCalled> getIsCalled() {
            return Optional.ofNullable(isCalled);
        }

        public void setIsCalled(IsCalled isCalled) {
            this.isCalled = isCalled;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public Optional<Gender> getGender() {
            return Optional.ofNullable(gender);
        }

        public void setAge(Age age) {
            this.age = age;
        }

        public Optional<Age> getAge() {
            return Optional.ofNullable(age);
        }

        public void setInterests(InterestsList interests) {
            this.interests = interests;
        }

        public Optional<InterestsList> getInterests() {
            return Optional.ofNullable(interests);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getIsCalled().equals(e.getIsCalled())
                    && getAddress().equals(e.getAddress())
                    && getGender().equals(e.getGender())
                    && getAge().equals(e.getAge());
        }
    }
}
