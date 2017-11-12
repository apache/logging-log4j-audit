package org.apache.logging.log4j.catalog.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.apache.logging.log4j.catalog.api.Event;

import static org.apache.logging.log4j.catalog.api.constant.Constants.DEFAULT_CATALOG;

public class CatalogEventFilter extends SimpleBeanPropertyFilter {

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        if (writer.getName().equals("catalogId") && DEFAULT_CATALOG.equals(((Event)pojo).getCatalogId())) {
            return;
        }
        super.serializeAsField(pojo, jgen, provider, writer);
    }
}
