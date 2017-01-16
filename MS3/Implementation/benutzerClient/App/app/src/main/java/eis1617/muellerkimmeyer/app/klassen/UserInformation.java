package eis1617.muellerkimmeyer.app.klassen;

/**
 * Created by morit on 10.01.2017.
 */

public class UserInformation {

    public String sid;
    public String token;

    public UserInformation(){}

    public UserInformation(String sid, String token) {
        this.sid = sid;
        this.token = token;
    }
}
