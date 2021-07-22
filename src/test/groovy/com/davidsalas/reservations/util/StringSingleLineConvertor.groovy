package com.davidsalas.reservations.util

import java.util.stream.Collectors

class StringSingleLineConvertor {

    static singleLine(String content) {
        content.trim().lines().map({ line -> line.trim() }).collect(Collectors.joining())
    }
}
