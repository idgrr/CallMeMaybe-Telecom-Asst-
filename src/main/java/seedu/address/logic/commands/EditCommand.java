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

import java.util.ArrayList;
import java.util.Collections;
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
            + "Parameters: INDEX (must be a positive integer in the displayed list) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_CALLED + "CALLED]"
            + "[" + PREFIX_GENDER + "GENDER] "
            + "[" + PREFIX_AGE + "AGE] "
            + "[" + PREFIX_INTEREST + "INTEREST_TO_BE_ADDED]…\u200B"
            + "[" + PREFIX_INTEREST + "(INDEX) remove]…\u200B"
            + "[" + PREFIX_INTEREST + "(INDEX) INTEREST]…\u200B\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_GENDER + "M "
            + PREFIX_CALLED + "false "
            + PREFIX_INTEREST + "(1) software engineering "
            + PREFIX_INTEREST + "(2) remove";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_INVALID_INTERESTS_INDEX = "The specified interestsList index is invalid.";
    public static final String MESSAGE_DUPLICATE_INDEX = "You have specified the same index more than once.";
    public static final String MESSAGE_DUPLICATE_INTEREST_ARGUMENT = "You have duplicate interest arguments in "
            + "your command.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;
    private ArrayList<Integer> indexesToBeRemoved;
    private ArrayList<Interest> interestsToBeAdded;
    private ArrayList<Integer> listOfIndexes;
    private ArrayList<String> listOfArguments;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
        this.indexesToBeRemoved = new ArrayList<>();
        this.interestsToBeAdded = new ArrayList<>();
        this.listOfIndexes = new ArrayList<>();
        this.listOfArguments = new ArrayList<>();
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

        if (!personToEdit.equals(editedPerson)) {
            System.out.println("HELLO1");
            if (model.hasPerson(editedPerson)) {
                throw new CommandException(MESSAGE_DUPLICATE_PERSON);
            } else {
                model.setPerson(personToEdit, editedPerson);
            }
        } else if (editPersonDescriptor.getName().isEmpty() && editPersonDescriptor.getPhone().isEmpty()
                    && editPersonDescriptor.getEmail().isEmpty()) {

            checkEqualsCalled(personToEdit, editPersonDescriptor);
            checkEqualsAddress(personToEdit, editPersonDescriptor);
            checkEqualsGender(personToEdit, editPersonDescriptor);
            checkEqualsAge(personToEdit, editPersonDescriptor);

            model.setPerson(personToEdit, editedPerson);
        } else {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
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
        InterestsList interestsListCopy = personToEdit.getInterests().copyInterestsList();

        if (newInterests != null) {
            editInterestList(newInterests, interestsListCopy);
            removeSpecifiedInterests(interestsListCopy);
            addSpecifiedInterests(interestsListCopy);
        }
        InterestsList updatedInterests = interestsListCopy;

        return new Person(updatedName, updatedPhone, updatedEmail, updatedIsCalled, updatedAddress,
                updatedGender, updatedAge, updatedInterests);
    }

    /**
     * Edits the {@code InterestsList} attribute of {@code personToEdit} based on user input command.
     */
    public void editInterestList(InterestsList newList, InterestsList currentList) throws CommandException {
        emptyLists();

        for (Interest i : newList.getAllInterests()) {
            String s = i.toString();
            editSpecifiedInterest(s, currentList);
        }
    }

    private void editSpecifiedInterest(String s, InterestsList currentList) throws CommandException {
        if (s.substring(0, 1).equals("(")) {
            String pos = s.substring(s.indexOf("(") + 1, s.indexOf(")")).trim();
            String desc = s.substring(s.indexOf(")") + 1).trim();
            int index;

            try {
                index = Integer.parseInt(pos) - 1;
            } catch (NumberFormatException e) {
                throw new CommandException("The interestslist index provided is invalid.");
            }

            if (index >= currentList.size()) {
                throw new CommandException(MESSAGE_INVALID_INTERESTS_INDEX);
            }

            if (this.listOfIndexes.contains(index)) {
                throw new CommandException(MESSAGE_DUPLICATE_INDEX);
            }

            if (this.listOfArguments.contains(desc)) {
                throw new CommandException(MESSAGE_DUPLICATE_INTEREST_ARGUMENT);
            }

            this.listOfIndexes.add(index);

            if (desc.equals("remove")) {
                this.indexesToBeRemoved.add(index);
            } else {
                trySetInterest(currentList, desc, index);
                this.listOfArguments.add(desc);
            }

        } else {
            Interest interest = new Interest(s);

            if (this.listOfArguments.contains(s)) {
                throw new CommandException(MESSAGE_DUPLICATE_INTEREST_ARGUMENT);
            }

            this.listOfArguments.add(s);
            this.interestsToBeAdded.add(interest);
        }
    }

    private void trySetInterest(InterestsList currentList, String desc, int index) throws CommandException {
        try {
            currentList.setInterest(new Interest(desc), index);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private void tryAddInterest(InterestsList currentList, Interest interest) throws CommandException {
        try {
            currentList.addInterest(interest);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }
    }

    private void removeSpecifiedInterests(InterestsList currentList) {
        int count = 0;
        int len = this.indexesToBeRemoved.size();
        Collections.sort(this.indexesToBeRemoved);

        if (len > 0) {
            for (int i = 0; i < len; i++) {
                currentList.removeInterest(indexesToBeRemoved.get(i) - count);
                count++;
            }
        }
    }

    private void addSpecifiedInterests(InterestsList currentList) throws CommandException {
        for (int i = 0; i < interestsToBeAdded.size(); i++) {
            this.tryAddInterest(currentList, interestsToBeAdded.get(i));
        }
    }

    private void emptyLists() {
        this.listOfIndexes.clear();
        this.listOfArguments.clear();
        this.indexesToBeRemoved.clear();
        this.interestsToBeAdded.clear();
    }

    private void checkEqualsCalled(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {

        if (editPersonDescriptor.getIsCalled().isPresent()) {
            if (editPersonDescriptor.getIsCalled().get().equals(personToEdit.getIsCalled())) {
                throw new CommandException("Edited called status is already the same as the current called status!");
            }
        }
    }

    private void checkEqualsAddress(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        if (editPersonDescriptor.getAddress().isPresent()) {
            if (editPersonDescriptor.getAddress().get().equals(personToEdit.getAddress())) {
                throw new CommandException("Edited address is already the same as the current address!");
            }
        }
    }

    private void checkEqualsGender(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        if (editPersonDescriptor.getGender().isPresent()) {
            if (editPersonDescriptor.getGender().get().equals(personToEdit.getGender())) {
                throw new CommandException("Edited gender is already the same as the current gender!");
            }
        }
    }

    private void checkEqualsAge(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        if (editPersonDescriptor.getAge().isPresent()) {
            if (editPersonDescriptor.getAge().get().equals(personToEdit.getAge())) {
                throw new CommandException("Edited age is already the same as the current age!");
            }
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
