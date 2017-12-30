package org.apache.logging.log4j.audit.service.catalog;

import org.apache.logging.log4j.audit.catalog.CatalogManager;
import org.apache.logging.log4j.catalog.api.Event;
import org.apache.logging.log4j.catalog.jpa.model.EventModel;

public interface AuditManager extends CatalogManager {

    EventModel saveEvent(Event event);
}
