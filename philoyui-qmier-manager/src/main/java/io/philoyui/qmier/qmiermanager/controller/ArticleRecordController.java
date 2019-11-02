package io.philoyui.qmier.qmiermanager.controller;

import cn.com.gome.page.excp.GmosException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.philoyui.qmier.qmiermanager.entity.*;
import io.philoyui.qmier.qmiermanager.entity.enu.ArticleStatus;
import io.philoyui.qmier.qmiermanager.service.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/article_record")
public class ArticleRecordController {

    @Autowired
    private ArticleRecordService articleRecordService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private QmierService qmierService;

    @Autowired
    private SiteCategoryService siteCategoryService;

    @Autowired
    private QmierImageService qmierImageService;

    @Autowired
    private QmierCommentService qmierCommentService;

    private Gson gson = new GsonBuilder().create();

    @RequestMapping("/fetch")
    public ResponseEntity<String> fetch(@RequestParam Long id) throws IOException {

        ArticleRecordEntity articleRecordEntity = articleRecordService.get(id);

        SiteEntity siteEntity = siteService.get(articleRecordEntity.getSiteId());

        SiteCategoryEntity siteCategoryEntity = siteCategoryService.get(articleRecordEntity.getSiteCategoryId());

        int pageCount = articleRecordEntity.getReplyCount() % 10 == 0 ? articleRecordEntity.getReplyCount() / 10 : articleRecordEntity.getReplyCount() + 1;

        List<String> comments = new ArrayList<>();

        for (int pageNo = 1; pageNo <= pageCount; pageNo++) {

            String fetchUrl = siteEntity.getMainUrl() + "/" + articleRecordEntity.getDetailUrl() + "&page=" + pageNo;

            System.getProperties().setProperty("http.proxyHost", "127.0.0.1");
            System.getProperties().setProperty("http.proxyPort", "9999");

            Document doc = Jsoup.connect(fetchUrl)
                    .cookie("nzrE_2132_auth", "c297E9MPv29TwRMkPK58EKIJKGEg34axRbKOPEo0j%2BZa2kebwIDdUF9kkupVeb8Bz0fkD7DiomaKYZi5cPBR%2FqhzVw")
                    .cookie("nzrE_2132_saltkey", "eRBn59ve")
                    .get();

            Elements contentElements = doc.select("[id^=postmessage_]");

            if (pageNo == 1) {
                List<String> images = new ArrayList<>();
                Elements threadDivs = doc.select(" img[zoomfile^=data]");
                for (Element threadDiv : threadDivs) {
                    images.add(threadDiv.attr("zoomfile"));
                }

                List<String> contents = contentElements.eachText();
                articleRecordEntity.setContent(formatContent(contents.get(0)));
                articleRecordEntity.setImagePaths(StringUtils.join(images,","));
                articleRecordEntity.setStatus(ArticleStatus.COMPLETED);
                articleRecordEntity.setCity(siteCategoryEntity.getCity());
                comments.addAll(contents);
            }

        }

        comments.remove(0);

        articleRecordEntity.setComments(formatContent(StringUtils.join(comments, "")));

        articleRecordService.insert(articleRecordEntity);

        return ResponseEntity.ok("success");

    }

    @RequestMapping("/detailUrl")
    public String detailUrl(@RequestParam Long id,@RequestParam Long siteId) throws UnsupportedEncodingException {

        ArticleRecordEntity articleRecordEntity = articleRecordService.get(id);

        SiteEntity siteEntity = siteService.get(siteId);

        String redirectUrl = siteEntity.getMainUrl() + articleRecordEntity.getDetailUrl();

        return "redirect:"+redirectUrl;
    }

    @RequestMapping("/analysis")
    public ResponseEntity<String> analysis(@RequestParam Long[] id) throws IOException {

        for (Long recordId : id) {
            ArticleRecordEntity articleRecordEntity = articleRecordService.get(recordId);
            SiteEntity siteEntity = siteService.get(articleRecordEntity.getSiteId());
            QmierEntity qmierEntity = qmierService.findByCityAndName(articleRecordEntity.getCity(),articleRecordEntity.getQmierName());

            if(articleRecordEntity.getStatus()==ArticleStatus.ANALYSIS){
                continue;
            }

            if(qmierEntity != null){
                String imagePaths = articleRecordEntity.getImagePaths();
                String[] imagePathArray = imagePaths.split(",");
                for (String imagePath : imagePathArray) {
                    QmierImageEntity qmierImageEntity = new QmierImageEntity();
                    qmierImageEntity.setQmierId(qmierEntity.getId());
                    qmierImageEntity.setImagePath(siteEntity.getMainUrl()+imagePath);
                    qmierImageEntity.setArticleId(articleRecordEntity.getId());
                    qmierImageService.insert(qmierImageEntity);
                }

                String comments = articleRecordEntity.getComments();
                String[] commentArray = comments.split(";");
                for (String comment : commentArray) {
                    QmierCommentEntity qmierCommentEntity = new QmierCommentEntity();
                    qmierCommentEntity.setQmierId(qmierEntity.getId());
                    qmierCommentEntity.setContent(comment);
                    qmierCommentEntity.setArticleId(articleRecordEntity.getId());
                    qmierCommentService.insert(qmierCommentEntity);
                }
                articleRecordEntity.setStatus(ArticleStatus.ANALYSIS);
                articleRecordService.insert(articleRecordEntity);

            }else{
                throw new GmosException("不存在的Qmier");
            }
        }

        return ResponseEntity.ok("success");

    }

    private String formatContent(String content) {
        return content.replaceAll("\\p{Punct}","")
                .replaceAll("[A-Za-z0-9]+","")
                .replaceAll("下载次数|下载附件|保存到相册","");
    }

}
