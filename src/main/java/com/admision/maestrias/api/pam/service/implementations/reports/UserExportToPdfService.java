package com.admision.maestrias.api.pam.service.implementations.reports;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import com.admision.maestrias.api.pam.entity.AspiranteEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UserExportToPdfService extends ReportAbstract {

    public void writeTableData(PdfPTable table, Object data) {
        List<AspiranteEntity> list = (List<AspiranteEntity>) data;

        // for auto wide by paper  size
        table.setWidthPercentage(100);
        // cell
        PdfPCell cell = new PdfPCell();
        int number = 0;
        for (AspiranteEntity item : list) {
            number += 1;
            cell.setPhrase(new Phrase(String.valueOf(number), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getNombre(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getApellido(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getLugar_nac(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getDepartamento_residencia(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getMunicipio_residencia(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getDireccion_residencia(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getTelefono(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getDocumentType(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getNo_documento(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getFecha_exp_di().toString().split(" ")[0], getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getFecha_nac().toString().split(" ")[0], getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getGenero(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getEstadoCivilTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getZonaResidenciaTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getGrupoEtnicoTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getPuebloIndigenaTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getPoseeDiscapacidadTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getCapacidadxcepcionalTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getTipoVinculacionTypes(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getEmpresa_trabajo(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getPais_trabajo(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getDepartamento_trabajo(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getMunicipio_trabajo(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getDireccion_trabajo(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getEstudios_pregrado(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getEstudios_posgrados(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getPromedioPregrado(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getExp_laboral(), getFontContent()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(item.getEs_egresado_ufps().toString(), getFontContent()));
            table.addCell(cell);

        }

    }


    public void exportToPDF(HttpServletResponse response, Object data) throws IOException {
        response = initResponseForExportPdf(response, "apirantes");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Paragraph title = new Paragraph("Reporte Aspirantes", getFontTitle());
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitel = new Paragraph("Fecha reporte :"+ new Date(), getFontSubtitle());
        subtitel.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(subtitel);

        enterSpace(document);

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

        PdfPTable tableHeader = new PdfPTable(10);
        writeTableHeaderPdf(tableHeader, headers);
        document.add(tableHeader);

        PdfPTable tableData = new PdfPTable(10);
        writeTableData(tableData, data);
        document.add(tableData);

        document.close();
    }

}