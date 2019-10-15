/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.gestor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class ReadTxt {

    private int countTraps = 0;
    public static void main(String[] args) {
        ReadTxt test = new ReadTxt();
        test.readTxt();
    }

    private void readTxt() {
        try {
            muestraContenido("D:/txtU2000.txt");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void muestraContenido(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        StringBuilder strTrap = new StringBuilder();
        while ((cadena = b.readLine()) != null) {
            if (cadena.contains("2016-05-24")) {
                if (strTrap.length() > 0) {
                    leerTrap(strTrap);
                }
                strTrap = new StringBuilder();
            }
            strTrap.append(cadena);
        }
        if (strTrap.length() > 0) {
            leerTrap(strTrap);
        }
        b.close();
    }

    private void leerTrap(StringBuilder strTrap) {
        countTraps++;
        //System.out.println("countTraps = " + countTraps);
        int pos = strTrap.indexOf("securityLevel");
        if (pos <= -1) {
            return;
        }
        strTrap.delete(0, pos);
        pos = strTrap.indexOf("]");
        if (pos <= -1) {
            return;
        }
        strTrap.delete(0, pos + 1);
        String[] listOid = strTrap.toString().split(";");
        StringBuilder strEvent = new StringBuilder("event U2000");
        List<StringBuilder> list = new ArrayList();
        for (String strOid : listOid) {
            list.add(new StringBuilder(strOid));
        }
        GestorU2000 beanGestor = new GestorU2000();
        beanGestor.init(strEvent, list);
    }
}
