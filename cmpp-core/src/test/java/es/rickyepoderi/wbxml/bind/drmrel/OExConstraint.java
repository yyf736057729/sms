//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.29 at 02:56:01 PM CEST 
//


package es.rickyepoderi.wbxml.bind.drmrel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "oDdCount",
    "oDdDatetime",
    "oDdInterval"
})
@XmlRootElement(name = "constraint", namespace="http://odrl.net/1.1/ODRL-EX")
public class OExConstraint {

    @XmlElement(name = "count", namespace="http://odrl.net/1.1/ODRL-DD")
    protected String oDdCount;
    @XmlElement(name = "datetime", namespace="http://odrl.net/1.1/ODRL-DD")
    protected ODdDatetime oDdDatetime;
    @XmlElement(name = "interval", namespace="http://odrl.net/1.1/ODRL-DD")
    protected String oDdInterval;

    /**
     * Gets the value of the oDdCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getODdCount() {
        return oDdCount;
    }

    /**
     * Sets the value of the oDdCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setODdCount(String value) {
        this.oDdCount = value;
    }

    /**
     * Gets the value of the oDdDatetime property.
     * 
     * @return
     *     possible object is
     *     {@link ODdDatetime }
     *     
     */
    public ODdDatetime getODdDatetime() {
        return oDdDatetime;
    }

    /**
     * Sets the value of the oDdDatetime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ODdDatetime }
     *     
     */
    public void setODdDatetime(ODdDatetime value) {
        this.oDdDatetime = value;
    }

    /**
     * Gets the value of the oDdInterval property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getODdInterval() {
        return oDdInterval;
    }

    /**
     * Sets the value of the oDdInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setODdInterval(String value) {
        this.oDdInterval = value;
    }

}
