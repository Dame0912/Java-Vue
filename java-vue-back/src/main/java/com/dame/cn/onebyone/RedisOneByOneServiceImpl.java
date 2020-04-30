package com.dame.cn.onebyone;

import com.dame.cn.beans.consts.RedisConst;
import com.dame.cn.beans.response.BizException;
import com.dame.cn.beans.response.ResultCode;
import com.dame.cn.config.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

@Service
@Slf4j
public class RedisOneByOneServiceImpl extends RedisClient implements OneByOneService {

    /**
     * 存储key
     */
    private static final ThreadLocal<String> desKey = new ThreadLocal<>();
    /**
     * 存储value
     */
    private static final ThreadLocal<String> desVal = new ThreadLocal<>();
    /**
     * 插入成功
     */
    private static final ThreadLocal<Boolean> insertSuccess = new ThreadLocal<>();


    @Override
    public <T> T execute(OneByOne oneByOne, CallBack<T> callBack) {
        desKey.set(RedisConst.ONE_BY_ONE_KEY + oneByOne.getBizId());
        desVal.set(oneByOne.getBizType() + "-" + oneByOne.getMethod());
        try {
            // 回调前置
            this.beforeInvoke(oneByOne);
            // 处理逻辑
            return callBack.invoke();
        } finally {
            // 回调后置
            this.afterInvoke(oneByOne);
        }
    }

    /**
     * 回调前置
     */
    private void beforeInvoke(final OneByOne oneByOne) {
        Jedis jedis = jedisPool.getResource();
        try {
            insertSuccess.set(true);
            String val = jedis.get(desKey.get());
            if (!StringUtils.isEmpty(val)) {
                insertSuccess.set(false);
                log.warn("【并发控制】-->:" + desKey.get() + "业务正在处理中! ");
                throw new BizException(ResultCode.MULTIPLE_INVOKE_ERROR);
            } else {
                Long result = jedis.setnx(desKey.get(), desVal.get());
                if (result == 1) {
                    log.info("【并发控制】-->:向Redis中成功新增键值对，KEY=[" + desKey.get() + "],VALUE=[" + desVal.get() + "]");
                } else {
                    log.error("【并发控制】-->:向Redis中存入key=" + desKey.get() + ",val=" + desVal.get() + "失败！");
                    throw new BizException(ResultCode.SERVER_ERROR);
                }
            }
        } catch (Throwable t) {
            insertSuccess.set(false);
            log.error("【并发控制】-->:" + desKey.get() + "插入Redis处理记录失败！", t);
            throw new BizException(ResultCode.SERVER_ERROR);
        } finally {
            jedis.close();
        }
    }

    /**
     * 回调后置
     */
    private void afterInvoke(final OneByOne oneByOne) {
        Jedis jedis = jedisPool.getResource();
        try {
            if (!insertSuccess.get()) {
                return;
            }
            jedis.del(desKey.get());
            log.info("【并发控制】-->:从Redis中成功删除键值对，KEY=[" + desKey.get() + "],VALUE=[" + desVal.get() + "]");
        } catch (Throwable t) {
            log.error("【并发控制】-->:" + desKey.get() + "删除Redis处理记录失败！", t);
        } finally {
            desKey.set(null);
            desVal.set(null);
            insertSuccess.set(null);
            jedis.close();
        }
    }

}
