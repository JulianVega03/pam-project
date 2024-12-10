package com.admision.maestrias.api.pam.service.implementations.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.admision.maestrias.api.pam.repository.*;
import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UserReportService {

    @Autowired
    UserExportToPdfService userExportToPdfService;

    @Autowired
    UserExportToExcelService userExportToExcelService;

    @Autowired
    AspiranteRepository aspiranteRepository;


    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<AspiranteEntity> data = aspiranteRepository.findAll();
        userExportToPdfService.exportToPDF(response, data);
    }


    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<AspiranteEntity> data = aspiranteRepository.findAll();
        userExportToExcelService.exportToExcel(response, data);
    }

    public void exportToExcelPorUser(Integer id, HttpServletResponse response) throws IOException {
        List<AspiranteEntity> data = Arrays.asList((AspiranteEntity) aspiranteRepository.findById(id).get());
        userExportToExcelService.exportToExcel(response, data);
    }


}