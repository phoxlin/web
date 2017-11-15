/*
Translation by FreezeSoul
*/
scheduler.config.day_date="%M %d日 %D";
scheduler.config.default_date="%Y年 %M %d日";
scheduler.config.month_date="%Y年 %M";

scheduler.locale={
	date: {
		month_full: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
		month_short: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
		day_full: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
		day_short: ["日", "一", "二", "三", "四", "五", "六"]
	},
	labels: {
		dhx_cal_today_button: "今天",
		day_tab: "日",
		week_tab: "周",
		month_tab: "月",
		new_event: "新建团课计划",
		icon_save: "保存",
		icon_cancel: "关闭",
		icon_details: "详细",
		icon_edit: "编辑",
		icon_delete: "删除",
		confirm_closing: "请确认是否撤销修改!", //Your changes will be lost, are your sure?
		confirm_deleting: "是否删除日程?",
		
		
		/**
		 * name	code	data type	length	precision	primary	foreign key	mandatory
			提醒类型	reminder_type	varchar(20)	20		false	false	true
			提醒月份	reminder_month	int			false	false	true
			提醒号数	reminder_day	int			false	false	true
			提醒星期	reminder_week	int			false	false	true
			提醒时间	reminder_time	datetime			false	false	false
			参与者	    participators	varchar(1000)	1000		false	false	false
			参与角色	role_codes	varchar(1000)	1000		false	false	false
			消息类型	msg_type	varchar(20)	20		false	false	true
		 */
		
		section_time: "时间范围",
		section_title:"标题",
		section_sch_type:"日程类型",
		section_description:"内容",
		section_reminder_type:"重复类型",
		section_reminder_month:"按月份",
		section_reminder_day:"按天",
		section_reminder_week:"按星期",

		
		
		full_day: "整天",

		confirm_recurring:"请确认是否将日程设为重复模式?",
		section_recurring:"重复周期",
		button_recurring:"禁用",
		button_recurring_open:"启用",
		button_edit_series: "编辑系列",
		button_edit_occurrence: "编辑实例",
		
		/*agenda view extension*/
		agenda_tab:"议程",
		date:"日期",
		description:"说明",
		
		/*year view extension*/
		year_tab:"今年",

		/*week agenda view extension*/
		week_agenda_tab: "议程",

		/*grid view extension*/
		grid_tab:"电网"
	}
};

