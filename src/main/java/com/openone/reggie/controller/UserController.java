package com.openone.reggie.controller;

import com.openone.reggie.common.R;
import com.openone.reggie.entity.User;
import com.openone.reggie.service.UserService;
import com.openone.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> getMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        //判断是否为空
        if (StringUtils.isNotBlank(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);
            //需要将生成的验证码保存到Session
            session.setAttribute(phone, code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            return R.success("验证码已发送");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param session
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpSession session,@RequestBody Map map ){
       log.info(map.toString());
       //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
         //获取session中的验证码
        Object codeInSession = session.getAttribute(phone);

        if(!code.equals(codeInSession)||code==null){
           return R.error("验证码错误");
        }

        User user = userService.selectByPhone(phone);

        if (user==null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.add(user);
        }

        session.setAttribute("user",user.getId());

        return R.success(user);

    }


    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");

        return R.success("成功退出");

    }

}
