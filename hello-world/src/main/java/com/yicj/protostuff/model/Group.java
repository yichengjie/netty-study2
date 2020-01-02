package com.yicj.protostuff.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Group {
    private String id;
    private String name;
    private List<User> users = new ArrayList<>();
}