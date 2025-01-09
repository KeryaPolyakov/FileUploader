package com.kirillpolyakov.fileuploaderspringbootjavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFile {

    private String name;

    private Type type;

    @Override
    public String toString() {
        return name;
    }
}
