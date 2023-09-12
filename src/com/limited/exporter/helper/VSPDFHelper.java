package com.limited.exporter.helper;

import com.lcs.wc.document.*;
import com.lcs.wc.util.FormatHelper;
import com.limited.exporter.processor.VSSpecImagePageDataProcessorImpl;
import com.limited.wc.document.DocumentContentHelper;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Logger;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.log4j.LogR;
import wt.util.WTException;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VSPDFHelper {
    private static final Logger logger = LogR.getLogger (VSPDFHelper.class.getName ());

    public static void main (String[] args) {
        String name = "10236408_001 : SUMMER 24 10236408 MAS_001 : MAS INTIMATES\\UNI-KAN_002 : Detail Sketch Test_1691044952829.pdf";
        name = name.replaceAll ("[^a-zA-Z0-9_ -]", "");
        System.out.println (name);

    }

    public void printPDF (String fileName, Map<String, String> headerData, Map<String, byte[]> content) throws Exception {


        Document document = new Document ();
        PdfWriter.getInstance (document, Files.newOutputStream (Paths.get (fileName)));
        document.setMargins (5, 5, 25, 25);
        document.open ();
        document.setPageSize (PageSize.LETTER);

        PdfPTable table = new PdfPTable (1);

        PdfPCell cell = addTableHeader (headerData);
        cell.setBorder (0);
        cell.setPadding (0F);
        table.addCell (cell);

        table.addCell (createBlankDataCell ());
        table.addCell (createBlankDataCell ());


        if (content.size () > 0) {
            cell = createPDFCell (content.entrySet ().iterator ().next (), 480f);
            table.addCell (cell);

            document.add (table);
            document.newPage ();

            table = printContent (content);
            document.add (table);
        }

        document.close ();

    }

    private PdfPCell addTableHeader (Map<String, String> headerData) {
        PdfPTable table = new PdfPTable (4);
        PdfPCell cell;
        for (Map.Entry<String, String> entry : headerData.entrySet ()) {

            cell = new PdfPCell (new Phrase (entry.getKey (), getCellFont (true)));
            table.addCell (cell);

            cell = new PdfPCell (new Phrase (entry.getValue (), getCellFont (false)));
            table.addCell (cell);
        }
        table.completeRow ();
        return new PdfPCell (table);
    }

    private PdfPTable printContent (Map<String, byte[]> data) throws BadElementException, IOException, WTException, TranscoderException {

        PdfPTable table = new PdfPTable (2);
        for (Map.Entry<String, byte[]> entry : data.entrySet ()) {
            PdfPCell cell = createPDFCell (entry, 180f);
            table.addCell (cell);
        }
        table.completeRow ();
        return table;
    }

    public Font getCellFont (boolean bold) {

        String fontName = "Arial";
        float size = 8.0F;
        Color fontColor = Color.black;
        Font font = FontFactory.getFont (fontName, "Identity-H", size, 0, fontColor);

        if (bold) {
            font.setStyle (Font.BOLD);
        }

        return font;
    }

    private PdfPCell createBlankDataCell () {

        PdfPCell cell = new PdfPCell (new Phrase ("", getCellFont (true)));
        cell.setBorder (0);
        cell.setFixedHeight (20F);
        return cell;

    }

    private PdfPCell createPDFCell (Map.Entry<String, byte[]> data, float imageDimension) throws WTException, IOException, BadElementException, TranscoderException {

        PdfPTable cellTable = new PdfPTable (1);

        String fileName = data.getKey ();

        PdfPCell cell = new PdfPCell (new Phrase (fileName, getCellFont (true)));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellTable.addCell (cell);

        cellTable.addCell (createBlankDataCell ());

        Image img = null;
        logger.debug ("file name>>>>>" + fileName);
        if (fileName.toLowerCase ().endsWith (".svg")) {

        } else {
            img = Image.getInstance (data.getValue ());
        }
        logger.debug ("file name>>>>>" + fileName + "<<<img>>>" + img);
        if (img != null) {
            img.scalePercent (1000.0f);
            img.scaleToFit (imageDimension, imageDimension);
            PdfPCell iCell = new PdfPCell (img);
            iCell.setBorder (0);
            iCell.setHorizontalAlignment (Element.ALIGN_CENTER);
            iCell.setVerticalAlignment (Element.ALIGN_MIDDLE);
            iCell.setPadding (3.0f);
            cellTable.addCell (iCell);
        }

        cellTable.completeRow ();

        PdfPCell imgCell = new PdfPCell (cellTable);
        imgCell.setFixedHeight (0.0F);
        imgCell.setVerticalAlignment (4);
        return imgCell;
    }

}

