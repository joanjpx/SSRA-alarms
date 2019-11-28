package com.asistp.main;

import com.asistp.Utils.Conexion;
import com.asistp.Utils.utils;
import com.asistp.main.ReadFile;
import java.io.PrintStream;

public class Main11 {
    public static void main(String[] args) {
        
        ReadFile rf;
        System.out.println("---------INICIANDO LECTURA---------");
        if (args.length > 1) {
            for (int i = 2; i < args.length; ++i) {
                rf = new ReadFile(args[i], args[0], false,args[1]);
                rf.initRead();
            }
        }
        try {
            do {
                String dateValidate = utils.getFileName((String)"yyyy-MM-dd");
                Thread.sleep(60000L);
                rf = new ReadFile(dateValidate, args[0], true,args[1]);
                rf.initRead();
            } while (true);
        }
        catch (Exception dateValidate) {
            return;
        }
    }
}