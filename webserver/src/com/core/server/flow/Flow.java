package com.core.server.flow;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Flow {
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger((new SecureRandom()).nextInt());
    private static final int LOW_ORDER_THREE_BYTES = 16777215;
    private String flownum;
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private Date op_time;
    private String id;

    public Flow() {
        this(NEXT_COUNTER.getAndIncrement());
    }

    public static void main(String[] args) {
        for(int i = 0; i < 10000000; ++i) {
            System.out.println((new Flow()).getTrimedFlownum());
        }

    }

    private Flow(int andIncrement) {
        this.op_time = new Date();
        String x = (new SimpleDateFormat("HHmmss")).format(this.op_time);
        Calendar op = Calendar.getInstance();
        op.setTime(this.op_time);
        int year = op.get(1) - 2010;
        int month = op.get(2) + 1;
        int day = op.get(5);
        int counter = andIncrement & 16777215;
        this.flownum = Utils.int2hex(year) + Utils.int2hex(month) + Utils.int2hex(day) + x + this.toHexString(counter);
    }

    public byte[] toByteArray(int counter) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        this.putToByteBuffer(buffer, counter);
        return buffer.array();
    }

    public String toHexString(int counter) {
        char[] chars = new char[4];
        int i = 0;
        byte[] var7;
        int var6 = (var7 = this.toByteArray(counter)).length;

        for(int var5 = 0; var5 < var6; ++var5) {
            byte b = var7[var5];
            chars[i++] = HEX_CHARS[b >> 4 & 15];
            chars[i++] = HEX_CHARS[b & 15];
        }

        return new String(chars);
    }

    private void putToByteBuffer(ByteBuffer buffer, int counter) {
        buffer.put(int1(counter));
        buffer.put(int0(counter));
    }

    private static byte int0(int x) {
        return (byte)x;
    }

    private static byte int1(int x) {
        return (byte)(x >> 8);
    }

    public String getFlownum() {
        return this.flownum;
    }

    public String getTrimedFlownum() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.flownum.substring(0, 3));
        sb.append("-");
        sb.append(this.flownum.substring(3, 9));
        sb.append("-");
        sb.append(this.flownum.substring(9));
        return sb.toString();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
