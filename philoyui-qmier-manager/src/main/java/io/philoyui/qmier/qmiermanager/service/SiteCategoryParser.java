package io.philoyui.qmier.qmiermanager.service;

import io.philoyui.qmier.qmiermanager.entity.SiteCategoryEntity;
import org.jsoup.nodes.Document;

import java.text.ParseException;

public interface SiteCategoryParser {
    void parseListItem(SiteCategoryEntity siteCategoryEntity, Document doc) throws ParseException;
}
