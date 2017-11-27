package com.core.server.msg.email;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class Address {
    private String personal;
    private String email;

    public Address(String persional, String email) {
        this.personal = persional;
        this.email = email;
        if(persional == null) {
            this.personal = "";
        }

        if(this.email == null) {
            this.email = "";
        }

    }

    public String toString() {
        String compositeto = this.personal + "<" + this.email + ">";
        if(this.personal.length() <= 0) {
            compositeto = this.email;
        }

        return compositeto;
    }

    public String getPersonal() {
        return this.personal;
    }

    public String getEmail() {
        return this.email;
    }
}
