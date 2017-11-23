package com.gd.m.card;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gd.m.Flow;
import com.gd.m.FlowType;
import com.gd.m.GdUser;
import com.gd.m.card.ICard.CheckinState;
import com.gd.m.card.ICard.MemActCardType;
import com.gd.m.card.ICard.MemCardState;
import com.gd.m.card.ICard.MemCardType;
import com.jinhua.server.c.Codes;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class UserCards {
	private List<ICard> cards = new ArrayList<>();

	public void autoCheckin(String userState, Connection conn, GdUser user, GdUser emp, HttpServletRequest request, JSONObject obj) throws Exception {
		boolean isNeedKey = Utils.isTrue(request.getParameter("isNeedKey"));// 是否需要手牌
		String key = request.getParameter("key");// 是否传了手牌号
		boolean confirm = Utils.isTrue(request.getParameter("confirm"));// 会员状态不对的时候（请假等）确认修改状态到正常，并入场
		if (key != null && key.length() > 0) {
			obj.put("key", key);
		}
		ICard choosedCard = null;// 选择入场的会员卡
		if ("001".equals(userState)) {// 表示会员是激活状态
			choosedCard = chooseCheckinCard(emp.getGym());

			if (choosedCard.getState() == MemCardState.未激活) {
				if (!confirm) {
					obj.put("flag", "ACTIVE_BY_FIRST");// 返回前台会员状态为请假状态
					return;
				} else {
					激活(user.getId(), conn, emp.getCust_name(), emp.getGym());
					choosedCard.setState(MemCardState.激活);
				}
			}
		} else if ("005".equals(userState)) {// 表示会员在请假状态，提示用户自动销假
			if (!confirm) {
				obj.put("flag", "LEAVE_STATE");// 返回前台会员状态为请假状态
				return;
			} else {
				// 自动在这写销假逻辑
				销假(user.getId(), conn, emp.getCust_name(), emp.getGym());
				choosedCard = chooseCheckinCard(emp.getGym());
			}
		} else if ("002".equals(userState)) {// "未激活"
			// 查询是否为首次刷卡激活
			if (!confirm) {
				boolean ok = false;
				UserCards cards = UserCardUtils.getUserCardInfo(user.getId(), emp.getCust_name(), emp.getGym(), conn);
				for (int i = 0; i < cards.getCards().size(); i++) {
					ICard c = cards.getCards().get(i);
					if (c.getActiveType() == MemActCardType.首次刷卡) {
						ok = true;
						break;
					}
				}
				if (!ok) {
					// throw new Exception("当前会员卡未激活");
				}
				obj.put("flag", "ACTIVE_BY_FIRST");
				return;
			} else {
				// 自动在这写激活逻辑

				激活(user.getId(), conn, emp.getCust_name(), emp.getGym());
				choosedCard = chooseCheckinCard(emp.getGym());
			}
		} else {
			String stateStr = Codes.note("user_state", userState);
			throw new Exception("会员状态不对【" + stateStr + "】");
		}
		if (choosedCard != null) {
			if (choosedCard.getCheckinState() == CheckinState.场内) {
				choosedCard.checkOut(conn, obj);

				// 直接更新出场状态，
				Entity yp_checkin = new EntityImpl(conn);
				List<Object> params = new ArrayList<>();
				params.add(new Date());
				params.add(0);
				params.add("004");
				params.add(choosedCard.getCheckinId());
				yp_checkin.executeUpdate("update yp_checkin_" + choosedCard.getCheckinGym() + " set checkout_time=? ,checkout_price=?,state=? where id = ?", params.toArray());

				obj.put("flag", "CHECKOUT");
				obj.put("pic2", user.getXX("pic2"));

				Flow flow = new Flow();
				flow.setGym(emp.getGym());
				flow.setConn(conn);
				flow.setEmpId(emp.getId());
				flow.setEmpName(emp.getUserName());
				flow.setGdName("会员出场");
				flow.setMemId(user.getId());
				flow.setUserName(user.getUserName());
				flow.setContent(new JSONObject());
				flow.setCaAmt(0);
				flow.setOpId(emp.getId());
				flow.setOpName(emp.getUserName());
				flow.setState("001");// 状态 001 已付款 002 未付款
				flow.setDataTableName("yp_checkin_" + emp.getGym());
				flow.setDataId(choosedCard.getCheckinId());
				flow.setType(FlowType.出场);
				JSONObject content = new JSONObject();
				content.put("卡类型", choosedCard.getCardType() + "");
				content.put("出入场ID", choosedCard.getCheckinId());
				content.put("出入场会员卡名称", choosedCard.getType_name());
				content.put("出入场门店", choosedCard.getCheckinGym());
				content.put("出入场费用", choosedCard.getCheckin_fee());
				content.put("出场时间", Utils.parseData(new Date(), "yyyy.MM.dd HH:mm:ss"));

				if (choosedCard.getCardType() == MemCardType.天数卡) {// 天数卡
				} else if (choosedCard.getCardType() == MemCardType.储值卡) {// 储值卡
					flow.setType(FlowType.出场);
				} else if (choosedCard.getCardType() == MemCardType.次卡) {// 次数卡
				} else if (choosedCard.getCardType() == MemCardType.时间卡) {// 时间卡
					flow.setType(FlowType.出场);
					throw new Exception("选择了【时间卡出场】,暂缓实现");
				}
				flow.setContent(content);
				//flow.create();
				obj.put("isIn", "f");
			} else {
				String box_type = "003";// 普通入场换手牌
				long ca_price = 0l;
				long real_price = 0l;
				if (isNeedKey) {
					// 查询会员租柜信息
					// 如果有租柜信息 则把租柜钥匙牌显示出来，没有租柜 就叫收银员在前台填一个钥匙牌号
					if (user.isHasBox()) {
						key = user.getBoxNo();
						box_type = "002";// 租柜类型
						obj.put("key", key);
					} else {
						// 没有租柜，需要前台分配一个柜子
						if (key != null && key.length() > 0) {
							// 前台已经分配了柜子，
						} else {
							obj.put("flag", "NEED_KEY");// 前台输入手牌号
							return;
						}
					}
				} else {
					if (user.isHasBox()) {
						key = user.getBoxNo();
						box_type = "002";// 租柜类型
						obj.put("key", key);
					}
				}
				Entity en = new EntityImpl(conn);
				Flow flow = new Flow();
				Entity yp_checkin = new EntityImpl("YP_CHECKIN", conn);
				yp_checkin.setTablename("yp_checkin_" + emp.getGym());
				yp_checkin.setValue("cust_name", user.getCust_name());
				yp_checkin.setValue("gym", emp.getGym());
				yp_checkin.setValue("emp_id", emp.getId());
				yp_checkin.setValue("mem_id", user.getId());

				yp_checkin.setValue("mem_no", user.getMem_no());
				yp_checkin.setValue("phone", user.getXX("phone"));
				yp_checkin.setValue("user_name", user.getUserName());

				yp_checkin.setValue("checkin_time", new Date());
				yp_checkin.setValue("box_type", box_type);
				yp_checkin.setValue("box_no", key);
				// yp_checkin.setValue("checkout_time", );
				yp_checkin.setValue("checkout_price", 0);
				yp_checkin.setValue("ca_price", ca_price);
				yp_checkin.setValue("real_price", real_price);
				yp_checkin.setValue("flow_num", flow.getFlownum());
				yp_checkin.setValue("flow_num2", "");
				yp_checkin.setValue("flow_table_name", "yp_flow_" + emp.getGym());
				yp_checkin.setValue("checkin_price", 0);

				if (choosedCard.getCardType() == MemCardType.储值卡) {
					yp_checkin.setValue("checkin_price", choosedCard.getCheckin_fee());
					yp_checkin.setValue("ca_price", choosedCard.getCheckin_fee());
					yp_checkin.setValue("real_price", choosedCard.getCheckin_fee());
				}

				yp_checkin.setValue("checkin_type", ICard.getCardTypeCode(choosedCard.getCardType()));
				yp_checkin.setValue("type_code", choosedCard.getType_code());
				yp_checkin.setValue("state", "002");
				String checkin_id = yp_checkin.create();
				flow.setGym(emp.getGym());
				flow.setConn(conn);
				flow.setEmpId(emp.getId());
				flow.setEmpName(emp.getUserName());
				flow.setGdName("会员入场");
				flow.setMemId(user.getId());
				flow.setUserName(user.getUserName());
				JSONObject content = new JSONObject();
				content.put("卡类型", choosedCard.getCardType() + "");
				content.put("出入场ID", choosedCard.getCheckinId());
				content.put("出入场会员卡名称", choosedCard.getType_name());
				content.put("出入场门店", choosedCard.getCheckinGym());
				content.put("出入场费用", choosedCard.getCheckin_fee());
				content.put("入场时间", Utils.parseData(new Date(), "yyyy.MM.dd HH:mm:ss"));
				flow.setContent(content);
				flow.setCaAmt(0);
				flow.setCardAmt(real_price);
				flow.setOpId(emp.getId());
				flow.setOpName(emp.getUserName());
				flow.setState("001");// 状态 001 已付款 002 未付款
				flow.setDataTableName("yp_checkin_" + emp.getGym());
				flow.setDataId(checkin_id);

				if (choosedCard.getCardType() == MemCardType.天数卡) {// 天数卡
					flow.setType(FlowType.天数卡入场);
				} else if (choosedCard.getCardType() == MemCardType.储值卡) {// 储值卡
					flow.setType(FlowType.储值卡入场);
					flow.setCaAmt(choosedCard.getCheckin_fee());
					//储值卡入场，存入流水表支付类型为余额支付
					flow.setCardAmt(choosedCard.getCheckin_fee());
					// 更新用户余额
					if (user.getRemainAmt() < choosedCard.getCheckin_fee()) {
						throw new Exception("用户储值卡余额【" + Utils.toPrice(user.getRemainAmt()) + "】不足【" + Utils.toPrice(choosedCard.getCheckin_fee()) + "】");
					}
					en.executeUpdate("update yp_mem_" + user.getCust_name() + " set amt=? where id=?", new Object[] { user.getRemainAmt() - choosedCard.getCheckin_fee(), user.getId() });
					obj.put("checkinfee", Utils.toPrice(choosedCard.getCheckin_fee()));
				} else if (choosedCard.getCardType() == MemCardType.次卡) {// 次数卡
					// 抵扣一次次数
					flow.setType(FlowType.次卡入场);
					en.executeUpdate("update yp_type_user_" + emp.getGym() + " set times=times-1 where id =?", new String[] { choosedCard.getId() });
				} else if (choosedCard.getCardType() == MemCardType.时间卡) {// 时间卡
					// 记录开始时间
					flow.setType(FlowType.时间卡入场);
					throw new Exception("选择了【时间卡入场】,暂缓实现");
				}
				//flow.create();
				// 更新会员入场状态
				en.executeUpdate("update yp_mem_" + user.getCust_name() + " set checkin_state=? where id=?", new String[] { "002", user.getId() });
				obj.put("flag", "CHECKIN");
				obj.put("pic2", user.getXX("pic2"));
			}
		} else {
			throw new Exception("没有发现可以入场的卡");
		}

	}

	public void 销假(String mem_id, Connection conn, String cust_name, String gym) throws Exception {
		String sql = "select id,start_time from yp_leave where cust_name = ? and gym = ? and mem_id=? and state='001'";
		Entity entity = new EntityImpl(conn);
		// 更改用户状态
		try {
			entity.executeUpdate("update yp_mem_" + cust_name + "  set state='001' where id=?", new String[] { mem_id });
		} catch (Exception e) {
		}
		int size = entity.executeQuery(sql, new String[] { cust_name, gym, mem_id });
		if (size > 0) {
			// 当前请假是否有效"001"为有效，"002"为无效
			String start_time = entity.getStringValue("start_time");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date start = date.parse(start_time);
			entity.executeUpdate("update yp_leave set state='002',resume_time=? where mem_id =?", new Object[] { new Date(), mem_id });

			// 修改yp_mem_3333的状态由请假中转变为为已激活状态并延期有效期
			for (ICard ic : this.getCards()) {
				// 计算时间差
				Date now = new Date();
				int days = (int) ((now.getTime() - start.getTime()) / 1000);
				if (days < 0) {
					days = 0;
				}
				// 重新计算日期
				Calendar cld = Calendar.getInstance();
				if (ic.getDeadline() != null && !"".equals(ic.getDeadline())) {
					cld.setTime(ic.getDeadline());
				} else {
					cld.setTime(new Date());
				}
				cld.add(Calendar.SECOND, days);
				// 得到续费后的日期

				Entity yp_type_user = new EntityImpl("yp_type_user", conn);
				yp_type_user.setTablename("yp_type_user_" + gym);
				yp_type_user.setValue("deadline", cld.getTime());
				yp_type_user.setValue("state", "002");
				yp_type_user.setValue("id", ic.getId());
				yp_type_user.update();

				ic.setDeadline(cld.getTime());
				ic.setState(MemCardState.激活);

			}
		} else {
			// throw new Exception("没有您的请假记录");
		}
	}

	public void 激活(String mem_id, Connection conn, String cust_name, String gym) throws Exception {
		// 自动在这写激活逻辑
		Entity en = new EntityImpl("yp_mem", conn);
		en.setTablename("yp_mem_" + cust_name);
		en.setValue("state", "001");// 激活会员
		en.setValue("id", mem_id);
		en.update();

		for (ICard ic : this.getCards()) {
			if (ic.getState() == MemCardState.未激活) {
				String type_user_id = ic.getId();
				int days = ic.getDays();
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				Entity yp_type_user = new EntityImpl("yp_type_user", conn);
				yp_type_user.setTablename("yp_type_user_" + ic.getGym());
				yp_type_user.setValue("act_time", new Date());
				yp_type_user.setValue("deadline", cld.getTime());
				yp_type_user.setValue("state", "002");
				yp_type_user.setValue("id", type_user_id);
				yp_type_user.update();
				ic.setDeadline(cld.getTime());
				ic.setAct_time(new Date());
				ic.setState(MemCardState.激活);
			}
		}
	}

	public ICard chooseCheckinCard(String gym) throws Exception {
		if (this.cards.size() > 0) {
			// 0.检查是否有已经入场的卡
			// 1.检查是否有天数卡,如果有 就用天数卡入场
			// 2.检查是否有次卡,如果有就用次卡入场
			// 3.检查是否有储值卡,如果有就用储值卡入场
			/**
			 * 已经入场的卡
			 */
			for (ICard ic : this.getCards()) {
				if (ic.getCheckinState() == CheckinState.场内) {
					return ic;
				}
			}

			/**
			 * 天数卡
			 */
			List<ICard> dayCards = new ArrayList<>();
			for (ICard ic : this.getCards()) {
				if (ic.getCardType() == MemCardType.天数卡) {
					dayCards.add(ic);
				}
			}
			if (dayCards.size() > 0) {
				if (dayCards.size() == 1) {
					ICard card = dayCards.get(0);
					if (!card.isExpired()) {
						return card;
					}
				} else {
					// 选择一张合适的
					// 1.先用本店办理的会员卡,再用跨店卡
					for (ICard c : dayCards) {
						if (c.getGym().equals(gym) && !c.isExpired()) {
							return c;
						}
					}
					// 随机第一张卡,没有过期的卡
					for (ICard c : dayCards) {
						if (!c.isExpired()) {
							return c;
						}
					}

				}
			}

			/**
			 * 次卡
			 */
			List<ICard> timesCards = new ArrayList<>();
			for (ICard ic : this.getCards()) {
				if (ic.getCardType() == MemCardType.次卡) {
					timesCards.add(ic);
				}
			}
			if (timesCards.size() > 0) {
				if (timesCards.size() == 1) {
					ICard card = timesCards.get(0);
					if (!card.isExpired() && card.getTimes() > 0) {
						return card;
					}
				} else {
					// 选择一张合适的
					// 1.先用本店办理的会员卡,再用跨店卡
					for (ICard c : timesCards) {
						if (c.getGym().equals(gym) && !c.isExpired() && c.getTimes() > 0) {
							return c;
						}
					}
					// 随机第一张卡,没有过期的卡
					for (ICard c : timesCards) {
						if (!c.isExpired() && c.getTimes() > 0) {
							return c;
						}
					}
				}
			}

			/**
			 * 储值卡
			 */
			for (ICard ic : this.getCards()) {
				if (ic.getCardType() == MemCardType.储值卡) {
					return ic;
				}
			}
		}
		throw new Exception("没有找到可用的会员卡信息");

	}

	public List<ICard> getCards() {
		return cards;
	}

	public void setCards(List<ICard> cards) {
		this.cards = cards;
	}

	public boolean containsTypeName(String typename) {
		for (ICard ic : this.getCards()) {
			if (ic.getType_name().equals(typename)) {
				return true;
			}
		}
		return false;
	}

}
