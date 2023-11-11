package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工新增时传递的数据模型")
public class EmployeeDTO implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value ="用户名",required = true)
    private String username;

    @ApiModelProperty(value ="姓名",required = true)
    private String name;

    @ApiModelProperty(value ="手机号",required = true)
    private String phone;

    @ApiModelProperty(value ="性别",required = true)
    private String sex;

    @ApiModelProperty(value ="身份证号",required = true)
    private String idNumber;

}
