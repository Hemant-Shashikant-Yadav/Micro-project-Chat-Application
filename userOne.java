// Import the 'ClientPackage' class from the 'packageclient' package to use it for the chat client.
import packageclient.ClientPackage;

// Main class representing the chat client for "userOne."
public class userOne {
    public static void main(String[] args) {
        String name = "The H.Y.";
        int position = 0;
        String img = "639-6399637_henry-circle-gentleman.png";
        
        // Create a new instance of the 'ClientPackage' class, passing the name as an argument.
        // This will start the chat client for "userOne" with the specified name.
        new ClientPackage(name,position,img);
    }
}
