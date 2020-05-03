package com.ldt.inspired;


import java.util.ArrayList;
import java.util.List;

/*
Chứa danh sách các fragment controller
<br> ContainerController có giao diện cố định và chiếm toàn bộ kích cỡ của controller sở hữu nó
 */
public abstract class ContainerController<T extends NavController> extends NavController<T> {
    private final List<T> list = new ArrayList<>();
    public List<T> getChildControllers() {
        return list;
    }
}
