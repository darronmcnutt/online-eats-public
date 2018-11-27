package csc472.depaul.edu.onlineeats;

public class Driver {
    private String firstName;
    private String lastName;
    private int rating;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String firstName, String lastName, int rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = rating;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getRating() {
        return rating;
    }
}
