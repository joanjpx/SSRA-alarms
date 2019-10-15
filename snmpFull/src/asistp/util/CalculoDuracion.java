/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.util;

import java.util.Date;

/**
 *
 * @author Usuario
 */
public class CalculoDuracion {

    public static String getDiferenciaDias(Date dateIni, Date dateFin) {
        long dif = (dateFin.getTime() - dateIni.getTime()) / 1000;
        long difDay = dif / (24 * 60 * 60);
        int day = (int) difDay;
        dif = dif - (day * 24 * 60 * 60);
        long difHour = dif / (60 * 60);
        int hour = (int) difHour;
        dif = dif - (hour * 60 * 60);
        long difMin = dif / 60;
        int min = (int) difMin;
        dif = dif - (min * 60);
        int seg = (int) dif;
        String tiempo = day + "d " + (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min + ":" + (seg < 10 ? "0" : "") + seg;
        return tiempo;
    }
}
