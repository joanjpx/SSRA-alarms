/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.bean;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Usuario
 */
public class BeanTrap {

    private StringBuilder event;
    private List<BeanOid> listOid;
    private Map<String, Integer> mapPosOid;
    private String strError;

    public StringBuilder getEvent() {
        return event;
    }

    public void setEvent(StringBuilder event) {
        this.event = event;
    }

    public List<BeanOid> getListOid() {
        return listOid;
    }

    public void setListOid(List<BeanOid> listOid) {
        this.listOid = listOid;
    }

    public Map<String, Integer> getMapPosOid() {
        return mapPosOid;
    }

    public void setMapPosOid(Map<String, Integer> mapPosOid) {
        this.mapPosOid = mapPosOid;
    }

    public String getStrError() {
        return strError;
    }

    public void setStrError(String strError) {
        this.strError = strError;
    }

    @Override
    public String toString() {
        StringBuilder strResult = new StringBuilder();
        strResult.append("\n").append(strError);
        strResult.append("\nevent= ").append(event);
        for (BeanOid beanOid : this.listOid) {
            strResult.append(beanOid.toString());
            //strResult.append("\n").append(beanOid.getOid().toString());
        }
        return strResult.toString();
    }

}
