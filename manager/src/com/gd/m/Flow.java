package com.gd.m;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import com.gd.m.flow.AccountRecharge;
import com.gd.m.flow.AppRenewCard;
import com.gd.m.flow.BuyClass;
import com.gd.m.flow.BuyGclass;
import com.gd.m.flow.CardUpdate;
import com.gd.m.flow.CashierGoodsSale;
import com.gd.m.flow.CashierLockerRental;
import com.gd.m.flow.CashierRenewCard;
import com.gd.m.flow.CashierTurnCard;
import com.gd.m.flow.CashierTurnClass;
import com.gd.m.flow.CoachBuyClass;
import com.gd.m.flow.IndividualTicket;
import com.gd.m.flow.PaidLeave;
import com.gd.m.flow.SalesDeal;
import com.gd.m.flow.buyCard;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.log.Logger;
import com.jinhua.server.tools.Utils;

public class Flow {
	private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());
	private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;
	private String flownum;// 流水号
	private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private Date op_time = new Date();
	private Date pay_time = null;
	private boolean employee = false;
	private Connection conn;
	private String id;
	private String gym;
	private String pid = "-1";
	private String empId;// 销售员ID
	private String empName;// 销售员姓名
	private FlowType type;// 流水类型
	private String gdName;// 商品名称
	private String actId = "-1";// 活动ID
	private String memId;// 会员ID
	private String userName;// 会员名称
	private String phone = "0000";// 会员电话
	private String cardNumber = "0000";// 会员卡号
	private JSONObject content = new JSONObject();// 消费详情
	private long caAmt;// 应收金额
	private long realAmt;// 实收金额
	private boolean isChange = false;// 手动修改金额
	private String chgRemark;// 修改原因
	private String chgOpId;// 修改人ID
	private long remainAmt;// 用户账户剩余金额
	private long giftAmt;// 消费赠金
	private long remainGiftAmt;// 剩余赠金
	private long cashAmt;// 现金
	private long wxAmt;// 微信
	private long aliAmt;// 支付宝
	private long cardAmt;// 单笔下卡金额
	private long cardCashAmt;// 刷卡金额
	private long giftCardAmt;// 代金券
	private String giftCardNo;// 卷号
	private String opId;// 操作人id
	private String opName;// 操作人名字
	private String dataTableName = null;// 业务表名
	private String dataId = "-1";// 业务数据id
	private String state;// 流水状态 001 已付款 002 未付款004分单
	private String T;
	private String carId;

	private String counterFeeType;// 用于判断押金缴费方式
	private Long cardAmtBak;// 余额支付临时保存

	public Long getCardAmtBak() {
		return cardAmtBak;
	}

	public void setCardAmtBak(Long cardAmtBak) {
		this.cardAmtBak = cardAmtBak;
	}

	public String getCounterFeeType() {
		return counterFeeType;
	}

	public void setCounterFeeType(String counterFeeType) {
		this.counterFeeType = counterFeeType;
	}

	public String getT() {
		return T;
	}

	public void setT(String t) {
		T = t;
	}

	public void submitPay(String cust_name, Connection conn) throws Exception {
		// 判断 应收金额 和 实收金额 是否匹配
		if (this.caAmt != this.realAmt) {
			this.isChange = true;
			// 如果不匹配，判断是否有中间人进行确认，
			if (this.chgOpId != null && this.chgOpId.length() == 11) {
				Entity en = new EntityImpl(conn);
				int size = en.executeQueryWithOutMaxResult("select * from yp_emp a where a.phone=?", new String[] { chgOpId }, 1, 1);
				if (size <= 0) {
					throw new Exception("修改金额的人员信息不对");
				}
			}
		} else {
			this.isChange = false;
		}

		// 检查用户的付款信息和实收金额是否匹配
		if (this.realAmt != this.cardAmt + this.cashAmt + this.aliAmt + this.wxAmt + this.cardCashAmt) {
			throw new Exception("应收[" + Utils.toPrice(this.realAmt) + "],实收[" + Utils.toPrice(this.cardAmt + this.cashAmt + this.aliAmt + this.wxAmt + this.cardCashAmt) + "]");
		}
		// 检查是否有支付宝支付
		/*
		 * if (this.aliAmt > 0) { GdUser user; if (this.memId != "-1") { user =
		 * new GdUser(cust_name, this.memId, false, conn); } else { user = new
		 * GdUser(cust_name, this.opId, false, conn); } if (user != null) {
		 * JSONObject ali = user.getAliPayParam(); // 检查是否支付成功 TradePrecreate tp
		 * = new TradePrecreate(); tp.queryOrder(this.getFlownum(),
		 * ali.getString("partner"), ali.getString("private_key")); } }
		 */

		this.pay_time = new Date();
		this.state = "001";

		// 更新用户余额
		if (this.cardAmt > 0) {
			GdUser dg = null;
			try {
				dg = new GdUser(cust_name, memId, false, conn);
			} catch (Exception e1) {
				throw new Exception("NO_SUCH_USER");
			}
			/*
			 * Long yj_cardAmt = 0L; if (this.type.equals(FlowType.收银台储物柜出租)) {
			 * String type = this.getContent().getString("type"); if
			 * ("002".equals(type)) {// 新租柜,扣钱加上押金 Float rentboxFee = 0f; String
			 * _rentboxFee = ParamValUtils.getValues(cust_name, this.gym,
			 * "counterFee", "rentboxFee", conn); if (_rentboxFee != null &&
			 * _rentboxFee.length() > 0) { rentboxFee =
			 * Float.parseFloat(_rentboxFee); } if (cashAmt >= rentboxFee * 100
			 * || aliAmt >= rentboxFee * 100 || wxAmt >= rentboxFee * 100 ||
			 * cardCashAmt >= rentboxFee * 100) { } else if (cardAmt >=
			 * rentboxFee * 100) {// 押金是余额支付的 yj_cardAmt = new Float(rentboxFee
			 * * 100).longValue(); } } }
			 */
			if (memId != null && memId.length() == 24) {
				// 正式会员
				if (dg.getRemainAmt() >= (this.cardAmt)) {
					// 更新用户余额
					Entity en = new EntityImpl(conn);
					en.executeUpdate("update yp_mem_" + dg.getCust_name() + " set amt = ? where id = ?", new Object[] { dg.getRemainAmt() - this.cardAmt, memId });
					this.remainAmt = dg.getRemainAmt() - this.cardAmt;// 扣除余额支付的押金
				} else {
					throw new Exception("当前会员的余额不足");
				}
			} else if ("-1".equals(memId)) {
				// 散客
				// 不用更新
			}
		}
		this.update();

		// 更新业务数据状态
		if (this.type == FlowType.散客购票) {
			IndividualTicket it = new IndividualTicket();
			JSONObject json = getContent();
			it.xx(conn, json, this.getDataTableName(), this.getFlownum(), gym, cust_name, phone, caAmt);
		} else if (this.type == FlowType.收银台续卡) {
			String ca = this.caAmt + "";
			String ra = this.realAmt + "";
			CashierRenewCard crc = new CashierRenewCard();
			String recordId = crc.xx(this.content, conn, cust_name, gym, this.memId, ca, ra, flownum);
			Entity en = new EntityImpl("yp_flow", conn);
			
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { recordId, flownum });
			
		} else if (this.type == FlowType.续卡) {
			String ca = this.caAmt + "";
			String ra = this.realAmt + "";
			AppRenewCard arc = new AppRenewCard();
			String recordId = arc.xx(this.dataId, conn, cust_name, gym, this.memId, ca, ra, flownum);
			Entity en = new EntityImpl("yp_flow", conn);
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { recordId, flownum });

		} else if (this.type == FlowType.升级) {
			CardUpdate cu = new CardUpdate();
			String recordId = cu.xx(this.content, conn, cust_name, this.remainAmt, this.memId, flownum, gym, this.memId, this.realAmt + "");
			Entity en = new EntityImpl("yp_flow", conn);
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { recordId, flownum });
		} else if (this.type == FlowType.购买会员卡) {
			String ca = this.caAmt + "";
			String ra = this.realAmt + "";
			buyCard bc = new buyCard();
			String recordId = bc.xx(this.content, conn, cust_name, this.memId, this.gym, flownum, this.opId, this.opName, this.dataId, ca, ra);
			Entity en = new EntityImpl("yp_flow", conn);
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { recordId, flownum });

		} else if (this.type == FlowType.购买私教课课程) {// 私教课和付费团课在一起
			BuyClass bc = new BuyClass();
			String ra = this.realAmt + "";
			bc.xx(this.dataId, this.content, conn, cust_name, this.memId, this.empId, gym, flownum, ra);
		} else if (this.type == FlowType.收银台储物柜出租) {
			CashierLockerRental clr = new CashierLockerRental();
			String rentId = clr.xx(this, conn, this.memId, cust_name, gym, this.flownum, realAmt, opId, opName);
			Entity en = new EntityImpl("yp_flow", conn);
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { rentId, flownum });

		} else if (this.type == FlowType.收银台补卡) {
			JSONObject json = this.getContent();
			String mem_no = json.getString("mem_no");
			Entity entity = new EntityImpl("yp_mem", conn);
			entity.setTablename("yp_mem_" + cust_name);
			entity.setValue("id", this.memId);
			entity.setValue("state", "001");
			entity.setValue("mem_no", mem_no);
			entity.update();

			GdUser u = new GdUser(cust_name, this.getMemId(), false, conn);
			// 发消息
			JSONObject body = new JSONObject();
			body.put("user_name", u.getUserName());
			body.put("mem_no", mem_no);
			body.put("fee", Utils.toPrice(this.realAmt));
		} else if (this.type == FlowType.收银台转卡手续费) {
			CashierTurnCard ctc = new CashierTurnCard();
			ctc.xx(conn, this.getContent(), cust_name, gym);
		} else if (this.type == FlowType.收银台商品销售) {
			CashierGoodsSale cgs = new CashierGoodsSale();
			JSONObject obj = this.getContent();

			obj.put("realtotalprice", this.realAmt);
			cgs.xx(obj, conn, this.opName, this.opId, this.memId, gym, flownum);
		} else if (FlowType.购买团课课程 == this.type) {// 购买付费团课
			BuyGclass bg = new BuyGclass();
			bg.xx(this.getContent(), conn, this.getDataId(), this.getMemId(), this.getEmpId(), cust_name, gym, this.flownum, this.realAmt);
		} else if (FlowType.账户充值 == this.type) {// 账户充值
			AccountRecharge ar = new AccountRecharge();
			ar.xx(conn, (int) this.getCaAmt(), cust_name, gym, this.getMemId());
		} else if (FlowType.付费请假 == this.type) {// 付费请假
			PaidLeave pl = new PaidLeave();
			pl.xx(this.content, conn, cust_name, gym, this.memId, this.realAmt);
		} else if (this.type == FlowType.教练端购课) {
			CoachBuyClass cbc = new CoachBuyClass();
			cbc.xx(this.getDataId(), this.getDataTableName(), conn);
		} else if (this.type == FlowType.会籍成交) {
			SalesDeal sd = new SalesDeal();
			sd.xx(this.content, conn, cust_name, gym, this.getDataId(), this.memId, this.empId, this.empName, this.caAmt);
		} else if (this.type == FlowType.收银台转课手续费) {
			CashierTurnClass ctc = new CashierTurnClass();
			ctc.xx(this.content, conn, cust_name);
		} else if (this.type == FlowType.收银台跨店转卡手续费) {
			CashierTurnCard ctc = new CashierTurnCard();
			ctc.xx2(conn, this.getContent(), cust_name, gym, this.caAmt + "");
		} else if (this.type == FlowType.收银台预付定金) {
			JSONObject xx = this.content;
			String user_id = xx.getString("user_id");
			String use_amt = xx.getString("real_money");
			String pre_money = xx.getString("pre_money");
			Entity en = new EntityImpl("yp_prefee", conn);
			en.setTablename("yp_prefee_" + gym);
			en.setValue("gym", gym);
			en.setValue("mem_id", user_id);
			en.setValue("op_id", opId);
			BigDecimal f = new BigDecimal(pre_money);
			f = f.multiply(new BigDecimal(100));
			en.setValue("amt", f.intValue());
			f = new BigDecimal(use_amt);
			f = f.multiply(new BigDecimal(100));
			en.setValue("use_amt", f.intValue());
			en.setValue("create_time", new Date());
			en.setValue("state", "N");
			String dataId = en.create();
			en = new EntityImpl("yp_flow", conn);
			String sql = "update yp_flow_" + gym + " set data_id=? where flow_num=?";
			en.executeUpdate(sql, new String[] { dataId, flownum });

			// 发消息
			// 发消息
			JSONObject body = new JSONObject();
			body.put("pay", pre_money);
			body.put("use", use_amt);
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd HH:mm"));
		}

	}

	public Flow() {
		this(NEXT_COUNTER.getAndIncrement());
	}

	public Flow(String id, String gym, Connection conn) throws Exception {
		this.id = id;
		this.gym = gym;
		this.conn = conn;
		Entity yp_flow = new EntityImpl("yp_flow", conn);
		yp_flow.setTablename("yp_flow_" + gym);
		yp_flow.setValue("id", id);
		int size = yp_flow.search();
		if (size > 0) {
			this.pid = yp_flow.getStringValue("pid");
			this.empId = yp_flow.getStringValue("EMP_ID");
			this.empName = yp_flow.getStringValue("EMP_NAME");
			String _type = yp_flow.getStringValue("FLOW_TYPE");
			try {
				if ("散客购票".equals(_type)) {
					this.type = FlowType.散客购票;
				} else if ("教练端购课".equals(_type)) {
					this.type = FlowType.教练端购课;
				} else if ("购买团课课程".equals(_type)) {
					this.type = FlowType.购买团课课程;
				} else if ("购买私教课课程".equals(_type)) {
					this.type = FlowType.购买私教课课程;
				} else if ("会籍成交".equals(_type)) {
					this.type = FlowType.会籍成交;
				} else if ("购买会员卡".equals(_type)) {
					this.type = FlowType.购买会员卡;
				} else if ("付费请假".equals(_type)) {
					this.type = FlowType.付费请假;
				} else if ("账户充值".equals(_type)) {
					this.type = FlowType.账户充值;
				} else if ("收银台储物柜出租".equals(_type)) {
					this.type = FlowType.收银台储物柜出租;
				} else if ("收银台商品销售".equals(_type)) {
					this.type = FlowType.收银台商品销售;
				} else if ("健身房购买短信".equals(_type)) {
					this.type = FlowType.健身房购买短信;
				} else if ("收银台补卡".equals(_type)) {
					this.type = FlowType.收银台补卡;
				} else if ("收银台发卡押金收费".equals(_type)) {
					this.type = FlowType.收银台发卡押金收费;
				} else if ("会籍端预付定金".equals(_type)) {
					this.type = FlowType.会籍端预付定金;
				} else if ("收银台转卡手续费".equals(_type)) {
					this.type = FlowType.收银台转卡手续费;
				} else if ("收银台转课手续费".equals(_type)) {
					this.type = FlowType.收银台转课手续费;
				} else if ("收银台续卡".equals(_type)) {
					this.type = FlowType.收银台续卡;
				} else if ("续卡".equals(_type)) {
					this.type = FlowType.续卡;
				} else if ("升级".equals(_type)) {
					this.type = FlowType.升级;
				} else if ("收银台跨店转卡手续费".equals(_type)) {
					this.type = FlowType.收银台跨店转卡手续费;
				} else if ("收银台预付定金".equals(_type)) {
					this.type = FlowType.收银台预付定金;
				}
			} catch (Exception e) {
				// 没有找到匹配的报错
			}
			this.gdName = yp_flow.getStringValue("gd_name");
			this.actId = yp_flow.getStringValue("ACT_ID");
			this.memId = yp_flow.getStringValue("MEM_ID");
			this.userName = yp_flow.getStringValue("USER_NAME");
			this.phone = yp_flow.getStringValue("PHONE");
			this.cardNumber = yp_flow.getStringValue("CARD_NUMBER");
			this.flownum = yp_flow.getStringValue("FLOW_NUM");
			try {
				this.content = new JSONObject(yp_flow.getStringValue("CONTENT"));
			} catch (Exception e) {
			}

			String employee = yp_flow.getStringValue("employee");
			if (employee != null && "Y".equals(employee)) {
				this.employee = true;
			}

			this.caAmt = yp_flow.getLongValue("CA_AMT");
			this.realAmt = yp_flow.getLongValue("REAL_AMT");
			this.isChange = yp_flow.getBooleanValue("IS_CHG");
			this.chgRemark = yp_flow.getStringValue("CHG_REMARK");
			this.chgOpId = yp_flow.getStringValue("CHG_OP_ID");
			this.remainAmt = yp_flow.getLongValue("REMAIN_AMT");
			this.giftAmt = yp_flow.getLongValue("GIFT_AMT");
			this.remainGiftAmt = yp_flow.getLongValue("REMAIN_GIFT_AMT");
			this.cashAmt = yp_flow.getLongValue("CASH_AMT");
			this.wxAmt = yp_flow.getLongValue("WX_AMT");
			this.aliAmt = yp_flow.getLongValue("ALI_AMT");
			this.cardAmt = yp_flow.getLongValue("CARD_AMT");
			this.giftCardAmt = yp_flow.getLongValue("GIFT_CARD_AMT");
			this.giftCardNo = yp_flow.getStringValue("GIFT_CARD_NO");
			this.opId = yp_flow.getStringValue("OP_ID");
			this.opName = yp_flow.getStringValue("OP_NAME");
			this.op_time = yp_flow.getDateValue("OP_TIME");
			this.dataTableName = yp_flow.getStringValue("DATA_TABLE_NAME");
			this.dataId = yp_flow.getStringValue("DATA_ID");
			this.state = yp_flow.getStringValue("STATE");
			this.pay_time = yp_flow.getDateValue("PAY_TIME");
		} else {
			throw new Exception("系统没有查询到相应的流水信息");
		}
	}

	public Date getPay_time() {
		return pay_time;
	}

	public Flow(String flowNum, String gym, Connection conn, String type) throws Exception {
		this.flownum = flowNum;
		this.gym = gym;
		this.conn = conn;
		Entity yp_flow = new EntityImpl("yp_flow", conn);

		int size = yp_flow.executeQuery(" select * from yp_flow_" + gym + " where flow_num='" + flowNum + "'");
		if (size > 0) {
			this.pid = yp_flow.getStringValue("pid");
			this.empId = yp_flow.getStringValue("EMP_ID");
			this.empName = yp_flow.getStringValue("EMP_NAME");
			String _type = yp_flow.getStringValue("FLOW_TYPE");
			try {
				if ("散客购票".equals(_type)) {
					this.type = FlowType.散客购票;
				} else if ("教练端购课".equals(_type)) {
					this.type = FlowType.教练端购课;
				} else if ("购买团课课程".equals(_type)) {
					this.type = FlowType.购买团课课程;
				} else if ("购买私教课课程".equals(_type)) {
					this.type = FlowType.购买私教课课程;
				} else if ("会籍成交".equals(_type)) {
					this.type = FlowType.会籍成交;
				} else if ("购买会员卡".equals(_type)) {
					this.type = FlowType.购买会员卡;
				} else if ("付费请假".equals(_type)) {
					this.type = FlowType.付费请假;
				} else if ("账户充值".equals(_type)) {
					this.type = FlowType.账户充值;
				} else if ("收银台储物柜出租".equals(_type)) {
					this.type = FlowType.收银台储物柜出租;
				} else if ("收银台商品销售".equals(_type)) {
					this.type = FlowType.收银台商品销售;
				} else if ("健身房购买短信".equals(_type)) {
					this.type = FlowType.健身房购买短信;
				} else if ("收银台补卡".equals(_type)) {
					this.type = FlowType.收银台补卡;
				} else if ("收银台发卡押金收费".equals(_type)) {
					this.type = FlowType.收银台发卡押金收费;
				} else if ("会籍端预付定金".equals(_type)) {
					this.type = FlowType.会籍端预付定金;
				} else if ("收银台转卡手续费".equals(_type)) {
					this.type = FlowType.收银台转卡手续费;
				} else if ("收银台转课手续费".equals(_type)) {
					this.type = FlowType.收银台转课手续费;
				} else if ("收银台续卡".equals(_type)) {
					this.type = FlowType.收银台续卡;
				} else if ("续卡".equals(_type)) {
					this.type = FlowType.续卡;
				} else if ("升级".equals(_type)) {
					this.type = FlowType.升级;
				} else if ("收银台跨店转卡手续费".equals(_type)) {
					this.type = FlowType.收银台跨店转卡手续费;
				} else if ("收银台预付定金".equals(_type)) {
					this.type = FlowType.收银台预付定金;
				}
			} catch (Exception e) {
				// 没有找到匹配的报错
			}
			this.id = yp_flow.getStringValue("id");
			this.gdName = yp_flow.getStringValue("gd_name");
			this.actId = yp_flow.getStringValue("ACT_ID");
			this.memId = yp_flow.getStringValue("MEM_ID");
			this.userName = yp_flow.getStringValue("USER_NAME");
			this.phone = yp_flow.getStringValue("PHONE");
			this.cardNumber = yp_flow.getStringValue("CARD_NUMBER");
			try {
				this.content = new JSONObject(yp_flow.getStringValue("CONTENT"));
			} catch (Exception e) {
			}

			String employee = yp_flow.getStringValue("employee");
			if (employee != null && "Y".equals(employee)) {
				this.employee = true;
			}

			this.caAmt = yp_flow.getLongValue("CA_AMT");
			this.realAmt = yp_flow.getLongValue("REAL_AMT");
			this.isChange = yp_flow.getBooleanValue("IS_CHG");
			this.chgRemark = yp_flow.getStringValue("CHG_REMARK");
			this.chgOpId = yp_flow.getStringValue("CHG_OP_ID");
			this.remainAmt = yp_flow.getLongValue("REMAIN_AMT");
			this.giftAmt = yp_flow.getLongValue("GIFT_AMT");
			this.remainGiftAmt = yp_flow.getLongValue("REMAIN_GIFT_AMT");
			this.cashAmt = yp_flow.getLongValue("CASH_AMT");
			this.wxAmt = yp_flow.getLongValue("WX_AMT");
			this.aliAmt = yp_flow.getLongValue("ALI_AMT");
			this.cardAmt = yp_flow.getLongValue("CARD_AMT");
			this.giftCardAmt = yp_flow.getLongValue("GIFT_CARD_AMT");
			this.giftCardNo = yp_flow.getStringValue("GIFT_CARD_NO");
			this.opId = yp_flow.getStringValue("OP_ID");
			this.opName = yp_flow.getStringValue("OP_NAME");
			this.op_time = yp_flow.getDateValue("OP_TIME");
			this.dataTableName = yp_flow.getStringValue("DATA_TABLE_NAME");
			this.dataId = yp_flow.getStringValue("DATA_ID");
			this.state = yp_flow.getStringValue("STATE");
			this.pay_time = yp_flow.getDateValue("PAY_TIME");
		} else {
			throw new Exception("系统没有查询到相应的流水信息");
		}
	}


	public void update() throws Exception {
		if (this.id == null || "".equals(this.id)) {
			throw new Exception("未找到主键!");
		}
		Entity yp_flow = new EntityImpl("yp_flow", conn);
		yp_flow.setTablename("yp_flow_" + gym);
		yp_flow.setValue("id", this.id);
		yp_flow.setValue("pid", this.pid);
		yp_flow.setValue("EMP_ID", this.empId);
		yp_flow.setValue("EMP_NAME", this.empName);
		yp_flow.setValue("FLOW_TYPE", this.type + "");
		yp_flow.setValue("GD_NAME", this.gdName);
		yp_flow.setValue("ACT_ID", this.actId);
		yp_flow.setValue("MEM_ID", this.memId);
		yp_flow.setValue("USER_NAME", this.userName);
		yp_flow.setValue("PHONE", this.phone);
		yp_flow.setValue("CARD_NUMBER", this.cardNumber);// 会员卡号
		yp_flow.setValue("FLOW_NUM", this.flownum);// 流水号
		yp_flow.setValue("CONTENT", content.toString());// 消费详情
		yp_flow.setValue("CA_AMT", this.caAmt);// 应收金额
		yp_flow.setValue("REAL_AMT", this.realAmt);// 实收金额
		yp_flow.setValue("EMPLOYEE", employee ? "Y" : "N");// 是否员工
		yp_flow.setValue("IS_CHG", isChange ? "Y" : "N");// 手动修改金额
		yp_flow.setValue("CHG_REMARK", this.chgRemark);// 修改原因
		yp_flow.setValue("CHG_OP_ID", this.chgOpId);// 修改人ID
		yp_flow.setValue("REMAIN_AMT", this.remainAmt);// 剩余金额
		yp_flow.setValue("GIFT_AMT", this.giftAmt);// 消费赠金
		yp_flow.setValue("REMAIN_GIFT_AMT", this.remainGiftAmt);// 剩余赠金
		yp_flow.setValue("CASH_AMT", this.cashAmt);// 现金
		yp_flow.setValue("WX_AMT", this.wxAmt);// 微信
		yp_flow.setValue("ALI_AMT", this.aliAmt);// 支付宝
		yp_flow.setValue("CARD_AMT", this.cardAmt);// 会员余额
		yp_flow.setValue("CARD_CASH_AMT", this.cardCashAmt);// 刷卡
		yp_flow.setValue("GIFT_CARD_AMT", this.giftCardAmt);// 代金券
		yp_flow.setValue("GIFT_CARD_NO", this.giftCardNo);// 卷号
		yp_flow.setValue("OP_ID", this.opId);
		yp_flow.setValue("OP_NAME", this.opName);
		yp_flow.setValue("OP_TIME", this.op_time);
		yp_flow.setValue("DATA_TABLE_NAME", this.dataTableName);
		yp_flow.setValue("DATA_ID", this.dataId);
		yp_flow.setValue("STATE", this.state);
		if (this.pay_time != null) {
			yp_flow.setValue("PAY_TIME", this.pay_time);
		}
		yp_flow.update();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10000000; i++) {
			System.out.println(new Flow().getTrimedFlownum());
		}
	}

	private Flow(int andIncrement) {
		String x = new SimpleDateFormat("HHmmss").format(op_time);
		Calendar op = Calendar.getInstance();
		op.setTime(op_time);
		int year = op.get(Calendar.YEAR) - 2010;
		int month = op.get(Calendar.MONTH) + 1;
		int day = op.get(Calendar.DAY_OF_MONTH);
		int counter = andIncrement & LOW_ORDER_THREE_BYTES;
		this.flownum = Utils.int2hex(year) + Utils.int2hex(month) + Utils.int2hex(day) + x + toHexString(counter);
	}

	public String getPayBody() {
		return "" + this.getType();
	}

	public byte[] toByteArray(int counter) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		putToByteBuffer(buffer, counter);
		return buffer.array(); // using .allocate ensures there is a backing
		// array that can be returned
	}

	public String getDataTableName() {
		return dataTableName;
	}

	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}

	public String toHexString(int counter) {
		char[] chars = new char[4];
		int i = 0;
		for (byte b : toByteArray(counter)) {
			chars[i++] = HEX_CHARS[b >> 4 & 0xF];
			chars[i++] = HEX_CHARS[b & 0xF];
		}
		return new String(chars);
	}

	private void putToByteBuffer(ByteBuffer buffer, int counter) {
		// buffer.put(int2(counter));
		buffer.put(int1(counter));
		buffer.put(int0(counter));
	}

	private static byte int0(final int x) {
		return (byte) (x);
	}

	// private static byte int2(final int x) {
	// return (byte) (x >> 16);
	// }

	private static byte int1(final int x) {
		return (byte) (x >> 8);
	}

	public String getFlownum() {
		return flownum;
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

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGym() {
		return gym;
	}

	public void setGym(String gym) {
		this.gym = gym;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		if (Utils.isNull(empName)) {
			this.empName = "N/A";
			if (this.empId != null && this.empId.length() > 0) {
				Entity en = new EntityImpl(conn);
				try {
					int size = en.executeQuery("seleect a.emp_name from yp_emp a where a.id = ?", new String[] { this.empId });
					if (size > 0) {
						this.empName = en.getStringValue("emp_name");
					}
				} catch (Exception e) {
					Logger.error(e);
				}
			}
		}
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public FlowType getType() {
		return type;
	}

	public void setType(FlowType type) {
		this.type = type;
	}

	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	public String getMemId() {
		return memId;
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}

	public long getCaAmt() {
		return caAmt;
	}

	public void setCaAmt(long caAmt) {
		this.caAmt = caAmt;
	}

	public long getRealAmt() {
		return realAmt;
	}

	public void setRealAmt(long realAmt) {
		this.realAmt = realAmt;
	}

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public String getChgRemark() {
		return chgRemark;
	}

	public void setChgRemark(String chgRemark) {
		this.chgRemark = chgRemark;
	}

	public String getChgOpId() {
		return chgOpId;
	}

	public void setChgOpId(String chgOpId) {
		this.chgOpId = chgOpId;
	}

	public long getRemainAmt() {
		return remainAmt;
	}

	public void setRemainAmt(long remainAmt) {
		this.remainAmt = remainAmt;
	}

	public long getGiftAmt() {
		return giftAmt;
	}

	public void setGiftAmt(long giftAmt) {
		this.giftAmt = giftAmt;
	}

	public long getRemainGiftAmt() {
		return remainGiftAmt;
	}

	public void setRemainGiftAmt(long remainGiftAmt) {
		this.remainGiftAmt = remainGiftAmt;
	}

	public long getCashAmt() {
		return cashAmt;
	}

	public void setCashAmt(long cashAmt) {
		this.cashAmt = cashAmt;
	}

	public long getWxAmt() {
		return wxAmt;
	}

	public void setWxAmt(long wxAmt) {
		this.wxAmt = wxAmt;
	}

	public long getAliAmt() {
		return aliAmt;
	}

	public void setAliAmt(long aliAmt) {
		this.aliAmt = aliAmt;
	}

	public long getCardAmt() {
		return cardAmt;
	}

	public void setCardAmt(long cardAmt) {
		this.cardAmt = cardAmt;
	}

	public long getGiftCardAmt() {
		return giftCardAmt;
	}

	public void setGiftCardAmt(long giftCardAmt) {
		this.giftCardAmt = giftCardAmt;
	}

	public String getGiftCardNo() {
		return giftCardNo;
	}

	public void setGiftCardNo(String giftCardNo) {
		this.giftCardNo = giftCardNo;
	}

	public String getOpId() {
		return opId;
	}

	public void setOpId(String opId) {
		this.opId = opId;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public void setCaAmt(String price) {
		try {
			Float f = Float.parseFloat(price) * 100;
			this.setCaAmt(f.longValue());
		} catch (Exception e) {
		}
	}

	public String getGdName() {
		return gdName;
	}

	public void setGdName(String gdName) {
		this.gdName = gdName;
	}

	public long getCardCashAmt() {
		return cardCashAmt;
	}

	public void setCardCashAmt(long cardCashAmt) {
		this.cardCashAmt = cardCashAmt;
	}

	public boolean isEmployee() {
		return employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}
}
