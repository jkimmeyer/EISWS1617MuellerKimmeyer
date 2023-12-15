package de.eis.muellerkimmeyer.klassen;

/*
 *  EISWS1617
 *
 *  Implementation - Desktop Anwendung (Fachhandlung Client)
 *
 *  Autor: Moritz M�ller
 */

public class Aquarium {

    private String bezeichnung;
    private double laenge, breite, hoehe, glasstaerke, kieshoehe, fuellstanddifferenz;

    public Aquarium() {}
    
    public Aquarium(String bezeichnung, double laenge, double breite, double hoehe, double glasstaerke, double kieshoehe, double fuellstanddifferenz) {
        this.bezeichnung = bezeichnung;
        this.laenge = laenge;
        this.breite = breite;
        this.hoehe = hoehe;
        this.glasstaerke = glasstaerke;
        this.kieshoehe = kieshoehe;
        this.fuellstanddifferenz = fuellstanddifferenz;

    }

    // Formel von http://www.aq-technik.de/Aquarium-Rechner/co2_kh_ph.php

    public int getCO2Gehalt(double kh, double ph){
        return (int)Math.round(Math.pow(10,(7.477+(0.4343*Math.log(kh))-ph)));
    }

    // Formel von http://www.aq-technik.de/Aquarium-Rechner/duenger_dosierung.php

    public int getDuengerdosierung(double nettoWasserVolumen, double konzentrationAquarium, double konzentrationDuenger, double gewuenschteKonzentration){
        return (int)Math.round(nettoWasserVolumen*(gewuenschteKonzentration - konzentrationAquarium) / konzentrationDuenger * 1000);
    }

    /*
    * QUELLENANGABE
    * getBenoetigteWassermenge() �bernommen aus http://www.aq-technik.de/Aquarium-Rechner/tww_mit_zielwert.php
    * Autor: Peter Ehrenfried, Abruf am: 12.01.2017
    * �nderungen: Den Javascript Code der Seite in Java �bersetzt und angepasst
    */

    public double[] getBenoetigteWassermengen(double nettoWasserVolumen, double gewuenschteMenge, double khGhLw, double khGhAq, double gewuenschteKhGh, double wirkungsgradRe){

        double WertR = (gewuenschteKhGh-((nettoWasserVolumen-gewuenschteMenge)/nettoWasserVolumen)*khGhAq)*nettoWasserVolumen/gewuenschteMenge;
        double Vol1 = gewuenschteMenge;
        double Vol2 = 0;
        double WertT = khGhLw;
        double WertW = khGhLw*(1-wirkungsgradRe/100);

        while (WertT > WertR){
            WertT = (Vol1*khGhLw + Vol2*WertW) / gewuenschteMenge;
            Vol1 = Vol1-0.001;
            Vol2 = gewuenschteMenge-Vol1;
        }
        if (Vol1<0){
            Vol2 = nettoWasserVolumen*(khGhAq-gewuenschteKhGh)/(khGhAq-WertW);
            Vol1 = 0;
        }
        double ergebnisLeitungswasser = Math.round(Vol1);
        double ergebnisReinwasser = Math.round(Vol2);

        double[] ergebnisse = {ergebnisLeitungswasser, ergebnisReinwasser};
        return ergebnisse;
    }

    private double getBruttoVolumen(){
        return laenge * breite * hoehe;
    }

    /*
    * QUELLENANGABE
    * getNettoVolumen() �bernommen aus http://www.zierfischforum.at/volumsberechnung.php
    * Autor: J�rgen Haberstroh, Abruf am: 12.01.2017
    * �nderungen: Den Javascript Code der Seite in Java �bersetzt und angepasst
    */

    private double getNettoVolumen(){

        // F�llstanddifferenz = der Abstand von der Glasoberkante des Beckens bis zum Wasserspiegel

        double kies_gewicht_pro_liter = 1.5; // Ungef�hr

        double volumen_aquarium = getBruttoVolumen();
        double volumen_glas = ((laenge * breite) + (2 * laenge * hoehe) + (2 * breite * hoehe)) * glasstaerke;
        double volumen_kies = (laenge - 2 * glasstaerke) * (breite - 2 * glasstaerke) * kieshoehe;
        double volumen_oben = laenge * breite * fuellstanddifferenz;

        double volumen_korrektur_verlust = glasstaerke * fuellstanddifferenz * (2 * laenge + 2 * breite);
        double netto_volumen_oben = ((volumen_aquarium - volumen_glas) - (volumen_oben - volumen_korrektur_verlust));

        double gewicht_kies = Math.round(kies_gewicht_pro_liter * volumen_kies);
        double wasserverdraengung_kies = gewicht_kies - volumen_kies;

        return netto_volumen_oben - wasserverdraengung_kies;
    }

    public double getBruttoVolumenInLiter(){
        return Math.round(getBruttoVolumen()/1000);
    }

    public double getNettoVolumenInLiter(){
        return Math.round(getNettoVolumen()/1000);
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public double getLaenge() {
        return laenge;
    }

    public void setLaenge(double laenge) {
        this.laenge = laenge;
    }

    public double getBreite() {
        return breite;
    }

    public void setBreite(double breite) {
        this.breite = breite;
    }

    public double getHoehe() {
        return hoehe;
    }

    public void setHoehe(double hoehe) {
        this.hoehe = hoehe;
    }

    public double getGlasstaerke() {
        return glasstaerke;
    }

    public void setGlasstaerke(double glasstaerke) {
        this.glasstaerke = glasstaerke;
    }

    public double getKieshoehe() {
        return kieshoehe;
    }

    public void setKieshoehe(double kieshoehe) {
        this.kieshoehe = kieshoehe;
    }

    public double getFuellstanddifferenz() {
        return fuellstanddifferenz;
    }

    public void setFuellstanddifferenz(double fuellstanddifferenz) {
        this.fuellstanddifferenz = fuellstanddifferenz;
    }

}