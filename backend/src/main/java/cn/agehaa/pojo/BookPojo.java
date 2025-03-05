package cn.agehaa.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "bookresource")
public class BookPojo {

    @TableId(value = "id")
    private Integer id;

    @TableField(value = "code")
    private String code;

    @TableField(value = "title")
    private String title;

    @TableField(value = "author")
    private String author;

    @TableField(value = "cover")
    private String cover;

    @TableField(value = "category")
    private String category;

    @TableField(value = "description")
    private String description;

    @TableField(value = "download_url")
    private String downloadUrl;

}
