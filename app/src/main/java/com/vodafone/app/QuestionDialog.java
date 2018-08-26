package com.vodafone.app;

import java.util.ArrayList;

class QuestionDialog {

    private int q_id=0;
    private int type=0;
    private String question="";
    private ArrayList<DialogOptions> options;

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<DialogOptions> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<DialogOptions> options) {
        this.options = options;
    }
}
