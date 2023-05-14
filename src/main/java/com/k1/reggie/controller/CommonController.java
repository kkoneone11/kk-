package com.k1.reggie.controller;

import com.k1.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;


/**
 * 上载图片
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    //在配置文件设置基本路径并获取赋值给basepath
    @Value("${reggie.path}")
    private String basePath;


    /**
     * 上载数据，MultipartFile 后面的变量只能是和前端提交来的表单的名字一样不能取其他;
     *
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file中接收到的数据会暂时保存在一个目录里，如果不及时上载数据就会被删除
        log.info(file.toString());

        //原文件名,名字一般不用原文件名，如果名字重复导致覆盖原文件，可以取里面的后缀
        String originalFilename = file.getOriginalFilename();

        //取出.jpg的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //利用uuid随机生成一个名字
        String fileName = UUID.randomUUID() + suffix;

        //判断目标路径是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        //生成一个文件
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return R.success(fileName);
    }


    /**
     * 下载图片
     *
     * @throws FileNotFoundException
     */

    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) throws IOException {
        //输入流，定义从哪里写入数据
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

        //输出流
        ServletOutputStream outputStream = response.getOutputStream();
        int len = 0;
        byte[] bytes = new byte[1024];
        //向bytes数组里面写入数据，写完数据会返回-1
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
        }
    }
}
