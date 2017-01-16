package eis1617.muellerkimmeyer.app.klassen;

import java.util.Date;

/**
 * Created by morit on 13.01.2017.
 */

public class WasserwerteEintrag {

    public String von;
    public Date datum;
    public double kh;
    public double gh;
    public double ph;
    public double co2;
    public double eisen;
    public double kalium;
    public double no3;
    public double po3;

    public WasserwerteEintrag(String von, Date datum, double kh, double gh, double ph, double co2, double eisen, double kalium, double no3, double po3) {
        this.von = von;
        this.datum = datum;
        this.kh = kh;
        this.gh = gh;
        this.ph = ph;
        this.co2 = co2;
        this.eisen = eisen;
        this.kalium = kalium;
        this.no3 = no3;
        this.po3 = po3;
    }
}
