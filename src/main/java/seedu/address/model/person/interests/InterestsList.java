package seedu.address.model.person.interests;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;

import seedu.address.commons.core.index.Index;

/**
 * Represents the list of interests of the person
 */
public class InterestsList {
    public static final String MESSAGE_CONSTRAINTS = "Duplicated interests not allowed";
    public static final String NO_INTEREST = "N.A";

    private ArrayList<Interest> list;

    public InterestsList() {
        this.list = new ArrayList<>();
    }

    /**
     * Constructor for the class
     * @param list A list of interests
     */
    public InterestsList(ArrayList<Interest> list) {
        requireNonNull(list);
        this.list = list;
    }

    /**
     * Adds an interest into the list.
     * @param interest
     */
    public void addInterest(Interest interest) {
        requireNonNull(interest);
        checkArgument(!checkDuplicate(interest), MESSAGE_CONSTRAINTS);
        this.list.add(interest);
    }

    /**
     * Edits an interest in the list.
     * @param interest The new interest
     * @param index The index of the Interest in the list that is to be changed
     */
    public void setInterest(Interest interest, int index) {
        checkArgument(!checkDuplicate(interest), MESSAGE_CONSTRAINTS);
        this.list.set(index, interest);
    }

    /**
     * Removes an interest in the list.
     * @param index The index of the Interest in the list that is to be removed
     */
    public void removeInterest(int index) {
        this.list.remove(index);
    }

    /**
     * Checks for duplicates in the list
     * @param i
     * @return True if a duplicate exists, false otherwise.
     */
    public boolean checkDuplicate(Interest i) {
        if (size() == 0) {
            return false;
        }
        for (Interest interest : list) {
            if (interest.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return list.size();
    }

    public Interest getInterest(Index i) {
        return list.get(i.getZeroBased());
    }

    public ArrayList<Interest> getAllInterests() {
        return this.list;
    }

    public InterestsList copyInterestsList() {
        return new InterestsList(new ArrayList<>(this.list));
    }

    /**
     * Checks if there is a proper value.
     * @return True if the list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public String toString() {
        if (list.isEmpty()) {
            return NO_INTEREST;
        }

        StringBuilder value = new StringBuilder();
        for (int k = 0; k < list.size(); k++) {
            if (k == 0) {
                value.append("1. ").append(list.get(k).value);
            } else {
                value.append("\n").append(k + 1).append(". ").append(list.get(k).value);
            }
        }
        return value.toString();
    }

    /**
     * Prints out all interests without the numbering
     * @return The string
     */
    public String toStringNoNumbering() {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder value = new StringBuilder();
        for (int k = 0; k < list.size() - 1; k++) {
            value.append(list.get(k).value).append(", ");
        }
        value.append(list.get(size() - 1));
        return value.toString();
    }


    @Override
    public boolean equals(Object other) {

        if (other == this) { // short circuit if same object
            return true;
        }

        if (!(other instanceof InterestsList)) {
            return false;
        }

        InterestsList il = (InterestsList) other;

        if (size() != il.size()) {
            return false;
        }

        boolean result = true;

        for (int k = 0; k < size(); k++) {
            Interest i1 = getInterest(Index.fromZeroBased(k));
            Interest i2 = il.getInterest(Index.fromZeroBased(k));
            result = i1.equals(i2);
            if (!result) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

}

