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
    "groupID"
})
@XmlRootElement(name = "DeleteGroup-Request")
public class DeleteGroupRequest {

    @XmlElement(name = "GroupID", required = true)
    protected GroupID groupID;

    /**
     * Gets the value of the groupID property.
     * 
     * @return
     *     possible object is
     *     {@link GroupID }
     *     
     */
    public GroupID getGroupID() {
        return groupID;
    }

    /**
     * Sets the value of the groupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupID }
     *     
     */
    public void setGroupID(GroupID value) {
        this.groupID = value;
    }

}
