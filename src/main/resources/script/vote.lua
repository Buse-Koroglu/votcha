local userKey = KEYS[1]
local eventKey = KEYS[2]
local eventUsersKey = KEYS[3]

local newOption = ARGV[1]

local oldOption = redis.call('GET', userKey)

if newOption == "null" then
    if oldOption then
        redis.call('HINCRBY', eventKey, "option:" .. oldOption, -1)
        redis.call('DEL', userKey)
        redis.call("SREM", eventUsersKey, userKey)
    end
    return "deleted"
end

if not oldOption then
    redis.call('HINCRBY', eventKey, "option:" .. newOption, 1)
    redis.call('SET', userKey, newOption)
    redis.call("SADD", eventUsersKey, userKey)
    return "created"
end

if oldOption ~= newOption then
    redis.call('HINCRBY', eventKey, "option:" .. oldOption, -1)
    redis.call('HINCRBY', eventKey, "option:" .. newOption, 1)
    redis.call('SET', userKey, newOption)
    return "updated"
end

return "not_changed"