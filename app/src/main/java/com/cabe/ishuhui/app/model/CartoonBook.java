package com.cabe.ishuhui.app.model;

import java.util.List;

/**
 * Cartoon Book Info
 * Created by cabe on 16/5/11.
 */
public class CartoonBook {
    public String name;
    public String tips;
    public String introduce;
    public String cover;
    public List<CartoonInfo> list;

    public String toString() {
        return name + "_" + introduce;
    }
}
