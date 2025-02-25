package com.yudream.yudreamaddons.common.integration.mmce.adapter.tc6;

import java.util.ArrayList;
import java.util.List;

public class AspectCache {
    public String aspect;
    public List<String> items;

    public AspectCache() {
        this.items = new ArrayList<>();
    }

    public AspectCache(String aspect) {
        this();
        this.aspect = aspect;
    }
}
