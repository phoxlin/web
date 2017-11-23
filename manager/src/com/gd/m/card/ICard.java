package com.gd.m.card;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gd.m.GdUser;
import com.jinhua.server.log.Logger;
import com.jinhua.server.tools.Utils;

public abstract class ICard implements Comparable<ICard> {
	private String id;// yp_type_user表id
	private String cardId;// yp_type表id
	private int times;// 次卡剩余次数
	private long mins;// 时间卡剩余分钟数
	private long amt;// 储值卡余额
	private int days;// 会员卡天数
	private long checkin_fee;// 储值卡一次入场扣费

	private long price;// 原价
	private long realPrice;// 实际销售价

	private String type_code;
	private long type_fee;// 卡费用
	private MemActCardType activeType;// 开卡类型
	private MemCardType cardType;// 卡种类型
	private Date activeDate;// 指定开卡日期
	private Date buyDate;// 买卡日期

	private String type_name;
	private Date deadline;
	private Date act_time;
	private String sales_id;
	private String sales_name;
	private String sales_type;// 签单意向
	private MemCardState state;// 卡状态
	private boolean is_give;// 发卡;
	private boolean is_print;// 打印合同
	private String cardno;
	private String state_code;//状态数字形式

	private int consume_rank;// 消费排序
	private String remark;
	private String gym;// 购卡门店
	private String cust_name;
	public Set<String> gyms = new HashSet<>();//可见门店
	private String generalStoreLevel;// 通店属性
	private String gymName;// 购卡门店名称
	private CheckinState checkinState;
	private String checkinId;// 入场id;
	private Date checkinDate;// 入场时间
	private Date checkoutDate;// 出厂时间
	private String checkinGym;// 出入场健身房
	private Map<String, String> gymInfos = new HashMap<>();// 门店名称
	private String memId;
	private String memName;
	
	private String createCardGym;//创建卡种门店
	private String createCardGymName;//创建卡种门店名称
	
	public String getCreateCardGymName() {
		return createCardGymName;
	}

	public void setCreateCardGymName(String createCardGymName) {
		this.createCardGymName = createCardGymName;
	}

	public String getCreateCardGym() {
		return createCardGym;
	}

	public void setCreateCardGym(String createCardGym) {
		this.createCardGym = createCardGym;
	}

	private static Map<MemCardType, Integer> types = new HashMap<>();

	static {
		types.put(MemCardType.天数卡, 1);
		types.put(MemCardType.次卡, 2);
		types.put(MemCardType.时间卡, 3);
		types.put(MemCardType.单次入场卷, 4);
		types.put(MemCardType.储值卡, 5);
		types.put(MemCardType.私教卡, 6);
		types.put(MemCardType.收费团课, 7);
	}

	@Override
	public String toString() {
		return this.getCardType() + "(" + this.getType_name() + ")";
	}

	@Override
	public int compareTo(ICard o) {
		if (this.getCardType() != o.getCardType()) {
			int a = types.get(this.getCardType());
			int b = types.get(o.getCardType());
			if (a > b) {
				return 1;
			} else if (a < b) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return this.getType_name().compareTo(o.getType_name());
		}
	}

