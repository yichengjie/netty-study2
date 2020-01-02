package com.yicj.protostuff.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Group {
    private String id;
    private String name;
    private User user;
}