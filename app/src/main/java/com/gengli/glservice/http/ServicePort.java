package com.gengli.glservice.http;

public class ServicePort {
    //测试地址
//    private static final String API = "http://gengli.test.dxkj.com/api/test/";
    //正式地址
    private static final String API = "http://gengli.test.dxkj.com/api/";
    //假数据地址
//    private static final String API = "http://gengli.test.dxkj.com/api/test/index/data?qt=";
    /**
     * 客户端启动
     */
    public static String GET_CLIENT_RUN = API + "client/run/khd";
    /**
     * 登录
     */
    public static String ACCOUNT_LOGIN_COMPANY = API + "account/login_company";


    /**
     * 手机号登录
     */
    public static String ACCOUNT_LOGIN_MOBILE = API + "account/login_mobile";

    /**
     * 提交报修单
     */
    public static String REPAIR_ADD = API + "repair/add";

    /**
     * 提交意见反馈
     */
    public static String FEEDBACK_ADD = API + "feedback/add";

    /**
     * 设为默认联系人
     */
    public static String  CONTACT_SET_DEFAULT = API + "contact/set_default";

    /**
     * 联系人列表
     */
    public static String CONTACT_LISTS = API + "contact/lists";


    /**
     * 删除联系人
     */
    public static String CONTACT_REMOVE = API + "contact/remove";

    /**
     * 新建联系人
     */
    public static String CONTACT_ADD = API + "contact/add";

    /**
     * 修改联系人
     */
    public static String CONTACT_MODIFY = API + "contact/modify";

    /**
     * 维修单评价
     */
    public static String REPAIR_COMMENT_ADD = API + "repair/comment_add";

    /**
     * 修改信息
     */
    public static String ACCOUNT_MODIFY = API + "account/modify";


    /**
     * 退出登录
     */
    public static String ACCOUNT_LOGOUT = API + "account/logout";
    /**
     * 获取验证码
     */
    public static String DATA_VERIFY = API + "data/verify";
    /**
     * 获取图形验证码
     */
    public static String DATA_CAPTCHA = API + "data/captcha";
    /**
     * 收藏列表
     */
    public static String FAV_LISTS = API + "fav/lists";
    /**
     * 添加收藏
     */
    public static String FAV_ADD = API + "fav/add";
    /**
     * 删除收藏
     */
    public static String FAV_REMOVE = API + "fav/remove";
    /**
     * 获取产品系列
     */
    public static String PRODUCT_CATEGORY = API + "product/category";

    /**
     * 获取产品列表
     */
    public static String PRODUCT_LISTS = API + "product/lists";

    /**
     * 获取产品详情
     */
    public static String PRODUCT_DETAIL = API + "product/detail";
    /**
     * 添加常用设备
     */
    public static String USER_PRODUCT_ADD = API + "user/product_add";

    /**
     * 删除常用设备
     */
    public static String USER_PRODUCT_REMOVE = API + "user/product_remove";


    /**
     * 获取常用设备
     */
    public static String USER_PRODUCT = API + "user/product";

    /**
     * 获取购买记录
     */
    public static String USER_ORDER = API + "user/order";

    /**
     * 获取浏览记录列表
     */
    public static String LOG_ARCHIVE = API + "log/archive";

    /**
     * 浏览记录删除，清空
     */
    public static String LOG_ARCHIVE_REMOVE = API + "log/archive_remove";

    /**
     * 指南文章列表
     */
    public static String ARCHIVE_LISTS = API + "archive/lists";

    /**
     * 文章详情页
     */
    public static String ARCHIVE_DETAIL = API + "archive/detail";

    /**
     * 获取报修单列表
     */
    public static String REPAIR_LISTS = API +"repair/lists";

    /**
     *配件详情
     */
    public static String REPAIR_DETAIL = API +"repair/detail";

    /**
     * 售后服务
     */
    public static String PRODUCT_SERVICE = API +"product/service";

    /**
     * 消息
     */
    public static String MSG_LISTS = API +"msg/lists";

    /**
     * 申请加急
     */
    public static String  REPAIR_EMERG = API +"repair/emerg";

    /**
     * 检查更细
     */
    public static String  CLIENT_UPDATE = API +"client/update";


}
