
package asistp.util;

/**
 *
 * @author Usuario2
 */
public class CoriantHelper {
    
    public static String humanizeSeverity(String in) {
        
        switch(Integer.valueOf(in)) {
            case 1:
                return "critical";
            case 2:
                return "major";
            case 3:
                return "minor";
            case 4:
                return "informational";                
        }
        
        return "";
    }
    
    
    public static String humanizeLikelyReason(String in) {
        
        switch(Integer.valueOf(in)) {
            case 1:
                return "unknown";
            case 2:
                return "node";
            case 3:
                return "shelf";
            case 4:
                return "line"; 
            case 5:
                return "switch";
            case 6:
                return "circuit";
            case 7:
                return "lsp";
            case 8:
                return "l2Interface";
            case 9:
                return "l3Interface";
            case 10:
                return "rowIndex";
            case 1000000000:
                return "only8600Cli";
        }
        
        return "";
        
    }
    
    
    public static String humanizeFaultFlag(String in) {
    
        switch(Integer.valueOf(in)) {
            case 1:
                return "Activación";
            case 3:
                return "Cese";
        }
        
        return "";
        
    }
    
}
