package com.yicj.adapter;

public class PrintBanner2 implements Print{
    private Banner banner ;
    public PrintBanner2(Banner banner){
        this.banner = banner ;
    }

    @Override
    public void printWeak() {
        this.banner.showWithParen();
    }

    @Override
    public void printStrong() {
        this.banner.showWithAster();
    }
}
