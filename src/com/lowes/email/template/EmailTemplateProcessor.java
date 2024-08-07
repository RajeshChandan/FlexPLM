package com.lowes.email.template;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lowes.email.model.EmailTemplates;
import wt.util.WTProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class EmailTemplateProcessor {

    private static final String TEMPLATE_RESOURCE_LOCATION = LCSProperties.get("com.lowes.email.template.templateResourceLocation", "/codebase/rfa/lowes/templates/");
    public static final String DASHED_LINE = "<hr style=\" border-top: 2px dashed\"/>";
    public static final String DISPLAY = "$Display";
    private String templateName = "default.html";
    private String templateContent;


    public EmailTemplateProcessor() {
        this.templateName = EmailTemplates.DEFAULT.getTemplate();
    }

    public EmailTemplateProcessor(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateContent() {

        if (this.templateContent.contains(DISPLAY)) {
            this.templateContent = this.templateContent.replace(DISPLAY, "none");
        }
        return templateContent;
    }

    public void buildTemplate(String message) throws IOException {
        WTProperties wtProperties = WTProperties.getLocalProperties();
        String wtHome = wtProperties.getProperty("wt.home");
        String templateFile = wtHome + TEMPLATE_RESOURCE_LOCATION + templateName;
        Path templatePath = Path.of(templateFile);
        this.templateContent = Files.readString(templatePath);
        this.templateContent = this.templateContent.replace("$message", message);
    }

    public void buildSalutation(String message) {
        this.templateContent = this.templateContent.replace("$Salutation", message);
    }

    public void buildAttachment(String message) {
        if (FormatHelper.hasContent(message)) {
            this.templateContent = this.templateContent.replace(DISPLAY, "block");
            this.templateContent = this.templateContent.replace("$Attachment", message);
        }
    }

    public static String buildLink(String link, String message) {

        return "<strong> <a href=\"" + link + "\">" + message + "</a></strong>";

    }

    public static String buildAsBold(String message) {

        return "<strong>" + message + "</strong>";

    }

    public static String addLineBrake(int n) {
        String lineBreak = "";
        for (int i = 0; i < n; i++) {
            lineBreak = lineBreak.concat("<br>");
        }
        return lineBreak;
    }

    public static String drawTable(Map<String, String> table) {
        String defaultRow = "<tr> <td><strong>$text</strong></td> <td><a class=\"linkText\" href=\"$link\" style=\"text-decoration: none; color: blue;\">Click to Download</a><br></td> </tr>";
        String tableRows = "";
        for (Map.Entry<String, String> entry : table.entrySet()) {
            String fileName = entry.getKey();
            String link = entry.getValue();
            String temp = defaultRow.replace("$text", fileName);
            temp = temp.replace("$link", link);

            tableRows = tableRows.concat(temp);
        }
        return tableRows;
    }
}
