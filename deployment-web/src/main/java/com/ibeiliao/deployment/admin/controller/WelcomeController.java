package com.ibeiliao.deployment.admin.controller;

import com.ibeiliao.deployment.admin.annotation.authority.AllowAnonymous;
import com.ibeiliao.deployment.admin.context.AdminContext;
import com.ibeiliao.deployment.admin.context.AdminLoginUser;
import com.ibeiliao.deployment.admin.context.AppConstants;
import com.ibeiliao.deployment.admin.service.account.AdminAccountService;
import com.ibeiliao.deployment.admin.utils.resource.MenuResource;
import com.ibeiliao.deployment.admin.vo.account.AdminAccount;
import com.ibeiliao.deployment.base.ApiCode;
import com.ibeiliao.deployment.common.util.ParameterUtil;
import com.ibeiliao.deployment.exception.ServiceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.List;

/**
 * 欢迎界面
 * @author ten 2015/10/17.
 */
@Controller
@RequestMapping("/admin/")
public class WelcomeController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    @Autowired
    AdminAccountService adminAccountService;


    /**
     * 增加管理员主页，xhtml 仅用于展示页面
     * @return
     */
    @RequestMapping("welcome.xhtml")
    @MenuResource("欢迎界面")
    @AllowAnonymous
    public String index() {
        return "redirect:/admin/deploy/list.xhtml";
    }
    
    /**
     * 登录接口,sso登录时回调
     * @param request
     * @param response
     * @param account
     * @param token
     * @return
     */
    @RequestMapping("/login.do")
    @MenuResource("授权登录")
    @AllowAnonymous
    public String login(HttpServletRequest request, HttpServletResponse response, String account, String token){

        ParameterUtil.assertNotBlank(account, "账户不能为空");
        ParameterUtil.assertNotBlank(token, "token不能为空");

        // TODO 提供一个账号登录
        String t = "";//ssoProvider.getSessionId(account);
        if (StringUtils.isEmpty(t) || !t.equals(token)){
            logger.error("账户:{} 登录信息不匹配, 重新跳转登录页面 | token: {}, t: {}", account, token, t);
            return "redirect:" + AppConstants.SSO_LOGIN_URL;
        }
        AdminAccount adminAccount = adminAccountService.getByAccount(account);
        if (adminAccount == null){
            logger.warn("账户:{} 登录账户不存在 | ", account);
            throw new ServiceException(ApiCode.UNAUTHORIZED_ACCESS, "账户不存在");
        }

        List<Integer> appIdList = adminAccountService.listAccountApps(adminAccount.getUid());
        if (CollectionUtils.isEmpty(appIdList) || !appIdList.contains(AppConstants.APP_ID_DEFAULT)){
            logger.warn("账户:{} 没有应用权限 | ", account);
            throw new ServiceException(ApiCode.UNAUTHORIZED_ACCESS, "没有应用权限");
        }

        AdminLoginUser loginUser = new AdminLoginUser();
        loginUser.setAccountId(adminAccount.getUid());
        loginUser.setAppId(AppConstants.APP_ID_DEFAULT);

        AdminContext.saveToCookie(response, loginUser);
        logger.info("登录成功 | uid: {}", adminAccount.getUid());
        //返回欢迎页
        return "redirect:/admin/welcome.xhtml";
    }

    /**
     * 退出登录接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/logout.do")
    @MenuResource("退出登录")
    @AllowAnonymous
    public String logout(HttpServletRequest request, HttpServletResponse response){
        AdminContext.clearCookie(response);
        return "redirect:" + AppConstants.SSO_LOGIN_URL;
    }


    @RequestMapping("/error.xhtml")
    @AllowAnonymous
    public String errorMsg(HttpServletRequest request, HttpServletResponse response){
        String message = ServletRequestUtils.getStringParameter(request, "message", "");
        try {
            request.setAttribute("message", URLDecoder.decode(message, "UTF-8"));
        }catch (Exception e){
            logger.error("错误页面发生错误 | msg:{}", message, e);
        }
        return "exception";
    }


}
