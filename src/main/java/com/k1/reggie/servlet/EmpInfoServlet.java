package com.k1.reggie.servlet; /**
 * @Author：kkoneone11
 * @name：${NAME}
 * @Date：2023/4/27 21:30
 */

import com.k1.reggie.entity.Employee;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/EmpInfoServlet")
public class EmpInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Empname = (String) request.getSession().getAttribute("employeeName");
        Long Empid = (Long) request.getSession().getAttribute("employee");
        String Empphone = (String)  request.getSession().getAttribute("phone");

        List<Employee> list = new ArrayList<>();

        Employee emp = new Employee();
        emp.setName(Empname);
        emp.setId(Empid);
        emp.setPhone(Empphone);
        list.add(emp);

        HttpSession session = request.getSession();
        session.setAttribute("employeeList",list);
        response.sendRedirect("/jsp/Listener.jsp");
    }
}
