-- 模拟限流(假的)

-- 用作限流的Key
local key = 'My Key'

--限流的阈值=2
local limit = 2

-- 当前流量大小
local currentLimit = 0

--超出限流标准
if currentLimit + 1 > limit then
    print 'reject'
    return false
else
    print 'accept'
    return true

end

