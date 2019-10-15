/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asistp.bean;

/**
 *
 * @author Usuario
 */
public class BeanOid {

    private StringBuilder oid;
    private StringBuilder oidConvert;
    private StringBuilder value;

    public StringBuilder getOid() {
        return oid;
    }

    public void setOid(StringBuilder oid) {
        this.oid = oid;
    }

    public StringBuilder getOidConvert() {
        return oidConvert;
    }

    public void setOidConvert(StringBuilder oidConvert) {
        this.oidConvert = oidConvert;
    }

    public StringBuilder getValue() {
        return value;
    }

    public void setValue(StringBuilder value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\noid= " + this.oid + ", oidConvert= " + this.oidConvert + ", value= " + this.value;
    }

}
