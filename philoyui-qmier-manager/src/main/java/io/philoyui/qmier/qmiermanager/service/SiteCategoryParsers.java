package io.philoyui.qmier.qmiermanager.service;

import io.philoyui.qmier.qmiermanager.entity.SiteCategoryEntity;
import io.philoyui.qmier.qmiermanager.entity.SiteEntity;
import io.philoyui.qmier.qmiermanager.service.impl.AiShangHaiSiteCategoryParser;
import io.philoyui.qmier.qmiermanager.service.impl.YiXianSiteCategoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SiteCategoryParsers {

    @Autowired
    private AiShangHaiSiteCategoryParser aiShangHaiSiteCategoryParser;

    @Autowired
    private YiXianSiteCategoryParser yiXianSiteCategoryParser;

    @Autowired
    private SiteService siteService;

    public SiteCategoryParser select(SiteCategoryEntity siteCategoryEntity) {

        SiteEntity siteEntity = siteService.get(siteCategoryEntity.getSiteId());

        switch (String.valueOf(siteEntity.getId())){
            case "1":
            case "2":
                return aiShangHaiSiteCategoryParser;
            case "3":
                return yiXianSiteCategoryParser;
        }

        return null;
    }
}
