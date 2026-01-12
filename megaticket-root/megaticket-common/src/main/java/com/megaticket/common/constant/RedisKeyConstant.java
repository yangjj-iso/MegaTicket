package com.megaticket.common.constant;


/**
 * Redis Key常量管理类
 * author Yang JunJie
 * since 2026/1/12
 */
public class RedisKeyConstant {

    /*
    影院类Key
    */

    /**
     * 座位位图 Key
     * 格式: cinema:seat_map:{scheduleId}
     * 示例: cinema:seat_map:2024100101
     */
    public static final String SEAT_MAP_PREFIX="cinema:seat_map:";

    /**
     * 影院排期 Key
     * 格式: cinema:schedule_detail:{scheduleId}
     * 示例: cinema:schedule_detail:20260112
     */
    public static final String SCHEDULE_DETAIL_PREFIX="cinema:schedule_detail:";

    /**
     * 用户Token Key
     * 格式: cinema:user_token:{uuid}
     * 示例: cinema:user_token:20060726
     */
    public static final String USER_TOKEN_PREFIX="cinema:user_token:";
}
