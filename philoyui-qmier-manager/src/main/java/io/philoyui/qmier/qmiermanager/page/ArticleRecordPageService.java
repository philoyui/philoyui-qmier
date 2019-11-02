package io.philoyui.qmier.qmiermanager.page;

import cn.com.gome.cloud.openplatform.common.PageObject;
import cn.com.gome.cloud.openplatform.common.SearchFilter;
import cn.com.gome.page.button.batch.BatchActionOperation;
import cn.com.gome.page.button.batch.BatchDeleteOperation;
import cn.com.gome.page.button.batch.ButtonStyle;
import cn.com.gome.page.button.batch.CreateOperation;
import cn.com.gome.page.button.column.ConfirmOperation;
import cn.com.gome.page.button.column.DeleteOperation;
import cn.com.gome.page.button.column.EditOperation;
import cn.com.gome.page.button.column.NewPageOperation;
import cn.com.gome.page.core.PageConfig;
import cn.com.gome.page.core.PageContext;
import cn.com.gome.page.core.PageService;
import cn.com.gome.page.field.*;
import cn.com.gome.page.field.domain.PageDomainProvider;
import cn.com.gome.page.field.validator.IntFieldDefinition;
import io.philoyui.qmier.qmiermanager.entity.ArticleRecordEntity;
import io.philoyui.qmier.qmiermanager.entity.QmierImageEntity;
import io.philoyui.qmier.qmiermanager.entity.enu.ArticleStatus;
import io.philoyui.qmier.qmiermanager.service.ArticleRecordService;
import io.philoyui.qmier.qmiermanager.service.SiteCategoryService;
import io.philoyui.qmier.qmiermanager.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleRecordPageService extends PageService<ArticleRecordEntity,Long>  implements PageDomainProvider<ArticleRecordEntity> {

    @Autowired
    private ArticleRecordService articleRecordService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteCategoryService siteCategoryService;

    @Override
    public PageObject<ArticleRecordEntity> paged(SearchFilter searchFilter) {
        return articleRecordService.paged(searchFilter);
    }

    @Override
    protected PageConfig initializePageConfig(PageContext pageContext) {
        return new PageConfig(pageContext)
                .withDomainName("article_record")
                .withDomainClass(ArticleRecordEntity.class)
                .withDomainChineseName("文章记录")
                .withFieldDefinitions(
                        new LongFieldDefinition("id", "ID"),
                        new DomainLongFieldDefinition("siteId", "网站",siteService).required(),
                        new DomainLongFieldDefinition("siteCategoryId", "网站版区",siteCategoryService).required(),
                        new StringFieldDefinition("title", "标题").required(),
                        new StringFieldDefinition("detailId","详情页ID").required(),
                        new StringFieldDefinition("detailUrl","详情页地址").required(),
                        new EnumFieldDefinition("status","状态", ArticleStatus.class).required(),
                        new StringFieldDefinition("author","作者").required(),
                        new StringFieldDefinition("region","区域").required(),
                        new DateFieldDefinition("createdTime","创建时间").required(),
                        new DateFieldDefinition("processTime","处理时间").required(),
                        new EditorFieldDefinition("content","正文").required(),
                        new StringFieldDefinition("imagePaths","图片地址").required(),
                        new StringFieldDefinition("city","城市").required(),
                        new StringFieldDefinition("qmierName","网名"),
                        new IntFieldDefinition("replyCount","回复数"),
                        new IntFieldDefinition("readCount","阅读数"),
                        new TextAreaFieldDefinition("comments","评论")

                        )
                .withTableColumnDefinitions(
                        "#checkbox_4",
                        "siteId_8",
                        "siteCategoryId_10",
                        "title_32",
                        "author_10",
                        "region_5",
                        "status_5",
                        "replyCount_4",
                        "createdTime_10",
                        "#operation_12"
                )
                .withFilterDefinitions("title_like","author","region","siteCategoryId","siteId","status")
                .withSortDefinitions("createdTime_desc","createdTime_asc")
                .withTableAction(
                        new CreateOperation(),
                        new BatchDeleteOperation(),
                        new BatchActionOperation("分析","analysis", ButtonStyle.Orange)
                )
                .withColumnAction(
                        new NewPageOperation("原页面","/admin/article_record/detailUrl?id=#id#&siteId=#siteId#","原页面","id","siteId"),
                        new ConfirmOperation("fetch","抓取"),
                        new EditOperation(),
                        new DeleteOperation()
                )
                .withFormItemDefinition(
                        "siteId_rw",
                        "siteCategoryId_rw",
                        "title_rw",
                        "detailUrl_rw",
                        "author_rw",
                        "region_rw",
                        "status_rw",
                        "content_rw",
                        "imagePaths_r",
                        "qmierName_rw",
                        "comments_rw",
                        "readCount_rw",
                        "replyCount_rw"
                        );
    }

    @Override
    public ArticleRecordEntity get(String id) {
        return articleRecordService.get(Long.parseLong(id));
    }

    @Override
    public ArticleRecordEntity get(SearchFilter searchFilter) {
        return articleRecordService.get(searchFilter);
    }

    @Override
    public void saveOrUpdate(ArticleRecordEntity articleRecordEntity) {
        articleRecordService.insert(articleRecordEntity);
    }

    @Override
    public void delete(ArticleRecordEntity articleRecordEntity) {
        articleRecordService.delete(articleRecordEntity.getId());
    }

    @Override
    public void delete(List<ArticleRecordEntity> entities) {
        articleRecordService.delete(entities);
    }

    @Override
    public Object findByReferId(String referId) {
        return articleRecordService.get(Long.parseLong(referId));
    }

    @Override
    public String getDomainName() {
        return "article_record";
    }

    @Override
    public String getDomainChineseName() {
        return "文章记录";
    }

    @Override
    public String getDisplayFieldName() {
        return "title";
    }

    @Override
    public List<ArticleRecordEntity> findAll() {
        return articleRecordService.list(SearchFilter.getDefault());
    }
}
