//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.25 at 05:48:09 PM CEST 
//


package es.rickyepoderi.wbxml.bind.wvcsp;

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
    "srch",
    "stsrc"
})
@XmlRootElement(name = "SearchFunc")
public class SearchFunc {

    @XmlElement(name = "SRCH")
    protected SRCH srch;
    @XmlElement(name = "STSRC")
    protected STSRC stsrc;

    /**
     * Gets the value of the srch property.
     * 
     * @return
     *     possible object is
     *     {@link SRCH }
     *     
     */
    public SRCH getSRCH() {
        return srch;
    }

    /**
     * Sets the value of the srch property.
     * 
     * @param value
     *     allowed object is
     *     {@link SRCH }
     *     
     */
    public void setSRCH(SRCH value) {
        this.srch = value;
    }

    /**
     * Gets the value of the stsrc property.
     * 
     * @return
     *     possible object is
     *     {@link STSRC }
     *     
     */
    public STSRC getSTSRC() {
        return stsrc;
    }

    /**
     * Sets the value of the stsrc property.
     * 
     * @param value
     *     allowed object is
     *     {@link STSRC }
     *     
     */
    public void setSTSRC(STSRC value) {
        this.stsrc = value;
    }

}
