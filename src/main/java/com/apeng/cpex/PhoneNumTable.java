package com.apeng.cpex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PhoneNumTable implements Iterable<String> {
    private final List<String> table = new ArrayList<>();
    public PhoneNumTable(String... phoneNums) {
        Collections.addAll(table, phoneNums);
    }

    @Override
    public Iterator<String> iterator() {
        return table.iterator();
    }
}
