package com.lowes.email.model;

public enum EmailTemplates {
    DEFAULT("default.html"), DEFAULT_ATTACHMENT("default_attachment.html");
    private String template;

    private EmailTemplates(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }


}
