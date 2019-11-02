package io.philoyui.qmier.qmiermanager.service.impl;

import com.google.common.base.Strings;
import io.philoyui.qmier.qmiermanager.entity.ArticleRecordEntity;
import io.philoyui.qmier.qmiermanager.entity.SiteCategoryEntity;
import io.philoyui.qmier.qmiermanager.entity.enu.ArticleStatus;
import io.philoyui.qmier.qmiermanager.service.ArticleRecordService;
import io.philoyui.qmier.qmiermanager.service.SiteCategoryParser;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;


@Component
public class YiXianSiteCategoryParser implements SiteCategoryParser {

    @Autowired
    private ArticleRecordService articleRecordService;

    @Override
    public void parseListItem(SiteCategoryEntity siteCategoryEntity, Document doc) throws ParseException {
        Elements threadDivs = doc.select("[id^=normalthread_]");
        for (Element threadDiv : threadDivs) {

            String title = threadDiv.select("tr > th > a.xst").text();
            String author = threadDiv.select("tr > td:nth-child(3) > cite > a").text();
            String region = threadDiv.select("tr > th > em > a").text();
            String detailId = threadDiv.attr("id");
            String detailUrl = threadDiv.select("tr > th > a.xst").attr("href");
            String replyCount = threadDiv.select("tr > td.num > a").text();
            String readCount = threadDiv.select("tr > td.num > em").text();

            String createdTimeString = threadDiv.select("tr > td:nth-child(3) > em > span > span").attr("title");
            if (Strings.isNullOrEmpty(createdTimeString)) {
                createdTimeString = threadDiv.select("tr > td:nth-child(3) > em > span").text();
            }

            if (Strings.isNullOrEmpty(createdTimeString)) {
                System.err.println(threadDiv.text() + author + "--" + region + "--" + createdTimeString);
            } else {
                boolean existRecord = articleRecordService.existsBySiteCategoryIdAndDetailId(siteCategoryEntity.getId(), detailId);
                if (!existRecord) {
                    Date createdTime = DateUtils.parseDate(createdTimeString, "yyyy-MM-dd HH:mm");
                    ArticleRecordEntity articleRecordEntity = new ArticleRecordEntity();
                    articleRecordEntity.setSiteId(siteCategoryEntity.getSiteId());
                    articleRecordEntity.setDetailUrl(detailUrl);
                    articleRecordEntity.setStatus(ArticleStatus.WAITING);
                    articleRecordEntity.setCreatedTime(createdTime);
                    articleRecordEntity.setSiteCategoryId(siteCategoryEntity.getId());
                    articleRecordEntity.setDetailId(detailId);
                    articleRecordEntity.setRegion(region);
                    articleRecordEntity.setAuthor(author);
                    articleRecordEntity.setTitle(title);
                    articleRecordEntity.setReadCount(Integer.parseInt(readCount));
                    articleRecordEntity.setReplyCount(Integer.parseInt(replyCount));
                    articleRecordService.insert(articleRecordEntity);
                }
            }
        }

    }


}
