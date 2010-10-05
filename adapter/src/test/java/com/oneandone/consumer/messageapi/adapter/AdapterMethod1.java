/**
 * 
 */
package com.oneandone.consumer.messageapi.adapter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdapterMethod1 {

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleMethod1()";
    }
}