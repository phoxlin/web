package com.gd.m.card;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gd.m.Cust;
import com.gd.m.UserType;
import com.gd.m.card.ICard.CheckinState;
import com.gd.m.card.ICard.MemCardType;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.log.Logger;
import com.jinhua.server.tools.RedisUtils;
import com.jinhua.server.tools.Utils;

public class UserCardUtils {

	/**
	 * 查询会员在某个门店的会员卡信息
	 * 
	 * @param id
	 * @param cust_name
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static UserCards getUserCardInfo(String id, String cust_name, String curGym, Connection conn)
			throws Exception {
		List<ICard> list = new ArrayList<>();
		List<ICard> li = null;
		try {
			li = getUserCards(cust_name, id, conn);
		} catch (Exception e) {
			throw new Exception("当前用户没有会员卡");
		}
		for (int i = 0; i < li.size(); i++) {
			ICard ic = li.get(i);
			if (ic.getGyms().contains(curGym) || ic.getCardType() == MemCardType.储值卡) {
				list.add(ic);
			}
		}
		Collections.sort(list);
		if (list.size() <= 0) {
			throw new Exception("当前用户没有本店会员卡");
		}
		UserCards cards = new UserCards();
		cards.setCards(list);
		return cards;
	}

	public static List<ICard> getUserCards(String cust_name, String memId, Connection conn) throws Exception {
		List<ICard> li = new ArrayList<>();
		long checkinFee = 0;
		String moneyCardType = "";
		String moneyCardName = null;
		Entity en = new EntityImpl(conn);
		// 查询所有卡种及所属门店和通店信息
		Map<String, Set<String>> codeGym = new HashMap<>();
		Map<String, String> gymInfo = new HashMap<>();
		// 1.卡种所属门店查询
		int size = en.executeQuery("select a.gym,a.type_code from yp_type_gym a where a.cust_name=?",
				new String[] { cust_name });
		for (int i = 0; i < size; i++) {
			String gym2 = en.getStringValue("gym", i);
			String code = en.getStringValue("type_code", i);
			Set<String> list = codeGym.get(code);
			if (list == null) {
				list = new HashSet<>();
				codeGym.put(code, list);
			}
			list.add(gym2);
		}
		// 2.查询所有通店代码对应的门店信息
		Set<String> gyms = new HashSet<>();// 保存所有门店代码
		Map<String, Set<String>> levelGym = new HashMap<>();
		size = en.executeQuery("select a.gym,a.gym_level ,a.gym_name from yp_gym a where a.cust_name=?",
				new String[] { cust_name });
		for (int i = 0; i < size; i++) {
			String gym = en.getStringValue("gym", i);
			String gymName = en.getStringValue("gym_name", i);
			gyms.add(gym);
			gymInfo.put(gym, gymName);
			String gym_level = en.getStringValue("gym_level", i);
			if (gym_level != null && gym_level.length() > 0) {
				String[] levels = gym_level.split(",");
				for (String l : levels) {
					if (l != null && l.length() > 0) {
						Set<String> list = levelGym.get(l);
						if (list == null) {
							list = new HashSet<>();
							levelGym.put(l, list);
						}
						list.add(gym);
					}
				}
			}
		}

		// 3.查询会员所有门店的卡
		StringBuilder querySql = new StringBuilder();
		List<String> tempIds = new ArrayList<>();
		if (gyms.size() > 0) {
			int i = 0;
			for (String gym : gyms) {
				tempIds.add(memId);
				if (i != 0) {
					querySql.append(" UNION ALL ");
				}
				querySql.append(
						"select a.id,a.mem_id,a.gym,a.buy_id,a.is_give,a.is_print,a.type_code,a.mins, a.times,a.buy_time,a.deadline,a.act_time,a.sales_id,a.sales_name,a.sales_type,a.state,a.remark from yp_type_user_"
								+ gym + " a where a.mem_id=?");
				i++;
			}
		}

		int mainsize = en.executeQuery(querySql.toString(), tempIds.toArray());
		Set<String> userTypeCode = en.getValues().stream().map(map -> map.get("type_code").toString())
				.collect(Collectors.toSet());
		Map<String, String> buyIds = new HashMap<>();
		for (int i = 0; i < mainsize; i++) {
			String buy_id = en.getStringValue("buy_id", i);
			String gym = en.getStringValue("gym", i);
			String mem_id = en.getStringValue("mem_id", i);
			String type_code = en.getStringValue("type_code", i);
			buyIds.put(buy_id + "__" + mem_id + "__" + type_code, gym);
		}
		if (userTypeCode.size() > 0) {
			Entity yp_type = new EntityImpl(conn);
			List<String> tempParams = new ArrayList<>();
			tempParams.add(cust_name);
			tempParams.addAll(userTypeCode);
			// 查询会员的卡信息
			yp_type.executeQuery(
					"select a.id,a.gym,a.CARD_TYPE,a.type_code,a.type_name,a.GIFT_ID,a.DAYS,a.AMT,a.TIMES,a.MINS,a.CHECKIN_FEE,a.general_store_level,a.TYPE_FEE,a.consume_rank,a.remark from yp_type a where a.cust_name=? and type_code in("
							+ Utils.getListString("?", userTypeCode.size()) + ")",
					tempParams.toArray());

			// 查询会员卡购买记录信息

			Entity buycard = new EntityImpl(conn);

			StringBuilder sb = new StringBuilder();
			if (buyIds.size() > 0) {
				int i = 0;
				List<String> tempId2s = new ArrayList<>();
				for (Entry<String, String> er : buyIds.entrySet()) {
					if (i != 0) {
						sb.append(" UNION ALL ");
					}
					String idstr = er.getKey();
					String[] ids = idstr.split("__");
					if (ids[0].length() > 5) {
						tempId2s.add(ids[0]);
						sb.append(
								"select a.active_time,a.active_type,b.real_amt real_price,a.id,a.price,a.type_code from yp_buy_card_record_"
										+ er.getValue() + " a,yp_flow_"+er.getValue()+" b where a.id = b.data_id and  a.id =?");
					} else {
						sb.append(
								"select a.active_time,a.active_type,b.real_amt real_price,a.id,a.price,a.type_code from yp_buy_card_record_"
										+ er.getValue() + " a ,yp_flow_"+er.getValue()+" b where a.id = b.data_id and a.mem_id =? and a.type_code = ?");
						tempId2s.add(ids[1]);
						if(ids.length > 3){
							tempId2s.add(ids[2]);
						} else {
							tempId2s.add("");
						}
					}
					i++;
				}
				buycard.executeQuery(sb.toString(), tempId2s.toArray());
			}

			// 检查是否已经入场
			Entity checkin = UserCardUtils.getCheckinInfos(conn, gyms, memId, 10);

			boolean hasAmtCard = false;// 是否有储值卡
			for (int i = 0; i < mainsize; i++) {
				String cardId = en.getStringValue("id", i);
				String gym = en.getStringValue("gym", i);
				boolean is_give = en.getBooleanValue("is_give", i);
				boolean is_print = en.getBooleanValue("is_print", i);
				String type_code = en.getStringValue("type_code", i);
				String buy_id = en.getStringValue("buy_id", i);
				long mins = en.getLongValue("mins", i);
				int times = en.getIntegerValue("times", i);
				Date deadline = en.getDateValue("deadline", i);
				Date buy_time = en.getDateValue("buy_time", i);
				Date act_time = en.getDateValue("act_time", i);
				// 计算会员卡截止时间距离当前时间还有几天
				try {
					mins = (deadline.getTime() - System.currentTimeMillis()) / 1000 / 60 / 60 / 24;
				} catch (Exception e) {
				}
				String sales_id = en.getStringValue("sales_id", i);
				String sales_name = en.getStringValue("sales_name", i);
				String sales_type = en.getStringValue("sales_type", i);
				String state = en.getStringValue("state", i);
				String remark = en.getStringValue("remark", i);
				Map<String, Object> moreInfos = getCardDetail(type_code, yp_type);
				Map<String, Object> checkinInfos = getCheckinedDetail(type_code, checkin);

				Map<String, Object> actInfos = getCardActDetail(buy_id, type_code, buycard);
				if (moreInfos != null) {
					String type_name = Utils.getMapStringValue(moreInfos, "type_name");
					String cardTypeCode = Utils.getMapStringValue(moreInfos, "card_type");
					String createCardGym = Utils.getMapStringValue(moreInfos, "gym");
					MemCardType cardType = ICard.getCardType(cardTypeCode);
					ICard card = null;
					if (cardType == MemCardType.天数卡) {
						int days = Utils.getMapIntegerValue(moreInfos, "days");
						card = new DayCard();
						card.setDays(days);
					} else if (cardType == MemCardType.储值卡) {
						// 储值卡的值直接加到了会员的余额里面,这里不做处理,最后查询一下 如果会员有余额,就直接加一个储值卡
						long checkinFee2 = Utils.getMapLongValue(moreInfos, "checkin_fee");
						if (checkinFee == 0) {
							checkinFee = checkinFee2;
							moneyCardName = type_name;
							moneyCardType = type_code;
						} else {
							if (checkinFee2 < checkinFee && checkinFee2 > 0) {
								checkinFee = checkinFee2;
								moneyCardName = type_name;
								moneyCardType = type_code;
							}
						}
						hasAmtCard = true;
						continue;
					} else if (cardType == MemCardType.时间卡) {
						card = new TimeCard();
					} else if (cardType == MemCardType.次卡) {
						card = new TimesCard();
					} else if (cardType == MemCardType.单次入场卷) {
						card = new OnceCard();
					} else {
						throw new Exception("发现未知卡类型[" + cardType + "]");
					}
					card.setRemark(remark);
					card.setConsume_rank(Utils.getMapIntegerValue(moreInfos, "consume_rank"));
					// card.setRemark(Utils.getMapStringValue(moreInfos,
					// "remark"));
					card.setCheckinState(CheckinState.未入场);
					if (checkinInfos != null && checkinInfos.size() > 0) {
						// 有入场信息
						try {
							card.setCheckinDate(Utils.getMapDateValue(checkinInfos, "checkin_time"));
							card.setCheckinGym(Utils.getMapStringValue(checkinInfos, "gym"));
							card.setCheckinId(Utils.getMapStringValue(checkinInfos, "id"));
							card.setCheckinState(CheckinState.场内);
						} catch (Exception e) {
							Logger.error(e);
						}
					}
					card.setBuyDate(buy_time);
					card.setMemId(memId);
					card.setCust_name(cust_name);
					card.setId(cardId);
					card.setCardType(cardType);
					card.setCreateCardGymName(gymInfo.get(createCardGym));
					// a.active_time,a.active_type,a.real_price,a.id
					Date active_time = null;
					try {
						active_time = Utils.getMapDateValue(actInfos, "active_time");
					} catch (Exception e) {
					}
					String active_type = Utils.getMapStringValue(actInfos, "active_type");
					long real_price = Utils.getMapLongValue(actInfos, "real_price");
					long price = Utils.getMapLongValue(actInfos, "price");

					card.setAct_time(act_time);
					card.setActiveDate(active_time);// 指定开卡日期
					card.setActiveType(ICard.getActCardType(active_type));
					card.setGym(gym);
					card.setGymName(gymInfo.get(gym));
					card.setIs_give(is_give);
					card.setIs_print(is_print);
					card.setType_code(type_code);
					card.setDeadline(deadline);
					card.setSales_id(sales_id);
					card.setSales_name(sales_name);
					card.setSales_type(sales_type);
					card.setState(ICard.getCardState(state));
					card.setState_code(state);;
					card.setTimes(times);
					card.setMins(mins);
					card.setRealPrice(real_price);
					card.setPrice(price);

					String general_store_level = Utils.getMapStringValue(moreInfos, "general_store_level");
					long TYPE_FEE = Utils.getMapLongValue(moreInfos, "type_fee");
					card.setPrice(TYPE_FEE);
					card.setGeneralStoreLevel(general_store_level);
					card.setType_name(type_name);

					try {
						if (general_store_level != null && general_store_level.length() > 0) {
							Set<String> temps = levelGym.get(general_store_level);
							if (temps != null) {
								card.getGyms().addAll(temps);
							}
						}
					} catch (Exception e) {
					}
					try {
						Set<String> temps = codeGym.get(type_code);
						if (temps != null) {
							card.gyms.addAll(temps);
						}
					} catch (Exception e) {
					}
					card.setCheckin_fee(checkinFee);
					card.setGymInfos(gymInfo);
					li.add(card);
				}
			}

			size = en.executeQuery("select a.amt from yp_mem_" + cust_name + " a where a.id=?", new String[] { memId },
					1, 1);
			if (size > 0) {
				// 如果有储值卡或者余额大于0,则显示储值卡
				if (en.getLongValue("amt") > 0 || hasAmtCard) {
					ICard ic = new MoneyCard();
					if (li.size() == 0) {
						// 其他啥子卡都求没得,只有储值卡
						ic.setCheckinState(CheckinState.未入场);
						Map<String, Object> checkinInfos = getCheckinedDetail(moneyCardType, checkin);
						if (checkinInfos != null && checkinInfos.size() > 0) {
							// 有入场信息
							for (int i = 0; i < checkinInfos.size(); i++) {
							}
							try {
								ic.setCheckinDate(Utils.getMapDateValue(checkinInfos, "checkin_time"));
								ic.setCheckinGym(Utils.getMapStringValue(checkinInfos, "gym"));
								ic.setCheckinId(Utils.getMapStringValue(checkinInfos, "id"));
								ic.setCheckinState(CheckinState.场内);
							} catch (Exception e) {
								Logger.error(e);
							}
						}
					}
					ic.setAmt(en.getLongValue("amt"));
					ic.setCardType(MemCardType.储值卡);
					ic.setCheckin_fee(checkinFee);
					ic.setType_name(moneyCardName);
					if (moneyCardType != null && moneyCardType.length() > 0) {
						ic.setType_code(moneyCardType);
					} else {
						ic.setType_code("N/A");
					}
					li.add(ic);
				}
			}
		}
		return li;
	}

	private static Map<String, Object> getCardActDetail(String buy_id, String type_code, Entity en) throws Exception {
		if (en.getResultCount() > 0) {
			for (int i = 0; i < en.getResultCount(); i++) {
				String id = en.getStringValue("id", i);
				String type_code2 = en.getStringValue("type_code", i);
				if (buy_id.length() <= 0) {
					if (type_code2.equals(type_code)) {
						return en.getValues().get(i);
					}
				} else {
					if (buy_id.equals(id)) {
						return en.getValues().get(i);
					}
				}
			}
		}
		return null;
	}

	public static Map<String, Object> getCardDetail(String type_code, Entity en) throws Exception {
		if (en.getResultCount() > 0) {
			for (int i = 0; i < en.getResultCount(); i++) {
				String type_code1 = en.getStringValue("type_code", i);
				if (type_code.equals(type_code1)) {
					return en.getValues().get(i);
				}
			}
		}
		return null;
	}

	public static Map<String, Object> getCheckinedDetail(String type_code, Entity en) throws Exception {
		if (en.getResultCount() > 0) {
			for (int i = 0; i < en.getResultCount(); i++) {
				String type_code1 = en.getStringValue("type_code", i);
				if (type_code.equals(type_code1)) {
					String state = en.getStringValue("STATE", i);
					if ("002".equals(state)) {
						return en.getValues().get(i);
					}
				}
			}
		}
		return null;
	}

	public static Cust getCustByLoginName(String name, Connection conn) throws Exception {
		/**
		 * 查询用户cust_name 1.判断用户名是否为电话号码，如果是则不是 管理员，如果不是则是管理员
		 */

