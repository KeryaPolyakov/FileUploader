package com.kirillpolyakov.fileuploaderspringbootjavafx.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;


/**
 * У пользователя поля: логин (не может повторяться), пароль, ФИО
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "admin"),
        @JsonSubTypes.Type(value = SimpleUser.class, name = "simpleUser")
})
public class User {

    private long id;

    @NonNull
    private String userName;

    @NonNull
    private String password;

    @NonNull
    private String fio;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
