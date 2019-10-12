package com.liuchao.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liuchao.demo.entity.User;
import com.liuchao.demo.mapper.UserMapper;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/loginInit")
    public String loginInit(){
        return "login.html";
    }

    @RequestMapping("/login")
    public String login(@RequestParam("userName")String userName,
                        @RequestParam("password")String password,
                        Model model){
        User user=new User();
        user.setUserName(userName);
        user.setPassword(password);
        User byNamePassword = userMapper.findByNamePassword(user);
        if(byNamePassword==null){
            model.addAttribute("error","loginerror");
            return "login.html";
        }else{
            model.addAttribute("user",user);
            return "index.html";
        }

    }

    @RequestMapping("/qqLoginInit")
    public void qqLoginInit(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect("https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101807700&redirect_uri=http://127.0.0.1/qqLoginCallback&state=123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/qqLoginCallback")
    public String qqLoginCallback(HttpServletRequest request,Model model) throws QQConnectException, ScriptException {
       /* AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
        String accessToken = accessTokenObj.getAccessToken();
        OpenID openIDObj =  new OpenID(accessToken);
       String openID = openIDObj.getUserOpenID();
        User user = userMapper.findByOpenId(openID);
        model.addAttribute("user",user);*/
        String code = request.getParameter("code");
        String url="https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=101807700&client_secret=fcbff0a9095cb9323d1408afc36e1c55&code="+code+"&redirect_uri=http://127.0.0.1/qqLoginCallback";

        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String body = forEntity.getBody();
        Map<String, String> map = getValue(body);
        String token = map.get("access_token");
        String openIdUrl="https://graph.qq.com/oauth2.0/me?access_token="+token;
        ResponseEntity<String> forEntity1 = restTemplate.getForEntity(openIdUrl, String.class);
        String body1 = forEntity1.getBody();
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");
        scriptEngine.eval("function callback(data){return data.openid;}");
        String openId = scriptEngine.eval(body1).toString();
        User user = userMapper.findByOpenId(openId);
        if(user==null){
            user=new User();
            user.setUserName("空");
            user.setPassword("1234");

        }
        model.addAttribute("user",user);
        return "index.html";
    }

    public Map<String,String> getValue(String str){
        String[] split = str.split("&");
        Map<String,String> map=new HashMap<String,String>();
        for(String keyValue:split){
            String[] split1 = keyValue.split("=");
            map.put(split1[0],split1[1]);
        }

        return map;
    }
}
