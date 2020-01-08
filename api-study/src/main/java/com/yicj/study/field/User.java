package com.yicj.study.field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {
    private long id ;//注意这里不能使用Long,否则无法设置成功
    private String name ;
}
