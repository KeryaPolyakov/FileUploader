package com.kirillpolyakov.fileuploaderspringbootjavafx.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SimpleUser extends User{

    public SimpleUser(@NonNull String userName, @NonNull String password, @NonNull String fio) {
        super(userName, password, fio);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
