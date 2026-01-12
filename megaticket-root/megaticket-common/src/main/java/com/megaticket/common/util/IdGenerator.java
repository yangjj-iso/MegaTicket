package com.megaticket.common.util;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 分布式ID生成器工具类
 * author Yang JunJie
 * since 2026/1/12
 */
@Component
@Slf4j
@Getter
public class IdGenerator {

    private long workerId=0;
    private long datacenterId=1;
    //防止 NPE
    private Snowflake snowflake= IdUtil.getSnowflake(workerId,datacenterId);

    /**
     * 初始化方法，计算workerId并创建Snowflake实例
     * param null
     * return null
     */
    @PostConstruct
    public void init(){
        try {
            // 1.尝试从机器IP地址计算workerId
            workerId= NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            workerId=workerId%32;

            log.info("当前机器 workerId:{}",workerId);
            // 2.设置datacenterId,简单处理设置为 1

            // 3.重新创建Snowflake实例
            snowflake=IdUtil.getSnowflake(workerId,datacenterId);
        }catch (Exception e){
            log.warn("获取机器IP失败，使用默认workerId:0",e);
            snowflake=IdUtil.getSnowflake(0,datacenterId);
        }
    }

    /**
     * 获取下一个ID
     * param null
     * return 下一个ID
     */
    public synchronized long nextId(){
        return snowflake.nextId();
    }

    /**
     * 获取下一个ID(String)
     * param null
     * return 下一个ID字符串
     */
    public synchronized String nextIdStr(){
        return String.valueOf(snowflake.nextId());
    }
}


