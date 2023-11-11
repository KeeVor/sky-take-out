package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);


    /**
     * 新增员工
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工状态修改
     */
    void updateStatus(long id,int status);

    /**
     * 员工信息回显
     * @param id
     * @return
     */
    Employee getEmployee(long id);

    /**
     * 员工信息修改
     * @param employeeDTO
     * @return
     */
    void updateEmployee(EmployeeDTO employeeDTO);
}
