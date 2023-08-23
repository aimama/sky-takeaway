package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //对用户输入的密码进行MD5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //传DTO最后转成实体类后再给Mapper
        Employee employee = new Employee();
        //使用BeanUtils中的工具类直接拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置创建时间与修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //设置默认允许登录
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码，采用MD5设置
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前记录人的ID与修改人的ID TODO 后期需要改写为当前用户登录的ID(完成。使用ThreadLocal存取当前线程的ID)
        System.out.println("当前线程ID：" + Thread.currentThread().getId());
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //使用PageHelper进行分页

        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        //获取总数
        long total = page.getTotal();
        //TODO 对数据库返回的数据利用PageHelper进行自动分页后，使用getResult()获取分页后的集合
        List<Employee> records = page.getResult();
        //TODO 对查询出来的结果进行封装返回
        return new PageResult(total, records);
    }

    /**
     * 启用或禁用账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //为了通用性，采用Mapper的update
        //所以传输一个实体类
        //TODO 以下方法须在实体类中添加@Builder，可以获取实体类对象,再传入参数
        Employee employee = Employee.builder().
                status(status).
                id(id).
                build();
        employeeMapper.update(employee);
    }

    /**
     * 根据Id获取员工信息（回显）
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        //防止密码显示泄露，安全更高
        //在返回数据后再设置密码密文，再返回
        employee.setPassword("****");
        return employee;
    }

    /**
     * 修改员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
//                .updateTime(LocalDateTime.now())
//                .updateUser(Thread.currentThread().getId())     //设置修改人
                .build();

        BeanUtils.copyProperties(employeeDTO, employee);

        employeeMapper.update(employee);
    }

}
