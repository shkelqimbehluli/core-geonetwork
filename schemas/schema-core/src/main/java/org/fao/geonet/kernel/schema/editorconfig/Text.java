
package org.fao.geonet.kernel.schema.editorconfig;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ref" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "text")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-11-09T11:51:19+01:00", comments = "JAXB RI v2.2.4-2")
public class Text {

    @XmlAttribute(name = "ref", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-11-09T11:51:19+01:00", comments = "JAXB RI v2.2.4-2")
    protected String ref;

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-11-09T11:51:19+01:00", comments = "JAXB RI v2.2.4-2")
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-11-09T11:51:19+01:00", comments = "JAXB RI v2.2.4-2")
    public void setRef(String value) {
        this.ref = value;
    }

}
