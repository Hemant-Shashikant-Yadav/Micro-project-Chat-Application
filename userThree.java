
// Import the 'ClientPackage' class from the 'packageclient' package to use it for the chat client.
import packageclient.ClientPackage;

// Main class representing the chat client for "userThree."
public class userThree {
    public static void main(String[] args) {
        String name = "The A";
        int position = 800;
        String img = "opi.png";

        // Create a new instance of the 'ClientPackage' class, passing the name as an argument.
        // This will start the chat client for "userThree" with the specified name.
        new ClientPackage(name,position,img);

    }
}
