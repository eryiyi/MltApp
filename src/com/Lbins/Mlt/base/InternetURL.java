package com.Lbins.Mlt.base;

public class InternetURL {
    public static final String INTERNAL = "http://smilekyle.xicp.net:42765/";
//    public static final String INTERNAL = "http://www.zhlmlt.cn/";

    //mob
    public static final String APP_MOB_KEY = "159376f684b00";
    public static final String APP_MOB_SCRECT = "6045af454114d7edc997b1ffb4eb3a8b";

    public static final String WEIXIN_APPID = "wxe48c235e104c5332";
    public static final String WEIXIN_SECRET = "cd46a2cae4981a4fab91b2c3271052a1";
    public static final  String WX_API_KEY="cd46a2cae4981a4fab91b2c3271052aa";

    public static final String WEIXIN_partnerId = "1368485802";
    public static final String WEIXIN_MCH_ID = "1368485802";

    public static final String XUNFEI_APPID = "57a1557b";

    public static final String QINIU_URL = "http://7xqzj9.com1.z0.glb.clouddn.com/";

    public static final String QINIU_SPACE = "hmmm-pic";
    //多媒体文件上传接口
    public static final String UPLOAD_FILE = INTERNAL + "uploadImage.do";
    public static final String UPLOAD_TOKEN = INTERNAL + "token.json";
    //1登陆
    public static final String LOGIN__URL = INTERNAL + "memberLogin.do";
    //2根据用户id获得用户信息
    public static final String GET_MEMBER_URL = INTERNAL + "getMemberInfoById.do";
    //3获得所有省份
    public static final String GET_PROVINCE_URL = INTERNAL + "getProvince.do";
    //4获得城市
    public static final String GET_CITY_URL = INTERNAL + "getCity.do";
    //5获得地区
    public static final String GET_COUNTRY_URL = INTERNAL + "getCountry.do";
    //6注册
    public static final String REG_URL = INTERNAL + "memberRegister.do";
    //7发布的求购供应信息列表
    public static final String GET_RECORD_LIST_URL = INTERNAL + "recordList.do";
    //8保存求购供应信息
    public static final String SEND_RECORD_URL = INTERNAL + "sendRecord.do";
    //9获得求购供应详细信息
    public static final String GET_RECORD_BY_ID_URL = INTERNAL + "getRecordById.do";
    //10.分享求购供应信息
    public static final String VIEW_RECORD_BYID_URL = INTERNAL + "viewRecord.do";
    //11根据用户id获得用户发布的信息
    public static final String GET_RECORDS_BYID_URL = INTERNAL + "recordListById.do";
    //上传经纬度
    public static final String SEND_LOCATION_BYID_URL = INTERNAL + "sendLocation.do";
    //获得服务信息
    public static final String GET_FUWU_MSG_BY_LOCATION_URL = INTERNAL + "getFuwuByLocationAndType.do";
    //
    public static final String GET_FUWU_BY_LOCATION_URL = INTERNAL + "getFuwuType.do";
    //获得关于我们
    public static final String GET_ABOUT_US_URL = INTERNAL + "getAboutUs.do";
    //添加意见
    public static final String ADD_SUGGEST_URL = INTERNAL + "suggest/addSuggest.do";
    //修改密码
    public static final String UPDATE_PWR_URL = INTERNAL + "updatePwrApp.do";
    //获得用户广告图
    public static final String GET_AD_URL = INTERNAL + "getEmpAd.do";
    //获得附近商家
    public static final String GET_NEARBY_URL = INTERNAL + "getNearby.do";
    //产品推荐
    public static final String GET_TOP_URL = INTERNAL + "getTopMsg.do";
    //获得vip列表
    public static final String GET_VIP_URL = INTERNAL + "getVipList.do";
    //获得等级详情
    public static final String GET_VIP_DETAIL_URL = INTERNAL + "getLevelById.do";
    //添加举报
    public static final String ADD_REPORT_URL = INTERNAL + "saveReport.do";
    //查询收藏信息
    public static final String LIST_FAVOUR_URL = INTERNAL + "getFavourById.do";
    //保存收藏信息
    public static final String ADD_FAVOUR_URL = INTERNAL + "saveFavour.do";
    //获得客服电话
    public static final String GET_TEL_URL = INTERNAL + "getKefuTel.do";
    //获得收藏数量
    public static final String GET_FAVOUR_COUNT_URL = INTERNAL + "getFavourCount.do";
    //删除收藏
    public static final String DELETE_FAVOUR_URL = INTERNAL + "deleteFavour.do";
    //查詢廣告
    public static final String GET_AD_LOGIN_URL = INTERNAL + "getLoginAd.do";
    //完善个人资料
    public static final String UPDATE_PROFILE_URL = INTERNAL + "memberUpdateProfile.do";
    //查询排行榜
    public static final String GET_PAIHANG_URL = INTERNAL + "getPaihangs.do";
    //获得微信客服列表
    public static final String GET_WEIXINS_URL = INTERNAL + "getKefuWeixin.do";
    //保存关注区域
    public static final String SAVE_GUANZHU_URL = INTERNAL + "saveGuanzhuArea.do";
    //查询关注区域
    public static final String GET_GUANZHU_URL = INTERNAL + "getGuanzhuArea.do";
    //查询公告
    public static final String GET_NOTICE_URL = INTERNAL + "getNotices.do";
    //公告详情
    public static final String GET_NOTICE_DETAIL_URL = INTERNAL + "getNoticesDetail.do";
    //根据手机号查询用户是否存在
    public static final String GET_EMP_MOBILE = INTERNAL + "getMemberByMobile.do";
    //百度推送
    public static final String UPDATE_PUSH_ID = INTERNAL + "updatePushId.do";
    //二维码分享页面
    public static final String SHARE_URL_ID = INTERNAL + "html/download.html";
    //查询附近距离设置
    public static final String GET_NEARBY_DISTANCE_URL = INTERNAL + "getNearbyDistance.do";
    //获得热门城市
    public static final String GET_HOT_CITY_URL = INTERNAL + "getHotCitys.do";
    //上传公司地址
    public static final String ADD_COMPANY_ADDRESS = INTERNAL + "addCompanyLocation.do";
    //分享明信片
    public static final String SHARE_MINGXINPIAN_URL = INTERNAL + "toShareMp.do";
    //上传头像
    public static final String UPDATE_COVER = INTERNAL + "uploadCover.do";

    //传订单给服务端--生成订单
    public static final String SEND_ORDER_TOSERVER = INTERNAL + "orderSave.do";
    public static final String SEND_ORDER_TOSERVER_WX = INTERNAL + "orderSaveWx.do";

    //查询短信平台 商业银行的网址等
    public static final String GET_NET_BY_TYPE_URL = INTERNAL + "getNetwwwObjByType.do";

    //更新订单状态
    public static final String UPDATE_ORDER_TOSERVER = INTERNAL + "orderUpdate.do";
    //微信统一下单notify_url
    public static final String WEIXIN_NOTIFY_URL = INTERNAL + "orderSaveWxFk.do";
    //获取热门搜索词汇
    public static final String GET_HOT_WORD_URL = INTERNAL + "getHotWord.do";

    //广告语
    public static final String MANAGER_MSG_AD_URL = INTERNAL + "listsMsgAds.do";
}
