package com.TanNgee.bilibili.service.util;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.TanNgee.bilibili.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * FastDFS 工具类
 *
 * @Author TanNgee
 * @Date 2022/7/2 19:25
 **/
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final String DEFAULT_GROUP = "group1";  //默认分组

    private static final int SLICE_SIZE = 1024 * 1024 * 2;  //分片大小

    @Value("${fdfs.http.storage-addr}")   //注入存储服务器路径
    private String httpFdfsStorageAddr;

    /**
     * 获取文件类型
     *
     * @param file
     * @return
     */
    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }


    public String uploadCommonFile(File file, String fileType) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        StorePath storePath = fastFileStorageClient.uploadFile(new FileInputStream(file),
                file.length(), fileType, metaDataSet);
        return storePath.getPath();
    }

    /**
     * 上传可以断点续传的文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     * 修改续传文件的内容
     *
     * @param file
     * @param filePath
     * @param offset   偏移量
     * @throws Exception
     */
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    /**
     * 上传文件分片
     *
     * @param file         分片文件
     * @param fileMd5      文件的MD5值
     * @param sliceNo      分片的序号
     * @param totalSliceNo 总共的分片数
     * @return
     * @throws Exception
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常！");
        }
        String pathKey = PATH_KEY + fileMd5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5; // 已经上传的总大小
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;   // 已经上传的分片

        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);   // 从redis中取

        Long uploadedSize = 0L;
        // 判断是否已经上传
        if (!StringUtil.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }

        if (sliceNo == 1) { //上传的是第一个分片
            String path = this.uploadAppenderFile(file);  //第一个分片，直接上传，获得文件路径
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败！");
            }

            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadedNoKey, "1");  //已经上传第一个分片
        } //不是第一个分片
        else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);  //对redis 中已经上传的序号，原子+1
        }

        // 修改历史上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));  // 在redis中更新
        //如果所有分片全部上传完毕，则清空redis里面相关的key和value
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSliceNo)) { //上传完毕
            resultPath = redisTemplate.opsForValue().get(pathKey);  //文件上传完毕，返回文件的完整路径
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            redisTemplate.delete(keyList);  //清除redis中相关value
        }
        return resultPath;
    }

    /**
     * 把一个文件转换成分片
     *
     * @param multipartFile
     * @throws Exception
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);

        //生成临时文件，将MultipartFile转为File
        File file = this.multipartFileToFile(multipartFile);

        long fileLength = file.length();
        int count = 1;

        for (int i = 0; i < fileLength; i += SLICE_SIZE) {

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);

            byte[] bytes = new byte[SLICE_SIZE]; //分片大小
            int len = randomAccessFile.read(bytes); //读取出来的长度

            String path = "D:\\temp\\tempfile\\" + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }

        //删除临时文件
        file.delete();
    }

    /**
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFileName = multipartFile.getOriginalFilename();
        String[] fileName = originalFileName.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }


    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);  //查询文件信息
        long totalFileSize = fileInfo.getFileSize();  //大小
        String url = httpFdfsStorageAddr + path;  //实际的文件路径

        Enumeration<String> headerNames = request.getHeaderNames();  //获取请求头
        Map<String, Object> headers = new HashMap<>();

        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }

        String rangeStr = request.getHeader("Range");  //范围
        String[] range;

        if (StringUtil.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }

        range = rangeStr.split("bytes=|-"); // 对range进行拆分

        long begin = 0;
        if (range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }

        long end = totalFileSize - 1;
        if (range.length >= 3) {
            end = Long.parseLong(range[2]);
        }
        long len = (end - begin) + 1;  //文件的长度

        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;

        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        HttpUtil.get(url, headers, response);
    }

    /**
     * @param url
     * @param localPath
     */
    public void downLoadFile(String url, String localPath) {
        fastFileStorageClient.downloadFile(DEFAULT_GROUP, url,
                new DownloadCallback<String>() {
                    @Override
                    public String recv(InputStream ins) throws IOException {
                        File file = new File(localPath);
                        OutputStream os = new FileOutputStream(file);
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = ins.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        os.close();
                        ins.close();
                        return "success";
                    }
                });
    }
}
