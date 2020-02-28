package com.example.isc.Core;

public class MyOption {
    private int optionIcon;
    private String option;
    public MyOption(int optionIcon, String option){
        this.option = option;
        this.optionIcon = optionIcon;
    }

    public int getOptionIcon() {
        return optionIcon;
    }

    public void setOptionIcon(int optionIcon) {
        this.optionIcon = optionIcon;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
