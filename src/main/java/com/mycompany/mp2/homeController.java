package com.mycompany.mp2;

///*
// * CopyRight cosmos
// */
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mycompany.mp2.modal.AllProduct;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mycompany.mp2.modal.MainCompany;
import com.mycompany.mp2.modal.numpojo;
import java.lang.reflect.Type;

/**
 *
 * @author saif
 */
//@WebServlet(urlPatterns = {"/homeController"})
public class homeController extends HttpServlet {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    List<AllProduct> alllist = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int start = 0;
            int end = 10;
            List<MainCompany> mainlist = new ArrayList<>();
            List<numpojo> numli = new ArrayList<>();
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("username");
            String query = "select * from maincompany";
            con = ConnectionClass.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            List<MainCompany> li = MainCompany.getCate(rs);
            con.close();
            System.out.println("size of the list after shop " + li.size());
            double num = li.size() / 10;
            int ognum = (int) num + 1;
            for (int i = 1; i <= ognum; i++) {
                numpojo np = new numpojo();
                np.setNum(i);
                numli.add(np);
            }
            String paginationnumber = request.getParameter("number");
            if (paginationnumber != null) {
                int ogpagination = Integer.parseInt(paginationnumber);
                if (ogpagination == 1) {
                    start = 0;
                    end = 10;
                } else {
                    int finalnum = ogpagination - 1;
                    start = finalnum * 10 + 1 - 1;
                    int checkend = 10 * ogpagination;
                    if (checkend >= li.size()) {
                        end = li.size();
                    } else {
                        end = checkend;
                    }
                }
                System.out.println("2nd " + li.get(10));
                System.out.println(start + "<-s e->" + end);
                mainlist = li.subList(start, end);
            } else {
                System.out.println(start + "<-s 1st e->" + end);
                mainlist = li.subList(start, end);
            }
            int price = getPrice();
            request.setAttribute("allvalue", mainlist);
            request.setAttribute("username", username);
            request.setAttribute("count", numli);
            request.setAttribute("gonogo", "false");
            request.setAttribute("price", price);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String query1 = "select brandname,company,genericname from producttable";
            System.out.println(query1 + " <><>");
            con = ConnectionClass.getConnection();
            ps = con.prepareStatement(query1);
            rs = ps.executeQuery();
            alllist = AllProduct.getList2(rs);
            con.close();
        } catch (SQLException s) {
        }
        Gson gson = new Gson();
        JsonElement jelement = gson.toJsonTree(alllist, (Type) new TypeToken<List<AllProduct>>() {
        }.getType());
        JsonArray ja = jelement.getAsJsonArray();
        response.setContentType("application/json");
        response.getWriter().print(ja);
    }

    private int getPrice() {
        int price = 0;
        try {
            String query = "select price from priceanddiscount";
            con = ConnectionClass.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                price = rs.getInt("price");
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return price;
    }
}
