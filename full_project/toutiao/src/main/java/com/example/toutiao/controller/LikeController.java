package com.example.toutiao.controller;

import com.example.toutiao.async.EventModel;
import com.example.toutiao.async.EventProducer;
import com.example.toutiao.async.EventType;
import com.example.toutiao.model.EntityType;
import com.example.toutiao.model.HostHolder;
import com.example.toutiao.model.News;
import com.example.toutiao.service.LikeService;
import com.example.toutiao.service.NewsService;
import com.example.toutiao.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newId") int newsId) {
        News news = newsService.getById(newsId);
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).
                                setActorId(hostHolder.getUser().getId()).setEntityId(newsId).
                                setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newId") int newsId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        //
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

}
