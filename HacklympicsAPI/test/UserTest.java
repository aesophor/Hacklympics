

import java.io.IOException;
import com.google.gson.JsonObject;
import com.hacklympics.api.communication.Response;
import com.hacklympics.api.user.UserProfile;
import com.hacklympics.api.user.Student;
import com.hacklympics.api.user.Teacher;
import com.hacklympics.api.user.User;
import com.hacklympics.api.utility.NetworkUtils;

public class UserTest {
    
    public static String createUser(UserProfile user) {
        
        JsonObject j = new JsonObject();
        
        j.addProperty("username", user.getUsername());
        //j.addProperty("password", user.getPassword());
        j.addProperty("fullname", user.getFullname());
        j.addProperty("graduation_year", user.getGradYear());
        j.addProperty("is_student", "False");
        
        return j.toString();
    }
    
    public static void main(String[] args) throws IOException {
        //Teacher max = new Teacher("max");
        //System.out.println(max);
        
        String username = "max";
        String password = "password";
        
        System.out.print("--> Logging in as " + username + " ...");
        Response resp = User.login(username, password);
        
        System.out.println(resp.getStatusCode());
        
        if (resp.success()) {
            System.out.println("--> Role: " + resp.getContent().get("role"));
        }
        
        //System.out.println("Registering Tim...");
        //System.out.println(User.register("tim", "timmy", "Tim", 108).success());
        
        //System.out.println("Logging out as andrey...");
        //System.out.println(andrey.logout().success());
    } 
    
}
