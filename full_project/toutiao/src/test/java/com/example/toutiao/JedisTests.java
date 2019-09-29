package com.example.toutiao;

import com.example.toutiao.model.User;
import com.example.toutiao.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class JedisTests {

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testObject(){
        User user = new User();
        user.setHeadUrl("http://images.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("def");
        jedisAdapter.setObject("user1", user);

        User u = jedisAdapter.getObject("user1", User.class);
        System.out.print(ToStringBuilder.reflectionToString(u));
    }
}
