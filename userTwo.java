
// Import the 'ClientPackage' class from the 'packageclient' package to use it for the chat client.
import packageclient.ClientPackage;

// Main class representing the chat client for "userThree."
public class userTwo {
    public static void main(String[] args) {
        String name = "The V.D.";
        int position = 400;
        String img = "Rubio_Circle.png";


        // Create a new instance of the 'ClientPackage' class, passing the name as an argument.
        // This will start the chat client for "userTwo" with the specified name.
        new ClientPackage(name,position,img);

    }
}
