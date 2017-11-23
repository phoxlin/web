package com.gd.m.flow;

import java.sql.Connection;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;

public class CoachBuyClass {
	public void xx(String did,String dt,Connection conn) throws Exception {
		Entity en = new EntityImpl(dt, conn);
		en.setValue("id", did);
		en.search();

		String mem_id = en.getStringValue("mem_id");
		String lesson_id = en.getStringValue("lesson_id");

		Entity clazz = new EntityImpl(dt, conn);
		clazz.setValue("mem_id", mem_id);
		clazz.setValue("lesson_id", lesson_id);
		clazz.setValue("state", "001");
		int size = clazz.search();
		if (size == 0) {
			en.setValue("state", "001");
			en.update();
		} else {
			String cid = clazz.getStringValue("id");
			int nums = en.getIntegerValue("nums");
			int lesson_num = en.getIntegerValue("lesson_num");
			int class_num = clazz.getIntegerValue("nums");
			clazz = new EntityImpl(dt, conn);
			clazz.setValue("id", cid);
			clazz.setValue("nums", class_num + nums);
			clazz.setValue("lesson_num", lesson_num + nums);
			clazz.update();
			en.delete();
		}
	}
}
