package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.interests.Interest;
import seedu.address.model.person.interests.InterestsList;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    public void assertEditCommandSuccess(Person p1, Person p2, int index) {
        Person expectedPerson = p1;
        Person editedPerson = p2;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(Index.fromOneBased(index + 1), descriptor);
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, expectedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(index), expectedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().withAddress("THIS IS MY HOME")
                .withGender("M").withAge("102").withInterest("Blockchain", "Swimming").build();
        assertEditCommandSuccess(editedPerson, editedPerson, 2);
    }

    @Test
    public void execute_addAndEditInterestToInterestsListSeparately_success() {
        // adding an interest
        Person expectedPerson = new PersonBuilder().withName("Eric").withInterest("Golf", "Reading").build();
        assertEditCommandSuccess(expectedPerson, expectedPerson, 3);

        // editing the interest
        Person expectedPerson2 = new PersonBuilder().withName("LMAO").withInterest("Mining", "Reading").build();
        Person editedPerson = new PersonBuilder().withName("LMAO").withInterest("(1) Mining").build();

        assertEditCommandSuccess(expectedPerson2, editedPerson, 3);
    }

    @Test
    public void execute_removeInterestFromInterestsList_success() {
        Person expectedPerson = new PersonBuilder().withName("Madrid Zimmerman")
                .withEmail("maddy@example.com").withGender("M").withAge("73").build();
        Person editedPerson = new PersonBuilder().withName("Madrid Zimmerman")
                .withEmail("maddy@example.com").withGender("M").withAge("73").withInterest("(1) remove").build();

        assertEditCommandSuccess(expectedPerson, editedPerson, 0);
    }

    @Test
    public void execute_performManyOperationsInOneCommandOnInterestsList_success() {
        Person expectedPerson = new PersonBuilder().withName("Luke Skywalker").withEmail("luke@example.com")
                .withPhone("96561828").withCalled("false").withAddress("HOUSE").withGender("M").withAge("17")
                .withInterest("Reading", "football", "music", "jogging").build();
        Person editedPerson = new PersonBuilder().withName("Luke Skywalker").withEmail("luke@example.com")
                .withPhone("96561828").withCalled("false").withAddress("HOUSE").withGender("M").withAge("17")
                .withInterest("(1) remove", "football", "music", "(2) Reading", "jogging").build();

        assertEditCommandSuccess(expectedPerson, editedPerson, 1);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        Person person = TypicalPersons.DANIEL;
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(person).build());
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidInterestsListIndex_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(5) remove").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_THIRD_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_INVALID_INTERESTS_INDEX);
    }

    @Test
    public void execute_duplicateInterestArgument_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(1) golf", "(2) golf").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_INTEREST_ARGUMENT);
    }

    @Test
    public void execute_duplicateIndex_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(1) cycling", "(1) remove").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_INDEX);
    }

    @Test
    public void execute_duplicateInterest_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(2) eating", "robotics").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, InterestsList.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_emptyInterestArgument_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(2) ").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, Interest.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_invalidInterestsListIndexCharacter_failure() {
        Person editedPerson = new PersonBuilder().withInterest("(api) astronomy").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_THIRD_PERSON, descriptor);

        assertCommandFailure(editCommand, model, "The interestslist index provided is invalid.");
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

}
