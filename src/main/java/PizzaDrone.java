import java.util.Scanner;

public class PizzaDrone {

    private static String getRestAPIURL(String[] args) {
        String restAPI = null;
        if (args.length != 1) {
            System.err.println("Usage: java PizzaDrone <server address>");
            while (restAPI == null){
                System.out.println("Please enter the server address:");
                Scanner scanner = new Scanner(System.in);
                restAPI = scanner.nextLine();
            }
        }
        else {
            restAPI = args[0];
        }
        return restAPI;
    }

    public static void main(String[] args) {
        String restAPIUrl = getRestAPIURL(args);
    }
}
