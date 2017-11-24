package com.core.server.db;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface Entity {
    void addOtherCondition(String var1);

    void setValue(String var1, Object var2) throws Exception;

    void setValue(String var1, Object var2, int var3) throws Exception;

    void setNullValue(String var1) throws Exception;

    Object getValue(String var1) throws Exception;

    String getStringValue(String var1) throws Exception;

    List<String> getStringListValue(String var1) throws Exception;

    List<Date> getDateListValue(String var1) throws Exception;

    List<Double> getDoubleListValue(String var1) throws Exception;

    List<Float> getFloatListValue(String var1) throws Exception;

    List<String> getFlormatStringListValue(String var1) throws Exception;

    List<Long> getLongListValue(String var1) throws Exception;

    List<Integer> getIntegerListValue(String var1) throws Exception;

    BigDecimal getBigDecimalValue(String var1) throws Exception;

    BigDecimal getBigDecimalValue(String var1, int var2) throws Exception;

    Integer getIntegerValue(String var1) throws Exception;

    Long getLongValue(String var1) throws Exception;

    Long getLongValue(String var1, int var2) throws Exception;

    Float getFloatValue(String var1) throws Exception;

    boolean getBooleanValue(String var1) throws Exception;

    boolean getBooleanValue(String var1, int var2) throws Exception;

    Double getDoubleValue(String var1) throws Exception;

    Date getDateValue(String var1) throws Exception;

    String getFormatStringValue(String var1, String var2) throws Exception;

    String getStringValue(String var1, int var2) throws Exception;

    Integer getIntegerValue(String var1, int var2) throws Exception;

    Float getFloatValue(String var1, int var2) throws Exception;

    Double getDoubleValue(String var1, int var2) throws Exception;

    Date getDateValue(String var1, int var2) throws Exception;

    String getFormatStringValue(String var1, String var2, int var3) throws Exception;

    String save(boolean var1) throws Exception;

    String save() throws Exception;

    String create() throws Exception;

    void delete() throws Exception;

    void update() throws Exception;

    void updateAll() throws Exception;

    int searchOne() throws Exception;

    int search() throws Exception;

    int search(int var1, int var2) throws Exception;

    int executeQuery(String var1, int var2, int var3) throws Exception;

    int executeQueryWithMaxResult(String var1, int var2, int var3) throws Exception;

    int executeQuery(String var1) throws Exception;

    int executeQuery(String var1, Object[] var2) throws Exception;

    int executeQuery(String var1, Object[] var2, int var3, int var4) throws Exception;

    int executeQueryWithMaxResult(String var1, Object[] var2, int var3, int var4) throws Exception;

    int executeUpdate(String var1) throws Exception;

    int executeUpdate(String var1, Object[] var2) throws Exception;

    int getResultCount() throws Exception;

    int getMaxResultCount() throws Exception;

    List<Map<String, Object>> getValues();

    List<Column> getCols();

    String toJsonString() throws Exception;

    JSONObject toJson() throws Exception;

    void DDL(boolean var1) throws Exception;

    void setTablename(String var1);

    void setTableComment(String var1);

    void copyTableFrom(String var1, String var2) throws Exception;

    void setSlience(boolean var1);

    JSONObject toJson(String var1) throws Exception;
}
