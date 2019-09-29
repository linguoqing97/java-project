package com.example.toutiao.Dao;

import com.example.toutiao.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.*;

import javax.jws.soap.SOAPBinding;

@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = "name,password,salt,head_url";
    String SELETE_FIELDS = "id,name,password,salt,head_url";
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
        ") Values (#{name}, #{password}, #{salt}, #{headUrl})"})
    int addUser(User user);

    @Select({"select ",SELETE_FIELDS, " from ", TABLE_NAME, "where id =#{id}"})
    User selectById(int id);

    @Select({"select ",SELETE_FIELDS, " from ", TABLE_NAME, "where name =#{name}"})
    User selectByName(String name);

    @Update({"update ", TABLE_NAME ," set password =#{password} where id =#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME , " where id=#{id}"})
    void deleteById(int id);
}