		boolean isPhoneNo = isMobileNO(name);
		String cust_name = null;
		Entity en = new EntityImpl(conn);
		if (!isPhoneNo) {
			/**
			 * 管理员
			 */
			int size = en.executeQueryWithOutMaxResult("select a.id,a.cust_name from yp_gym a where a.admin_login=?",
					new String[] { name }, 1, 1);
			if (size > 0) {
				cust_name = en.getStringValue("cust_name");
				Cust cust = new Cust(cust_name, name, UserType.管理员, conn);
				return cust;
			} else {
				throw new Exception("用户名错误");
			}
		} else {
			/**
			 * 会员或者员工
			 */
			/**
			 * 1.先判断是否为员工
			 */

			int size = en.executeQueryWithOutMaxResult("select cust_name from yp_emp a where a.phone=?",
					new String[] { name }, 1, 1);
			if (size > 0) {
				/**
				 * 是员工
				 */
				cust_name = en.getStringValue("cust_name");
				Cust cust = new Cust(cust_name, name, UserType.员工, conn);
				return cust;
			}

			// 判断是不是会员
			{
				/**
				 * 是会员
				 */
				// 通过电话号码的，找到存放会员cust_name的xml，规则为:手机号前三位后，每两位参生一个文件夹，共4层文件夹，
				// 文件夹更目录为：WEB-INF/configures/database/xml
				try {
					cust_name = UserCardUtils.getCust_name(name);
					Cust cust = new Cust(cust_name, name, UserType.会员, conn);
					return cust;
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	public static String getCust_name(String phone) throws Exception {
		Object cust_name = RedisUtils.getHParam("yepao_phone_cust_name", phone);
		if (cust_name != null && !cust_name.equals("null") && cust_name.toString().length() > 0) {
			return cust_name.toString();
		} else {
			throw new Exception("没有找到'" + phone + "'对应的健身房代码");
		}
	}

	public static void addPhoneSetting(String phone, String cust_name) throws Exception {
		if (isMobileNO(phone)) {
			RedisUtils.setHParam("yepao_phone_cust_name", phone, cust_name);
		} else {
			throw new Exception("手机号码位数不对");
		}
	}

	public static Entity getCheckinInfos(Connection conn, Collection<String> gyms, String memId, int limited)
			throws Exception {
		// 检查是否已经入场
		Entity checkin = new EntityImpl(conn);
		StringBuilder sb = new StringBuilder();
		List<String> tempId2s = new ArrayList<>();
		int ii = 0;
		for (String gym : gyms) {
			if (ii != 0) {
				sb.append(" UNION ALL ");
			}
			sb.append("select a.checkout_time,a.checkin_type, a.emp_id, a.id,a.checkin_time,a.type_code,'" + gym
					+ "' gym ,a.STATE from yp_checkin_" + gym + " a where a.MEM_ID=?");
			tempId2s.add(memId);
			ii++;
		}
		checkin.executeQuery("select * from (" + sb.toString() + ") aa order by aa.checkin_time desc",
				tempId2s.toArray(), 1, limited);
		return checkin;
	}

	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		if (mobiles != null && mobiles.length() == 11) {
			try {
				// 13********* ,15********,18*********
				Pattern p = Pattern.compile("^(1[0-9])\\d{9}$");
				Matcher m = p.matcher(mobiles);
				flag = m.matches();
			} catch (Exception e) {
				flag = false;
			}
		}
		return flag;
	}
}