	/**
	 * 是否已经过期
	 * 
	 * @return
	 */
	public boolean isExpired() {
		Date deadline = this.getDeadline();
		if (deadline != null) {
			if (deadline.before(new Date())) {
				if (Utils.parseData(deadline, "yyyyMMdd").equals(Utils.parseData(new Date(), "yyyyMMdd"))) {
					return false;
				}

				return true;
			}
		} else {
			if (this.state == MemCardState.未激活) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> m = new HashMap<>();
		m.put("type_name", getType_name());
		m.put("type_user_id", this.getId());
		m.put("card_typeStr", getCardType());
		m.put("type_fee", getType_fee());
		m.put("id", getId());
		m.put("state", getState());
		m.put("deadline", getDateStr(deadline));
		m.put("sales_id", getSales_id());
		m.put("type_code", getType_code());
		String card_type = getCardTypeCode(getCardType());
		m.put("card_type", card_type);
		m.put("checkinFee", Utils.toPrice(getCheckin_fee()));
		m.put("times", this.getTimes());
		m.put("mins", getMins());
		Date now = new Date();
		try {
			now = Utils.parse2Date(Utils.parseData(new Date(), "yyyy-MM-dd") + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			if (deadline == null && act_time == null) {
				m.put("remainDays", this.getDays());
			} else {
				long deadlinetimes = (deadline.getTime() - now.getTime());
				long days = deadlinetimes / 1000 / 60 / 60 / 24;
				m.put("remainDays", days);
			}
		} catch (Exception e) {
			m.put("remainDays", 0);
		}
		if (MemCardType.天数卡 == this.getCardType()) {// 天数卡
		} else if (MemCardType.次卡 == this.getCardType()) {// 次数卡
		} else if (MemCardType.储值卡 == this.getCardType()) {// 储值卡
		} else if (MemCardType.时间卡 == this.getCardType()) {// 时间卡
		} else if (MemCardType.单次入场卷 == this.getCardType()) {// 单次入场卷
		}
		m.put("act_time", getDateStr(getAct_time(), getDeadline(), this.getDays()));
		m.put("gyms", this.getGyms());
		m.put("gymNames", this.getGymNames());
		m.put("gym", this.getGym());
		m.put("gymName", Utils.getMapStringValue(gymInfos, this.getGym()));
		m.put("general_store_level", this.getGyms().size() > 1);
		m.put("remark", this.getRemark());
		return m;
	}

	public Object getDateStr(Date act_time, Date deadline, int days) {
		if (!Utils.isNull(act_time)) {
			return Utils.parseData(act_time, "yyyy.MM.dd");
		} else {
			if (deadline != null) {
				if ("baodi".equals(cust_name)) {
					return "16.10.1旧" + Utils.parseData(buyDate, "MM.dd") + "导";
				} else {
					return (Utils.parseData(Utils.dateAddDay(deadline, -days), "yyyy.MM.dd")) + "(略)";
				}
			} else {
				if(activeType == MemActCardType.指定日期){
					return Utils.parseData(activeDate, "yyyy.MM.dd") + "(指定)";
				} else {
					return "";
				}
			}
		}
	}

	private List<String> getGymNames() {
		List<String> list = new ArrayList<>();
		for (String g : this.getGyms()) {
			list.add(Utils.getMapStringValue(gymInfos, g));
		}
		return list;
	}

	private String getDateStr(Date d) {
		if (d != null) {
			return Utils.parseData(d, "yyyy.MM.dd");
		} else {

		}
		return "";
	}

	/**
	 * 自动出入场
	 */
	public abstract void autoCheckin(Connection conn, GdUser emp, HttpServletRequest request, JSONObject obj) throws Exception;

	/**
	 * 
	 * @author terry
	 */

	public enum CheckinState {
		未入场, 场内, 自动离场, 出场, 出场收费, 拒绝入场
	}

	public enum MemCardState {
		未激活, 激活, 退费, 请假, 挂失, 补卡, 转卡, 退卡
	}

	public enum MemActCardType {
		立即激活, 首次刷卡, 指定日期, 统一开卡
	}

	public enum MemCardType {
		天数卡, 储值卡, 次卡, 时间卡, 单次入场卷, 私教卡, 收费团课
	}

	public static CheckinState getCheckinState(String checkinStateCode) throws Exception {
		if ("001".equals(checkinStateCode)) {
			return CheckinState.未入场;
		} else if ("002".equals(checkinStateCode)) {
			return CheckinState.场内;
		} else if ("003".equals(checkinStateCode)) {
			return CheckinState.自动离场;
		} else if ("004".equals(checkinStateCode)) {
			return CheckinState.出场;
		} else if ("004-2".equals(checkinStateCode)) {
			return CheckinState.出场收费;
		} else if ("005".equals(checkinStateCode)) {
			return CheckinState.拒绝入场;
		} else {
			throw new Exception("不认识的入场状态[" + checkinStateCode + "]");
		}
	}

	public static String getCheckinStateCode(CheckinState type) {
		if (type == CheckinState.未入场) {
			return "001";
		} else if (type == CheckinState.场内) {
			return "002";
		} else if (type == CheckinState.自动离场) {
			return "003";
		} else if (type == CheckinState.出场) {
			return "004";
		} else if (type == CheckinState.出场收费) {
			return "004-2";
		} else {
			return "005";
		}
	}

	public static MemCardState getCardState(String cardStateCode) throws Exception {
		if ("001".equals(cardStateCode)) {
			return MemCardState.未激活;
		} else if ("002".equals(cardStateCode)) {
			return MemCardState.激活;
		} else if ("003".equals(cardStateCode)) {
			return MemCardState.退费;
		} else if ("004".equals(cardStateCode)) {
			return MemCardState.请假;
		} else if ("005".equals(cardStateCode)) {
			return MemCardState.挂失;
		} else if ("006".equals(cardStateCode)) {
			return MemCardState.补卡;
		} else if ("007".equals(cardStateCode)) {
			return MemCardState.转卡;
		} else if ("008".equals(cardStateCode)) {
			return MemCardState.退卡;
		} else {
			throw new Exception("不认识的卡状态[" + cardStateCode + "]");
		}
	}

	public static String getCardStateCode(MemCardState type) {
		if (type == MemCardState.未激活) {
			return "001";
		} else if (type == MemCardState.激活) {
			return "002";
		} else if (type == MemCardState.退费) {
			return "003";
		} else if (type == MemCardState.请假) {
			return "004";
		} else if (type == MemCardState.挂失) {
			return "005";
		} else if (type == MemCardState.补卡) {
			return "006";
		} else if (type == MemCardState.转卡) {
			return "007";
		} else {
			return "008";
		}
	}

	public static MemActCardType getActCardType(String actCardTypeCode) throws Exception {
		if ("001".equals(actCardTypeCode)) {
			return MemActCardType.立即激活;
		} else if ("002".equals(actCardTypeCode)) {
			return MemActCardType.首次刷卡;
		} else if ("003".equals(actCardTypeCode)) {
			return MemActCardType.指定日期;
		} else if ("004".equals(actCardTypeCode)) {
			return MemActCardType.统一开卡;
		} else {
			// throw new Exception("不认识的开卡类型[" + actCardTypeCode + "]");
			Logger.warn("不认识的开卡类型[" + actCardTypeCode + "]");
			return MemActCardType.首次刷卡;
		}
	}

	public static String getActCardTypeCode(MemActCardType type) {
		if (type == MemActCardType.立即激活) {
			return "001";
		} else if (type == MemActCardType.首次刷卡) {
			return "002";
		} else if (type == MemActCardType.指定日期) {
			return "003";
		} else {
			return "004";
		}
	}

	public static MemCardType getCardType(String cardTypeCode) throws Exception {
		if ("001".equals(cardTypeCode)) {
			return MemCardType.天数卡;
		} else if ("002".equals(cardTypeCode)) {
			return MemCardType.储值卡;
		} else if ("003".equals(cardTypeCode)) {
			return MemCardType.次卡;
		} else if ("004".equals(cardTypeCode)) {
			return MemCardType.时间卡;
		} else if ("005".equals(cardTypeCode)) {
			return MemCardType.单次入场卷;
		} else if ("006".equals(cardTypeCode)) {
			return MemCardType.私教卡;
		} else if ("007".equals(cardTypeCode)) {
			return MemCardType.收费团课;
		} else {
			throw new Exception("不认识的卡种类型[" + cardTypeCode + "]");
		}
	}

	public static String getCardTypeCode(MemCardType type) {
		if (type == MemCardType.天数卡) {
			return "001";
		} else if (type == MemCardType.储值卡) {
			return "002";
		} else if (type == MemCardType.次卡) {
			return "003";
		} else if (type == MemCardType.时间卡) {
			return "004";
		} else if (type == MemCardType.单次入场卷) {
			return "005";
		} else if (type == MemCardType.私教卡) {
			return "006";
		} else {
			return "007";
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}

	public long getType_fee() {
		return type_fee;
	}

	public void setType_fee(long type_fee) {
		this.type_fee = type_fee;
	}

	public MemActCardType getActiveType() {
		return activeType;
	}

	public void setActiveType(MemActCardType activeType) {
		this.activeType = activeType;
	}

	public MemCardType getCardType() {
		return cardType;
	}

	public void setCardType(MemCardType cardType) {
		this.cardType = cardType;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}

	public long getCheckin_fee() {
		return checkin_fee;
	}

	public void setCheckin_fee(long checkin_fee) {
		this.checkin_fee = checkin_fee;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getAct_time() {
		return act_time;
	}

	public void setAct_time(Date act_time) {
		this.act_time = act_time;
	}

	public String getSales_id() {
		if (sales_id != null && sales_id.length() == 24) {
			return sales_id;
		}
		return "";
	}

	public void setSales_id(String sales_id) {
		this.sales_id = sales_id;
	}

	public String getSales_name() {
		return sales_name;
	}

	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}

	public String getSales_type() {
		return sales_type;
	}

	public void setSales_type(String sales_type) {
		this.sales_type = sales_type;
	}

	public MemCardState getState() {
		return state;
	}

	public void setState(MemCardState state) {
		this.state = state;
	}

	public Set<String> getGyms() {
		Set<String> temps = new HashSet<>();
		for (String g : gyms) {
			if (!Utils.isNull(g)) {
				temps.add(g);
			}
		}
		if (temps.size() <= 0 && !Utils.isNull(this.gym)) {
			temps.add(this.gym);
		}
		this.gyms = temps;
		return temps;
	}

	public void setGyms(Set<String> gyms) {
		this.gyms = gyms;
	}

	public String getGeneralStoreLevel() {
		return generalStoreLevel;
	}

	public void setGeneralStoreLevel(String generalStoreLevel) {
		this.generalStoreLevel = generalStoreLevel;
	}

	public boolean isIs_give() {
		return is_give;
	}

	public void setIs_give(boolean is_give) {
		this.is_give = is_give;
	}

	public boolean isIs_print() {
		return is_print;
	}

	public void setIs_print(boolean is_print) {
		this.is_print = is_print;
	}

	public String getGym() {
		return gym;
	}

	public void setGym(String gym) {
		this.gym = gym;
	}

	public long getAmt() {
		return amt;
	}

	public void setAmt(long amt) {
		this.amt = amt;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public long getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(long realPrice) {
		this.realPrice = realPrice;
	}

	public long getMins() {
		return mins;
	}

	public void setMins(long mins) {
		this.mins = mins;
	}

	public String getGymName() {
		return gymName;
	}

	public void setGymName(String gymName) {
		this.gymName = gymName;
	}

	public Map<String, String> getGymInfos() {
		return gymInfos;
	}

	public void setGymInfos(Map<String, String> gymInfos) {
		this.gymInfos = gymInfos;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public CheckinState getCheckinState() {
		return checkinState;
	}

	public void setCheckinState(CheckinState checkinState) {
		this.checkinState = checkinState;
	}

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public Date getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}

	public abstract void checkOut(Connection conn, JSONObject obj) throws Exception;

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Date getCheckinDate() {
		return checkinDate;
	}

	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public String getCheckinGym() {
		return checkinGym;
	}

	public void setCheckinGym(String checkinGym) {
		this.checkinGym = checkinGym;
	}

	public String getCheckinId() {
		return checkinId;
	}

	public void setCheckinId(String checkinId) {
		this.checkinId = checkinId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public int getConsume_rank() {
		return consume_rank;
	}

	public void setConsume_rank(int consume_rank) {
		this.consume_rank = consume_rank;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getState_code() {
		return state_code;
	}

	public void setState_code(String state_code) {
		this.state_code = state_code;
	}

	public static Map<MemCardType, Integer> getTypes() {
		return types;
	}

	public static void setTypes(Map<MemCardType, Integer> types) {
		ICard.types = types;
	}

}
