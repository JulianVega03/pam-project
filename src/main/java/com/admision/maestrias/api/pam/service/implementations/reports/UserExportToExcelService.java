package com.admision.maestrias.api.pam.service.implementations.reports;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.admision.maestrias.api.pam.entity.AspiranteEntity;
import java.io.IOException;
import java.util.List;

@Service
public class UserExportToExcelService extends ReportAbstract {

    public void writeTableData(Object data) {
        // data
        List<AspiranteEntity> list = (List<AspiranteEntity>) data;

        // font style content
        CellStyle style = getFontContentExcel();

        // starting write on row
        int startRow = 2;

        // write content
        int numero = 1;
        for (AspiranteEntity aspiranteEntity : list) {
            Row row = sheet.createRow(startRow++);
            int columnCount = 0;
            createCell(row, columnCount++, numero++, style);
            createCell(row, columnCount++, aspiranteEntity.getNombre(), style);
            createCell(row, columnCount++, aspiranteEntity.getApellido(), style);
            createCell(row, columnCount++, aspiranteEntity.getLugar_nac(), style);
            createCell(row, columnCount++, aspiranteEntity.getDepartamento_residencia(), style);
            createCell(row, columnCount++, aspiranteEntity.getMunicipio_residencia(), style);
            createCell(row, columnCount++, aspiranteEntity.getDireccion_residencia(), style);
            createCell(row, columnCount++, aspiranteEntity.getTelefono(), style);
            createCell(row, columnCount++, aspiranteEntity.getDocumentType(), style);
            createCell(row, columnCount++, aspiranteEntity.getNo_documento(), style);
            createCell(row, columnCount++, aspiranteEntity.getFecha_exp_di().toString().split(" ")[0], style);
            createCell(row, columnCount++, aspiranteEntity.getFecha_nac().toString().split(" ")[0], style);
            createCell(row, columnCount++, aspiranteEntity.getGenero(), style);
            createCell(row, columnCount++, aspiranteEntity.getEstadoCivilTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getZonaResidenciaTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getGrupoEtnicoTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getPuebloIndigenaTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getPoseeDiscapacidadTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getCapacidadxcepcionalTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getTipoVinculacionTypes(), style);
            createCell(row, columnCount++, aspiranteEntity.getEmpresa_trabajo(), style);
            createCell(row, columnCount++, aspiranteEntity.getPais_trabajo(), style);
            createCell(row, columnCount++, aspiranteEntity.getDepartamento_trabajo(), style);
            createCell(row, columnCount++, aspiranteEntity.getMunicipio_trabajo(), style);
            createCell(row, columnCount++, aspiranteEntity.getDireccion_trabajo(), style);
            createCell(row, columnCount++, aspiranteEntity.getEstudios_pregrado(), style);
            createCell(row, columnCount++, aspiranteEntity.getEstudios_posgrados(), style);
            createCell(row, columnCount++, aspiranteEntity.getPromedioPregrado(), style);
            createCell(row, columnCount++, aspiranteEntity.getExp_laboral(), style);
            createCell(row, columnCount++, aspiranteEntity.getEs_egresado_ufps(), style);
        }
    }


    public void exportToExcel(HttpServletResponse response, Object data) throws IOException {
        newReportExcel();

        // response  writer to excel
        response = initResponseForExportExcel(response, "reporte_aspirantes");
        ServletOutputStream outputStream = response.getOutputStream();


        // write sheet, title & header
        String[] headers = new String[]{"No", "Nombres", "Apellidos", "Lugar de nacimiento", "Deparmento de residencia",
            "Municipio de residencia", "Dirección de residencia", "Teléfono", "Tipo de documento de identidad",
            "Número documento identidad", "Fecha de Expedición del documento de identidad", "Fecha de nacimiento",
            "Sexo biológico", "Estado Civil", "Zona de residencia", "Grupo Étnico", "Pueblo indígena",
            "Persona con discapacidad", "Persona con Capacidad Excepcional", "Tipo de Vinculación al Programa",
            "Empresa donde trabaja", "País donde trabaja", "Deparmento donde trabaja", "Municipio donde trabaja",
            "Dirección donde trabaja", "Estudios a nivel de formación universitaria (pre-grado)",
            "Estudios a nivel de formación avanzada, especifique los Títulos obtenidos (postgrado)", "Promedio Ponderado Acumulado",
            "Información de la Experiencia Laboral", "Es egresado de la UFPS"
        };
        writeTableHeaderExcel("Sheet User", "Reporte Aspirantes", headers);

        // write content row
        writeTableData(data);

        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}