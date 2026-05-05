local userKey = KEYS[1]
local eventKey = KEYS[2]
local eventUsersKey = KEYS[3]

local newVoteId = ARGV[1]
local newOptionId = ARGV[2]
local newUserId = ARGV[3]

local isDelete = ARGV[4]

local oldOptionId = redis.call('HGET', userKey, 'optionId')

-- DELETE
if isDelete == "true" then
    if oldOptionId ~= false and oldOptionId ~= nil then
        redis.call('HINCRBY', eventKey, "option:" .. oldOptionId, -1)
        redis.call('DEL', userKey)
        redis.call("SREM", eventUsersKey, userKey)
    end
    return "deleted"
end

-- CREATE
if oldOptionId == false or oldOptionId == nil then
    redis.call('HINCRBY', eventKey, "option:" .. newOptionId, 1)

    redis.call('HSET', userKey,
        'voteId', newVoteId,
        'optionId', newOptionId,
        'userId', newUserId
    )

    return "created"
end

-- UPDATE (option changed)
if oldOptionId ~= newOptionId then
    redis.call('HINCRBY', eventKey, "option:" .. oldOptionId, -1)
    redis.call('HINCRBY', eventKey, "option:" .. newOptionId, 1)

    redis.call('HSET', userKey,
        'voteId', newVoteId,
        'optionId', newOptionId,
        'userId', newUserId
    )

    return "updated"
end

-- SAME OPTION (for fallback if the user select exist vote already it goes to delete, this is for safety)
redis.call('HSET', userKey,
    'voteId', newVoteId,
    'optionId', newOptionId,
    'userId', newUserId
)

return "updated"