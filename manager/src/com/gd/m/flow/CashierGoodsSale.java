package com.gd.m.flow;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class CashierGoodsSale {
	public void xx(JSONObject sell, Connection conn, String op_name, String op_id, String mem_id, String gym,String flowNum) throws Exception {

		String store_id = sell.getString("repertory_id");
		//拿到应付价格
		float shouldTotal = Float.parseFloat(sell.getString("total")) * 100;
		//拿到实际支付价格
		float realtotalprice = Float.parseFloat(sell.getString("realtotalprice"));
		//计算出打几折
		int zhe = (int) ((realtotalprice/shouldTotal) * 100);
		float zhe2 = zhe/100F;
		
		// 减少商品的数量,并在出入库添加数据
		JSONObject goods = sell.getJSONObject("goods");
		// 查询以前的商品数量
		Entity old = new EntityImpl(conn);
		String where = "";
		for (String goods_id : goods.keySet()) {
			where += "'" + goods_id + "',";
		}
		where = where.substring(0, where.length() - 1);
		String sql = "select cust_name,id, good_num ,good_no from yp_goods where id in (" + where + ")";
		old.executeQuery(sql);

		List<Map<String, Object>> oldList = old.getValues();

		// Map<String, String> old_nums = old.getValues().stream()
		// .collect(Collectors.toMap(map -> map.get("id").toString(), map ->
		// map.get("num").toString()));

		String goods_msg = "";
		float total = 0;
		int i =0;
		float item = 0;
		Entity good = new EntityImpl("yp_goods", conn);
		for (String goods_id : goods.keySet()) {
			for (Map<String, Object> m : oldList) {
				String goodId = m.get("id").toString();
				String good_no = m.get("good_no").toString();
				String cust_name = m.get("cust_name").toString();
				if (goodId.equals(goods_id)) {
					JSONObject g = goods.getJSONObject(goods_id);
					if (g != null && g.length() > 0) {
						int old_num = Integer.parseInt(m.get("good_num").toString());
						int sell_num = g.getInt("num");
						float price = g.getFloat("price");
						
						i++;
						int relprice = (int) (sell_num*price*zhe2*100);
						if (oldList.size()==i) {
							float relPlaceF = (realtotalprice - item) *100;
							relprice = (int) (relPlaceF/100);
						}
						item+=relprice;
						float relprice2 = relprice/100F;
						String goodsName = g.getString("name");
						if (sell_num > old_num) {
							throw new Exception("有商品数量不足,无法进行销售!");
						}

						good.setValue("id", goods_id);
						good.setValue("good_num", old_num - sell_num);
						good.update();
						
						good.executeQuery("select bprice from yp_goods where id = ?",new Object[]{goods_id});
						Long bprice = good.getLongValue("bprice");
						Entity rec = new EntityImpl("yp_store_rec", conn);
						rec.setValue("store_id", store_id);
						rec.setValue("good_id", goods_id);
						rec.setValue("good_num", 0 - sell_num);
						rec.setValue("op_id", op_id);
						rec.setValue("cust_name", cust_name);
						rec.setValue("gym", gym);
						rec.setValue("op_name", op_name);
						rec.setValue("op_time", new Date());
						rec.setValue("store_num", old_num - sell_num);
						rec.setValue("remark", "收银台销售出库");
						rec.create();
						rec = new EntityImpl("yp_goods_buy_record", conn);
						rec.setTablename("yp_goods_buy_record_" + gym);
						rec.setValue("mem_id", mem_id);
						rec.setValue("goods_no", good_no);
						rec.setValue("good_id", goods_id);
						rec.setValue("goods_num", sell_num);
						rec.setValue("goods_name", goodsName);
						rec.setValue("goods_price", price);
						rec.setValue("goods_bprice", Utils.toPrice(bprice));
						rec.setValue("total_price", relprice2);
						rec.setValue("store_id", store_id);
						rec.setValue("gym", gym);
						rec.setValue("cust_name", "");//AppUtils.getCustName(gym, conn));
						rec.setValue("op_id", op_id);
						rec.setValue("flow_num", flowNum);
						rec.setValue("op_name", op_name);
						rec.setValue("op_time", new Date());
						rec.create();
						
						goods_msg += goodsName+"*"+sell_num+",";
						total += relprice2;
					}
				}
			}
		}
		
		if(mem_id != null && mem_id.length() > 0 && !"-1".equals(mem_id)){
			// 发消息
			JSONObject body = new JSONObject();
			body.put("goods_name", goods_msg);
			body.put("pay_money", total);
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd HH:mm"));
			//MsgToMemAction.SendMsg("mem_buy_goods", mem_id, body, conn, AppUtils.getCustName(gym, conn), gym);
		}
	}

}
