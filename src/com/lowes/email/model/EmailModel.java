package com.lowes.email.model;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmailModel {

    private String SENDER_EMAIL;
    private List<String> RECIPIENT_EMAIL;
    private List<String> CC_RECIPIENT_EMAIL;
    private List<String> BCC_RECIPIENT_EMAIL;
    private String EMAIL_SUBJECT;
    private String EMAIL_CONTENT;
    private List<File> EMAIL_ATTACHMENTS;

    public EmailModel() {
        this.EMAIL_ATTACHMENTS = new ArrayList<File>();
    }

    public String getSENDER_EMAIL() {
        return SENDER_EMAIL;
    }

    public void setSENDER_EMAIL(String SENDER_EMAIL) {
        this.SENDER_EMAIL = SENDER_EMAIL;
    }

    public List<String> getRECIPIENT_EMAIL() {
        if (Objects.isNull(RECIPIENT_EMAIL)) {
            this.RECIPIENT_EMAIL = new ArrayList<>();
        }
        return RECIPIENT_EMAIL;
    }

    public void setRECIPIENT_EMAIL(List<String> RECIPIENT_EMAIL) {
        this.RECIPIENT_EMAIL = RECIPIENT_EMAIL;
    }

    public List<String> getCC_RECIPIENT_EMAIL() {
        if (Objects.isNull(CC_RECIPIENT_EMAIL)) {
            this.CC_RECIPIENT_EMAIL = new ArrayList<>();
        }
        return CC_RECIPIENT_EMAIL;
    }

    public void setCC_RECIPIENT_EMAIL(List<String> CC_RECIPIENT_EMAIL) {
        this.CC_RECIPIENT_EMAIL = CC_RECIPIENT_EMAIL;
    }

    public List<String> getBCC_RECIPIENT_EMAIL() {
        if (Objects.isNull(BCC_RECIPIENT_EMAIL)) {
            this.BCC_RECIPIENT_EMAIL = new ArrayList<>();
        }
        return BCC_RECIPIENT_EMAIL;
    }

    public void setBCC_RECIPIENT_EMAIL(List<String> BCC_RECIPIENT_EMAIL) {
        this.BCC_RECIPIENT_EMAIL = BCC_RECIPIENT_EMAIL;
    }

    public String getEMAIL_SUBJECT() {
        return EMAIL_SUBJECT;
    }

    public void setEMAIL_SUBJECT(String EMAIL_SUBJECT) {
        this.EMAIL_SUBJECT = EMAIL_SUBJECT;
    }

    public String getEMAIL_CONTENT() {
        return EMAIL_CONTENT;
    }

    public void setEMAIL_CONTENT(String EMAIL_CONTENT) {
        this.EMAIL_CONTENT = EMAIL_CONTENT;
    }

    public List<File> getEMAIL_ATTACHMENTS() {
        return EMAIL_ATTACHMENTS;
    }

    public void setEMAIL_ATTACHMENTS(List<File> EMAIL_ATTACHMENTS) {
        this.EMAIL_ATTACHMENTS = EMAIL_ATTACHMENTS;
    }

    @Override
    public String toString() {
        return "EmailModel{" +
                "SENDER_EMAIL='" + SENDER_EMAIL + '\'' +
                ", RECIPIENT_EMAIL=" + RECIPIENT_EMAIL +
                ", CC_RECIPIENT_EMAIL=" + CC_RECIPIENT_EMAIL +
                ", BCC_RECIPIENT_EMAIL=" + BCC_RECIPIENT_EMAIL +
                ", EMAIL_SUBJECT='" + EMAIL_SUBJECT + '\'' +
                ", EMAIL_CONTENT='" + EMAIL_CONTENT + '\'' +
                ", EMAIL_ATTACHMENTS=" + EMAIL_ATTACHMENTS +
                '}';
    }


}
