package com.core.server.msg.email;

import java.io.Serializable;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class Receiver implements Serializable {
    private static final long serialVersionUID = 1L;
    private ReceiverType receiverType;
    private String id;
    private String name;
    private String addr;

    public Receiver() {
        this.receiverType = ReceiverType.TO;
    }

    public Receiver(String name, String addr, ReceiverType receiverType) {
        this.receiverType = ReceiverType.TO;
        this.name = name;
        this.addr = addr;
        this.receiverType = receiverType;
    }

    public String getId() {
        return this.id;
    }

    public ReceiverType getReceiverType() {
        return this.receiverType;
    }

    public void setReceiverType(ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if(this.name == null) {
            this.name = "";
        }

        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
