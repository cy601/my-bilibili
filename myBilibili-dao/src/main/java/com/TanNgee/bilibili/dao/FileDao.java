package com.TanNgee.bilibili.dao;


import com.TanNgee.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author TanNgee
 * @Date 2022/7/2 20:50
 **/
@Mapper
public interface FileDao {
    Integer addFile(File file);

    File getFileByMD5(String md5);
}
