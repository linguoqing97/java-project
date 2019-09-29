package com.example.toutiao.controller;

import com.example.toutiao.Dao.UserDAO;
import com.example.toutiao.model.EntityType;
import com.example.toutiao.model.HostHolder;
import com.example.toutiao.model.News;
import com.example.toutiao.model.ViewObject;
import com.example.toutiao.service.LikeService;
import com.example.toutiao.service.NewsService;
import com.example.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NewsService newsService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        for(News news:newsList){
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));

            if (localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                vo.set("like", 0);
            }


            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/","/Index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String Index(Model model){
        model.addAttribute("vos",getNews(0,0,100));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop",defaultValue = "0") int pop ){
        model.addAttribute("vos",getNews(userId,0,20));
        model.addAttribute("pop",pop);
        return "home";
    }
}
