package io.philoyui.qmier.qmiermanager.controller;

import io.philoyui.qmier.qmiermanager.entity.SiteCategoryEntity;
import io.philoyui.qmier.qmiermanager.service.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/admin/site_category")
public class SiteCategoryController {

    @Autowired
    private ArticleRecordService articleRecordService;

    @Autowired
    private SiteCategoryService siteCategoryService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteCategoryParsers siteCategoryParsers;

    @PutMapping("/fetch")
    public ResponseEntity<String> fetch(@RequestParam Long id) throws IOException, ParseException, InterruptedException {
        SiteCategoryEntity siteCategoryEntity = siteCategoryService.get(id);
        for (Integer i = 1; i <= siteCategoryEntity.getTotalPageNum(); i++) {
            String fetchUrl = siteCategoryEntity.getSiteListUrl().replaceAll("#pageNo#",String.valueOf(i));

            System.getProperties().setProperty("http.proxyHost", "127.0.0.1");
            System.getProperties().setProperty("http.proxyPort", "9999");

            Document doc = Jsoup.connect(fetchUrl).get();

            SiteCategoryParser siteCategoryParser = siteCategoryParsers.select(siteCategoryEntity);

            siteCategoryParser.parseListItem(siteCategoryEntity, doc);

            Thread.sleep(2000);

        }
        return ResponseEntity.ok("success");
    }


}
