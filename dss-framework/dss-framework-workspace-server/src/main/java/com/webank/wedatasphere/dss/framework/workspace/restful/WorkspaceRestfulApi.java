/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.bean.DSSWorkspace;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageDemoMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.HomepageVideoVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.OnestopMenuVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.dto.response.WorkspaceFavoriteVo;
import com.webank.wedatasphere.dss.framework.workspace.bean.vo.DepartmentVO;
import com.webank.wedatasphere.dss.framework.workspace.service.DSSWorkspaceService;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(path = "/dss/framework/workspace", produces = {"application/json"})
public class WorkspaceRestfulApi {
    @Autowired
    private DSSWorkspaceService dssWorkspaceService;

    @RequestMapping(path = "workspaces", method = RequestMethod.GET)
    public Message getAllWorkspaces(HttpServletRequest req) {
        // TODO: Order By time
        String username = SecurityFilter.getLoginUsername(req);
        List<DSSWorkspace> workspaces = dssWorkspaceService.getWorkspaces(username);
        return Message.ok().data("workspaces", workspaces);
    }

    @RequestMapping(path = "/workspaces/{id}", method = RequestMethod.GET)
    public Message getWorkspacesById(HttpServletRequest req, @PathVariable("id") Long id) {
        DSSWorkspace workspace = dssWorkspaceService.getWorkspacesById(id);
        return Message.ok().data("workspace", workspace);
    }

    @RequestMapping(path = "/workspaces/departments", method = RequestMethod.GET)
    public Message getAllWorkspaceDepartments(HttpServletRequest req) {
        List<DepartmentVO> departments = dssWorkspaceService.getDepartments();
        return Message.ok().data("departments", departments);
    }

    @RequestMapping(path = "/workspaces/exists", method = RequestMethod.GET)
    public Message getUsernameExistence(HttpServletRequest req, @RequestParam(required = false, name = "name") String name) {
        boolean exists = dssWorkspaceService.existWorkspaceName(name);
        return Message.ok().data("workspaceNameExists", exists);
    }

    @RequestMapping(path = "/workspaces", method = RequestMethod.POST)
    public Message addWorkspace(HttpServletRequest req, @RequestBody Map<String, String> json) throws ErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        if (!dssWorkspaceService.checkAdmin(userName)) {
            return Message.error("您好，您不是管理员,没有权限建立工作空间");

        }
    /*    String name = json.get("name").getTextValue();
        if (dssWorkspaceService.existWorkspaceName(name)) {
            return Message.error("工作空间名重复");
        }
        String department = String.valueOf(json.get("department").getIntValue());
        String label = json.get("label").getTextValue();
        String description = json.get("description").getTextValue();
        String workspaceType = json.get("workspace_type").getTextValue();*/
        String name = json.get("name");
        if (dssWorkspaceService.existWorkspaceName(name)) {
            return Message.error("工作空间名重复");
        }
        String department = json.get("department");
        String label = json.get("label");
        String description = json.get("description");
        String workspaceType = json.get("workspace_type");

        String productName = "DSS";
        int workspaceId = dssWorkspaceService.createWorkspace(name, label, userName, description, department, productName, workspaceType);
        return Message.ok().data("workspaceId", workspaceId);
    }

    @RequestMapping(path = "/workspaces/demos", method = RequestMethod.GET)
    public Message getAllHomepageDemos(HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageDemoMenuVo> homepageDemos = dssWorkspaceService.getHomepageDemos(isChinese);
        return Message.ok().data("demos", homepageDemos);
    }

    @RequestMapping(path = "/workspaces/videos", method = RequestMethod.GET)
    public Message getAllVideos(HttpServletRequest req) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        List<HomepageVideoVo> homepageVideos = dssWorkspaceService.getHomepageVideos(isChinese);
        return Message.ok().data("videos", homepageVideos);
    }

    @RequestMapping(path = "/workspaces/{workspaceId}/managements", method = RequestMethod.GET)
    public Message getWorkspaceManagements(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> managements = dssWorkspaceService.getWorkspaceManagements(workspaceId, username, isChinese);
        return Message.ok().data("managements", managements);
    }

    @RequestMapping(path = "workspaces/{workspaceId}/applications", method = RequestMethod.GET)
    public Message getWorkspaceApplications(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<OnestopMenuVo> applications = dssWorkspaceService.getWorkspaceApplications(workspaceId, username, isChinese);
        return Message.ok().data("applications", applications);
    }

    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.GET)
    public Message getWorkspaceFavorites(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @RequestParam(value = "type", required = false) String type) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
        Long menuApplicationId = dssWorkspaceService.getIdByTitleEn("Scriptis");
        if (favorites == null || favorites.size() == 0) {
            dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId, type == null ? "" : type);
            favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
        } else {
            boolean isHaveScripts = favorites.stream().anyMatch(favorite -> favorite.getMenuApplicationId().equals(menuApplicationId));
            if (!isHaveScripts) {
                dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId, type == null ? "" : type);
                favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
            }
        }
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.ok().data("favorites", favoriteVos);
    }

    @RequestMapping(path = "/workspaces/{workspaceId}/components", method = RequestMethod.GET)
    public Message getWorkspaceComponent(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @RequestParam(value = "type", required = false) String type) {
        String header = req.getHeader("Content-language").trim();
        boolean isChinese = "zh-CN".equals(header);
        String username = SecurityFilter.getLoginUsername(req);
        List<WorkspaceFavoriteVo> favorites = dssWorkspaceService.getWorkspaceFavorites(workspaceId, username, isChinese, type == null ? "" : type);
        Set<WorkspaceFavoriteVo> favoriteVos = new HashSet<>(favorites);
        return Message.ok().data("favorites", favoriteVos);
    }

    /**
     * 应用加入收藏，返回收藏后id
     *
     * @param req
     * @param json
     * @return
     */
    @RequestMapping(path = "/workspaces/{workspaceId}/favorites", method = RequestMethod.POST)
    public Message addFavorite(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(req);
//        Long menuApplicationId = json.get("menuApplicationId").getLongValue();
        Long menuApplicationId = Long.valueOf(json.get("menuApplicationId"));
        String type = json.get("type") == null ? "" : json.get("type");
        Long favoriteId = dssWorkspaceService.addFavorite(username, workspaceId, menuApplicationId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites/{applicationId}", method = RequestMethod.POST)
    public Message deleteFavorite(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @PathVariable("applicationId") Long applicationId, @RequestBody Map<String, String> json) {
        String username = SecurityFilter.getLoginUsername(req);
        String type = json.get("type") == null ? "" : json.get("type");
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, applicationId, workspaceId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }


    @RequestMapping(path = "/workspaces/{workspaceId}/favorites/{applicationId}", method = RequestMethod.GET)
    public Message deleteFavorite1(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @PathVariable("applicationId") Long applicationId, @RequestParam(value = "type", required = false) String type) {
        String username = SecurityFilter.getLoginUsername(req);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, applicationId, workspaceId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }

    @RequestMapping(path = "/workspaces/{workspaceId}/favorites/{favouritesId}", method = RequestMethod.DELETE)
    public Message deleteFavorite2(HttpServletRequest req, @PathVariable("workspaceId") Long workspaceId, @PathVariable("favouritesId") Long favouritesId, @RequestParam(value = "type", required = false) String type) {
        String username = SecurityFilter.getLoginUsername(req);
        Long favoriteId = dssWorkspaceService.deleteFavorite(username, favouritesId, workspaceId, type);
        return Message.ok().data("favoriteId", favoriteId);
    }


}
