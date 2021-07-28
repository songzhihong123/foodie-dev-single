--
-- Created by IntelliJ IDEA.
-- User: hasee
-- Date: 2021/1/15
-- Time: 14:53
-- To change this template use File | Settings | File Templates.
--

-- 获取方法签名特征
local methonKey = KEYS[1]
redis.log(redis.LOG_DEBUG, 'key is',methonKey)

-- 调用脚本的传入的限流大小
local limit = tonumber(ARGV[1])

-- 获取当前流量大小
local count = tonumber(redis.call('get',methonKey) or "0")

-- 判断是否超出限流阈值
if count + 1 > limit then
    -- 拒绝服务访问
    return false
else
    -- 没有超出阈值
    --设置当前访问的数量加1
    redis.call("INCRBY", methonKey,1);
    redis.call("EXPIRE",methonKey,1);
    return true

end