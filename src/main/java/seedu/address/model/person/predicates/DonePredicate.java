package seedu.address.model.person.predicates;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.model.person.Person;

/**
 * Tests that a {@code Person}'s {@code Done} matches the value given.
 */
public class DonePredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final boolean isFindAll;

    /**
     * Constructor for the DonePredicate class
     * @param keywords The keywords to compare against the {@code Person}'s {@code isDone}
     * @param isFindAll True if all keywords need to match, false otherwise
     */
    public DonePredicate(List<String> keywords, boolean isFindAll) {
        requireNonNull(keywords);
        this.keywords = keywords;
        this.isFindAll = isFindAll;
    }

    @Override
    public boolean test(Person person) {
        if (isFindAll) {
            return keywords.stream()
                    .allMatch(keyword -> person.getIsDone().toString().toLowerCase(Locale.ROOT).equals(
                            getDoneValueFromKeyword(keyword)
                    ));
        }
        return keywords.stream()
                .anyMatch(keyword -> person.getIsDone().toString().toLowerCase(Locale.ROOT).equals(
                        getDoneValueFromKeyword(keyword)
                ));
    }

    /**
     * Allows more flexibility for user inputs
     * @param keyword entered by user
     * @return True if input is t or true
     *          false if its f or false
     */
    private String getDoneValueFromKeyword(String keyword) {
        String test = keyword.toLowerCase(Locale.ROOT);
        if (test.equals("t") || test.equals("true")) {
            return "true";
        } else if (test.equals("f") || test.equals("false")) {
            return "false";
        } else {
            // Basically return anything that is not 'true' / 'false' / 'N.A'
            // so that the predicate will return false
            return "THIS IS NOT A DONE VALUE";
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DonePredicate // instanceof handles nulls
                && keywords.equals(((DonePredicate) other).keywords))
                && isFindAll == ((DonePredicate) other).isFindAll; // state check
    }
}
