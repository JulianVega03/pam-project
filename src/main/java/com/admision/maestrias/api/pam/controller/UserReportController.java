package com.admision.maestrias.api.pam.controller;

import com.admision.maestrias.api.pam.service.implementations.reports.UserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/report/user")
public class UserReportController {

    @Autowired
    UserReportService userReportService;

    @GetMapping("/pdf/all")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        this.userReportService.exportToPdf(response);
    }

    @GetMapping("/excel/all")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        this.userReportService.exportToExcel(response);
    }

    @GetMapping("/excel/{id}")
    public void exportToExcelPorUser(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
        this.userReportService.exportToExcelPorUser(id, response);
    }
}