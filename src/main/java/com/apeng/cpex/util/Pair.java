package com.apeng.cpex.util;

public record Pair<A, B>(A first, B second) {

    public String toString() {
        return "(" + first + ", " + second + ")";
    }


}
