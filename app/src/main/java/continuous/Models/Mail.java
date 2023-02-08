package continuous.Models;

public class Mail {

    public String host;
    public int port;
    public boolean debug;
    
    public String username;
    public String password;

    public String senderEmail;
    
    public Mail() {
        host = "smtp.gmail.com";
        port = 587;
        debug = true;

        username = "dd2480group23@gmail.com";
        // password = "continuous23";
        password = "pjiempleulhvjdjn";

        senderEmail = "dd2480group23@gmail.com";
    }    
    
}
