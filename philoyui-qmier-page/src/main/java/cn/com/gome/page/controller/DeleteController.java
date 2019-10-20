package cn.com.gome.page.controller;

import cn.com.gome.cloud.openplatform.common.Restrictions;
import cn.com.gome.cloud.openplatform.common.SearchFilter;
import cn.com.gome.page.core.PageConfig;
import cn.com.gome.page.core.PageManager;
import cn.com.gome.page.core.PageService;
import cn.com.gome.page.excp.GmosException;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@RestController
@RequestMapping("/admin")
public class DeleteController {

    @Autowired
    private PageManager pageManager;

    @DeleteMapping("/{domainName}/delete")
    public ResponseEntity<String> delete(HttpServletRequest request, @PathVariable String domainName){

        PageService pageService = pageManager.findServiceByDomainName(domainName);
        PageConfig pageConfig = pageService.getPageConfig();

        String id = request.getParameter("id");
        Serializable entity = null;

        String loginUserFieldName = pageConfig.getLoginUserFieldName();
        if(!Strings.isNullOrEmpty(loginUserFieldName)){
            SearchFilter searchFilter = SearchFilter.getDefault();
            searchFilter.add(Restrictions.eq(loginUserFieldName,pageManager.getLoginUsername()));
            searchFilter.add(Restrictions.eq("id",id));
            entity = pageService.get(searchFilter);
        }else{
            entity = pageService.get(id);
        }

        if(entity==null){
            throw new GmosException("请求对象不存在");
        }

        try {
            pageService.delete(entity);
            return ResponseEntity.ok("success");
        }catch (GmosException e){
            return ResponseEntity.status(503).body(e.getMessage());
        }

    }

}
