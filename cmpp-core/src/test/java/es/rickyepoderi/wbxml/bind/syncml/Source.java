//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.30 at 11:39:15 AM CEST 
//


package es.rickyepoderi.wbxml.bind.syncml;

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
    "locURI",
    "locName"
})
@XmlRootElement(name = "Source", namespace="SYNCML:SYNCML1.1")
public class Source {

    @XmlElement(name = "LocURI", namespace="SYNCML:SYNCML1.1", required = true)
    protected String locURI;
    @XmlElement(name = "LocName", namespace="SYNCML:SYNCML1.1")
    protected String locName;

    /**
     * Gets the value of the locURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocURI() {
        return locURI;
    }

    /**
     * Sets the value of the locURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocURI(String value) {
        this.locURI = value;
    }

    /**
     * Gets the value of the locName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocName() {
        return locName;
    }

    /**
     * Sets the value of the locName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocName(String value) {
        this.locName = value;
    }

}
