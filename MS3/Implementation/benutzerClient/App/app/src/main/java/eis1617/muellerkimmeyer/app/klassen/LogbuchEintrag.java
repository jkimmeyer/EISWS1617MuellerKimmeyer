package eis1617.muellerkimmeyer.app.klassen;

import java.util.Date;

/**
 * Created by morit on 12.01.2017.
 */

public class LogbuchEintrag {

    public String aktion;
    public Date datum;
    public int icon;
    public String message;

    public LogbuchEintrag() {}

    public LogbuchEintrag(String aktion, Date datum, int icon, String message) {
        this.aktion = aktion;
        this.datum = datum;
        this.icon = icon;
        this.message = message;
    }
}
