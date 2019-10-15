/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.mib;

import java.util.Map;

/**
 *
 * @author Usuario
 */
public abstract class MibGestor {

    protected Map<String, String> mapOid;
    protected Map<String, String> mapAlarm;

    public abstract void initOid();

    public abstract void initAlarm();

    public abstract Map<String, String> getMapOid();
    
    public abstract Map<String, String> getMapAlarm();
}
